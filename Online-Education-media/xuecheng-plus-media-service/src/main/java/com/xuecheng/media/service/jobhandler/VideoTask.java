package com.xuecheng.media.service.jobhandler;

import com.xuecheng.base.utils.Mp4VideoUtil;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author LNC
 * @version 1.0
 * @description 视频处理
 * @date 2023/10/13 18:42
 */
@Slf4j
@Component
public class VideoTask {
    @Autowired
    MediaFileService mediaFileService;
    @Autowired
    MediaFileProcessService mediaFileProcessService;

    @Value("${videoprocess.ffmpegpath}")
    String ffmpegpath;


    /**
     * 视频处理任务
     * @throws Exception
     */
    @XxlJob("videoJobHandler")
    public void videoJobHandler() throws Exception{
          //获取分片参数
        int shardIndex = XxlJobHelper.getShardIndex(); //执行器序号 从0开始
        int shardTotal = XxlJobHelper.getShardTotal(); //执行器总数

        //确定CPU核心数（几核）
        int processors = Runtime.getRuntime().availableProcessors();
        //查询待处理的任务 限制个数为当前主机允许的线程数
        List<MediaProcess> mediaProcessesList = mediaFileProcessService.selectMediaProcessList(shardIndex, shardTotal, processors);

        //获取任务数量
        int size = mediaProcessesList.size();
        log.debug("取到视频处理任务数");
        if (size <= 0){
            return;
        }

        //创建线程池 线程池数量为当前任务数
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        //使用计数器 来保证线程的运行
        CountDownLatch countDownLatch = new CountDownLatch(size);
        mediaProcessesList.forEach( mediaProcess -> {
            //对列表中的每个任务进行处理 加入线程池
            executorService.execute(()-> {
            try {
                //任务id
                Long taskId = mediaProcess.getId();
                //获取md5 文件id就是md5
                String fileId = mediaProcess.getFileId();
                //开启任务
                boolean b = mediaFileProcessService.startTask(taskId);
                if (!b) {
                    log.debug("抢占任务失败,任务id{}", taskId);
                    return;
                }

                //拿到minio的bucket
                String bucket = mediaProcess.getBucket();
                //objectName
                String objectName = mediaProcess.getFilePath();

                //下载minio视频到本地
                File file = mediaFileService.downloadFileFromMinIO(bucket, objectName);
                if (file == null) {
                    log.debug("下载视频出错,任务id:{},bucket:{},objectName:{}", taskId, bucket, objectName);
                    //保存任务处理失败的结果
                    mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "下载待处理文件失败");
                    return;
                }
                //源视频的路径
                String videoPath = file.getAbsolutePath();
                //转换后mp4文件的名称
                String mp4Name = fileId + ".mp4";
                //转换后的mp4文件路径
                //先创建一个临时文件
                File mp4File = null;
                try {
                    //创建临时文件
                    mp4File = File.createTempFile("minio", ".mp4");
                } catch (IOException e) {
                    log.debug("创建临时文件异常,{}", e.getMessage());
                    //保存任务处理失败的结果
                    mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "创建临时文件mp4失败");
                    return;
                }
                String mp4FilePath = mp4File.getAbsolutePath();
                //创建工具类对象
                Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegpath, videoPath, mp4Name, mp4FilePath);
                //开始视频转换，成功将返回success 返回返回失败原因
                String result = videoUtil.generateMp4();
                if (!result.equals("success")) {
                    log.debug("视频转码失败，原因:{},bucket:{},objectName:{}", result, bucket, objectName);
                    mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, result);
                    return;
                }
                //转码成功上传到minio
                boolean b1 = mediaFileService.addMediaFilesToMinIO(mp4FilePath, "video/mp4", bucket, objectName);
                if (!b1) {
                    log.debug("转码之后的视频上传到minio失败,taskid:{}", taskId);
                    mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "上传视频到minio失败");
                    return;
                }
                //获取mp4文件的url
                String url = getFilePath(fileId, ".mp4");
                //更新任务状态为成功
                mediaFileProcessService.saveProcessFinishStatus(taskId, "2", fileId, url, "创建临时文件异常");
            } finally {
                //计算器减一
                countDownLatch.countDown();
            }
            });
        });
        //阻塞，防止断电等异常情况 设置最大阻塞时间为30分钟（最多等三十分钟）
        countDownLatch.await(30, TimeUnit.MINUTES);
    }

    /**
     * 合并文件的路径
     * @param fileMd5
     * @param fileExt
     * @return
     */
    public String getFilePath(String fileMd5,String fileExt) {
        return fileMd5.substring(0,1) +"/" + fileMd5.substring(1,2) + "/" +fileMd5 +"/" +fileMd5+fileExt;
    }
}
