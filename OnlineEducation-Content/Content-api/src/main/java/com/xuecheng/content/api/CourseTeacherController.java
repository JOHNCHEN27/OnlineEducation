package com.xuecheng.content.api;

import com.xuecheng.base.exception.ValidationGroups;
import com.xuecheng.base.result.ResultClass;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description 课程教师控制器
 * @date 2023/9/15 16:16
 */
@Api(value = "课程教师功能接口",tags = "课程教师接口")
@RestController
@Slf4j
@RequestMapping
public class CourseTeacherController {
    @Autowired
    private CourseTeacherService teacherService;


    /**
     * 根据课程id查询所教课程教师
     * @param id
     * @return
     */
    @ApiOperation(value = "查询课程计划教师",notes = "课程id必传")
    @GetMapping("/courseTeacher/list/{id}")
    public List<CourseTeacher> queryCourseTeacherList(@PathVariable("id") Integer id){
        if (id != null){
           List<CourseTeacher> list = teacherService.selectCourseTeacher(id);
           if (list != null && !list.isEmpty()){
               return list;
           }
        }
        log.info("查询教师失败");
        return null;
    }

    /**
     * 根据课程id 和 机构id向同一机构下添加老师
     * @param courseTeacher
     * @return
     */
    @ApiOperation(value = "添加老师",notes = "课程id、机构id不为空")
    @PostMapping("/courseTeacher")
    public CourseTeacher saveCourseTeacher(@RequestBody @Validated(
            {ValidationGroups.Inster.class}) CourseTeacher courseTeacher){
        //添加教师时需要根据机构来添加，只允许向机构自己的课程中添加老师、删除老师
        //这里因为机构认证还没有上线，暂时硬编码
        Long companyId = 1232141425L;
        CourseTeacher teacher = teacherService.saveCourseTeacher(companyId,courseTeacher);
        if (courseTeacher != null){
            return teacher;
        }
        return null;
    }

    /**
     * 根据课程id、教师id、机构id来修改教师的信息
     * 同一机构下的只能修改自己的教师信息
     * @param courseTeacher
     * @return
     */
    @ApiOperation(value = "修改教师信息",notes = "课程号、教师id、机构id不为空")
    @PutMapping("/courseTeacher")
    public CourseTeacher updateCourseTeacher(@RequestBody @Validated({
            ValidationGroups.Update.class}) CourseTeacher courseTeacher){
        //这里因为机构认证还没有上线，暂时硬编码
        Long companyId = 1232141425L;
        CourseTeacher teacher = teacherService.updateCourseTeacher(companyId,courseTeacher);
        if (courseTeacher != null){
            return teacher;
        }
        return null;
    }

    /**
     *根据课程id、教师id、机构id删除教师信息
     * @param courseId
     * @param teacherId
     * @return
     */
    @ApiOperation(value = "删除教师", notes = "课程id、教师id、机构id不为空")
    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    public ResultClass deleteCourseTeacherById(@PathVariable ("courseId") Long courseId,
                                               @PathVariable("teacherId") Long teacherId){
        log.info("id1:{},id2:{}",courseId,teacherId);
        //这里因为机构认证还没有上线，暂时硬编码
        Long companyId = 1232141425L;
        int num = teacherService.deleteCourseTeacher(companyId,courseId,teacherId);
        if (num != 0){
            return ResultClass.success();
        }
        return null;
    }


}
