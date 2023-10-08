package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description
 * @date 2023/9/15 16:23
 */
public interface CourseTeacherService extends IService<CourseTeacher> {

    //查询教师接口
    public List<CourseTeacher> selectCourseTeacher(Integer id);

    //添加教师
    public CourseTeacher saveCourseTeacher(Long companyId,CourseTeacher courseTeacher);

    //修改教师
    public CourseTeacher updateCourseTeacher(Long companyId,CourseTeacher courseTeacher);

    //删除教师
    public int deleteCourseTeacher(Long companyId,Long courseId,Long teacherId);
}
