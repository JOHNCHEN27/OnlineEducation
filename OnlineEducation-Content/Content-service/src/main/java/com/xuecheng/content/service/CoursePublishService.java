package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;

import java.io.File;

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

    //根据课程id、机构id进行课程发布
    void coursePublish(Long companyId, Long courseId);

    //课程静态化
    public File generateCourseHtml(Long courseId);

    //上传课程静态化页面
    public void uploadCourseHtml(Long courseId,File file);
}
