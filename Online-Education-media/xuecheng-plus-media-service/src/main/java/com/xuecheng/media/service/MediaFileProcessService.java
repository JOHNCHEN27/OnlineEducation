package com.xuecheng.media.service;

import com.xuecheng.media.model.po.MediaProcess;

import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description 任务处理接口
 * @date 2023/10/13 10:23
 */
public interface MediaFileProcessService {

    /**
     * 查询待处理任务列表
     * @param shardIndex 分片序号
     * @param shardTotal 分片总数
     * @param count 获取记录数
     * @return
     */
    List<MediaProcess> selectMediaProcessList(int shardIndex,int shardTotal,int count);

    /**
     * 开启任务 用数据库实现分布式锁
     * @param id 任务id
     * @return
     */
    boolean startTask(long id);

    /**
     * 更新任务状态
     * @param tashId 任务id
     * @param status 任务状态
     * @param fileId 媒体文件id
     * @param url
     * @param errorMsg 错误信息
     */
    void saveProcessFinishStatus(Long tashId, String status, String fileId, String url,String errorMsg);
}
