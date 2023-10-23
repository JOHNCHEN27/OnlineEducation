package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LNC
 * @version 1.0
 * @description 视频课程信息
 * @date 2023/10/16 18:26
 */
@Api(value = "课程公开查询接口", tags = "课程公开查询接口")
@RestController
@RequestMapping("/open")
public class CourseOpenController {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @Autowired
    private CoursePublishService coursePublishService;

    /**
     * 获取课程预览信息
     * @param courseId 课程id
     * @return
     */
    @ApiOperation(value = "获取课程预览信息")
    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDto getCoursePreviewInfo(@PathVariable("courseId") Long courseId){
       return coursePublishService.getCoursePreviewInfo(courseId);
    }

}
