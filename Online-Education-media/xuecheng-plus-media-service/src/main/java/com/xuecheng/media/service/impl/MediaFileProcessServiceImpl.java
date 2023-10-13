package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessHistoryMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.model.po.MediaProcessHistory;
import com.xuecheng.media.service.MediaFileProcessService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description
 * @date 2023/10/13 10:25
 */
public class MediaFileProcessServiceImpl implements MediaFileProcessService {

    @Autowired
    MediaProcessMapper mediaProcessMapper;
    @Autowired
    MediaFilesMapper mediaFilesMapper;
    @Autowired
    MediaProcessHistoryMapper mediaProcessHistoryMapper;
    /**
     * 查询待处理任务列表
     * @param shardIndex 分片序号
     * @param shardTotal 分片总数
     * @param count 获取记录数
     * @return
     */
    @Override
    public List<MediaProcess> selectMediaProcessList(int shardIndex, int shardTotal, int count) {
        List<MediaProcess> mediaProcesses = mediaProcessMapper.selectListByShardIndex(shardTotal, shardIndex, count);
        return mediaProcesses;
    }

    /**
     * 乐观锁 开启任务
     * @param id 任务id
     * @return
     */
    @Override
    public boolean startTask(long id) {
       int result =  mediaProcessMapper.startTask(id);
       return result <=0? false :true;
    }

    /**
     * 更新任务状态
     * @param tashId 任务id
     * @param status 任务状态
     * @param fileId 媒体文件id
     * @param url
     * @param errorMsg 错误信息
     */
    @Override
    public void saveProcessFinishStatus(Long tashId, String status, String fileId, String url, String errorMsg) {
        //查询任务是否存在
        MediaProcess mediaProcess = mediaProcessMapper.selectById(tashId);
        if (mediaProcess == null){
            return; //结束方法
        }
        //构造lambda表达式
        LambdaQueryWrapper<MediaProcess> lamQuery = new LambdaQueryWrapper<>();
        lamQuery.eq(MediaProcess::getId,tashId);
        //查看任务状态 任务失败 更新任务处理结果
        if (status.equals("3")){
            mediaProcess.setStatus("3");
            mediaProcess.setErrormsg(errorMsg);
            mediaProcess.setCreateDate(LocalDateTime.now());
            mediaProcess.setFailCount(mediaProcess.getFailCount()+1);
            //根据lambda表达式更新
            mediaProcessMapper.update(mediaProcess,lamQuery);
            return;
        }

       //任务处理成功,更新相应信息
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
        if (mediaFiles!= null){
            //更新媒体文件的url
            mediaFiles.setUrl(url);
            mediaFilesMapper.updateById(mediaFiles);
        }
        //更新MediaProcess的信息
        mediaProcess.setStatus("2");
        mediaProcess.setUrl(url);
        mediaProcess.setFinishDate(LocalDateTime.now());
        mediaProcessMapper.updateById(mediaProcess);

        //添加到历史记录
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess,mediaProcessHistory);
        mediaProcessHistoryMapper.insert(mediaProcessHistory);
        //删除原来的待处理任务列表
        mediaProcessMapper.deleteById(mediaProcess.getId());
    }


}
