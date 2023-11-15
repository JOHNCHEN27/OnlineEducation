package com.xuecheng.learning.service;

import com.xuecheng.base.result.RestResponse;

/**
 * @author LNC
 * @version 1.0
 * @description 学习过程管理Service接口
 * @date 2023/11/14 13:24
 */
public interface LearningService {

    /**
     * 获取教学视频
     * @param userId 用户id
     * @param courseId 课程id
     * @param teachplanId 课程计划id
     * @param mediaId 视频文件id
     * @return
     */
    RestResponse<String> getVideo(String userId,Long courseId,Long teachplanId,String mediaId);
}
