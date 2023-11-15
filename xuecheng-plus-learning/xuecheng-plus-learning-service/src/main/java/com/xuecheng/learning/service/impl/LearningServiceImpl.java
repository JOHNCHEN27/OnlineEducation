package com.xuecheng.learning.service.impl;

import com.xuecheng.base.exception.OnlieEducationException;
import com.xuecheng.base.result.RestResponse;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.feignclient.MediaServiceClient;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.service.LearningService;
import com.xuecheng.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author LNC
 * @version 1.0
 * @description
 * @date 2023/11/14 13:28
 */
@Service
@Slf4j
public class LearningServiceImpl implements LearningService {
    @Autowired
    ContentServiceClient contentServiceClient;

    @Autowired
    MyCourseTablesService myCourseTablesService;

    @Autowired
    MediaServiceClient mediaServiceClient;

    /**
     * 获取视频
     * @param userId 用户id
     * @param courseId 课程id
     * @param teachplanId 课程计划id
     * @param mediaId 视频文件id
     * @return
     */
    @Override
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId) {
        //查询课程信息
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        if (coursepublish == null){
            OnlieEducationException.cast("课程信息不存在");
        }
        //校验学习资格

        //如果登录
        if (StringUtils.isNotEmpty(userId)){
            //判断是否选课
            XcCourseTablesDto courseTablesDto = myCourseTablesService.getLearningStatus(userId, courseId);
            //学习资格状态：code：702001 desc:正常学习
            String learnStatus =courseTablesDto.getLearnStatus();
            if (learnStatus.equals("702001")){
                return mediaServiceClient.getPlayUrlByMediaId(mediaId);
            }else if (learnStatus.equals("702003")){
                RestResponse.validfail("你的选课已过期需要申请续期或重新支付");
            }
        }
        //未登录或未选课判断是否收费
        String charge = coursepublish.getCharge();
        if (charge.equals("201000")){
            //免费可以正常学习
            return mediaServiceClient.getPlayUrlByMediaId(mediaId);
        }
        return RestResponse.validfail("请购买课程后继续学习");
    }
}
