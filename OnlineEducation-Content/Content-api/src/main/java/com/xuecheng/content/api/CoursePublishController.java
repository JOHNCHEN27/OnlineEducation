package com.xuecheng.content.api;

import com.xuecheng.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author LNC
 * @version 1.0
 * @description 课程发布接口
 * @date 2023/10/16 15:40
 */
@Api(value = "课程发布接口", tags = "课程发布接口")
@RestController
public class CoursePublishController {

    @Autowired
    CoursePublishService coursePublishService;

    /**
     * 查询课程预览基本信息
     * @param courseId
     * @return
     */
    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") Long courseId){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("model",coursePublishService.getCoursePreviewInfo(courseId));
        modelAndView.setViewName("course_template");
        return modelAndView;
    }

    /**
     * 提交课程进行审核
     * @param courseId
     */
    @ApiOperation(value = "提交课程审核接口")
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit (@PathVariable("courseId") Long courseId){
        Long companyId = 1232141425L;
        coursePublishService.commitAudit(companyId,courseId);
    }

    /**
     * 课程发布接口
     * @param courseId
     */
    @ApiOperation(value = "课程发布接口")
    @PostMapping("/coursepublish/{courseId}")
    public void coursePublish(@PathVariable("courseId") Long courseId){
        Long companyId = 1232141425L;
        coursePublishService.coursePublish(companyId,courseId);
    }
}
