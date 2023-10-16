package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;

/**
 * @author LNC
 * @version 1.0
 * @description 课程预览、课程发布接口
 * @date 2023/10/16 15:55
 */
public interface CoursePublishService {

    /**
     * 获取课程预览信息
     * @param courseId 课程id
     * @return
     */
    CoursePreviewDto getCoursePreviewInfo(Long courseId);

    //根据机构id和课程id 进行课程审核
    void  commitAudit(Long companyId, Long courseId);
}
