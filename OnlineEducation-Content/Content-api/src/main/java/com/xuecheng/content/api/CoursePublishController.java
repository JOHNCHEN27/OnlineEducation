package com.xuecheng.content.api;

import com.alibaba.fastjson.JSON;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

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

    /**
     * 查询课程发布信息 /r表示此路径不需要通过授权即可访问
     * @param courseId
     * @return
     */
    @ApiOperation("查询课程发布信息")
    @GetMapping("/r/coursepublish/{courseId}")
    public CoursePublish getCoursepublish(@PathVariable ("courseId") Long courseId){
        return coursePublishService.getCoursepublish(courseId);
    }


    /**
     * 获取课程发布信息
     * @param courseId 课程id
     * @return
     */
    @ApiOperation("获取课程发布信息")
    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDto getCoursePublish(@PathVariable("courseId") Long courseId){
        //查询课程发布信息
        CoursePublish coursePublish = coursePublishService.getCoursepublish(courseId);
        if (coursePublish == null){
            return  new CoursePreviewDto();
        }
        //课程基本信息
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(coursePublish,courseBaseInfoDto);
        //课程计划信息
        List<TeachplanDto> teachplanDtos = JSON.parseArray(coursePublish.getTeachplan(), TeachplanDto.class);
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfoDto);
        coursePreviewDto.setTeachplans(teachplanDtos);
        return coursePreviewDto;

    }
}
