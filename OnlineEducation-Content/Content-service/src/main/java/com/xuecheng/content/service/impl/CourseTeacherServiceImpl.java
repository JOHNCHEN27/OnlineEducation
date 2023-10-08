package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description
 * @date 2023/9/15 16:23
 */
@Slf4j
@Service
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper,CourseTeacher> implements CourseTeacherService  {

    @Autowired
    private CourseBaseMapper courseBaseMapper;
    @Autowired
    private CourseTeacherMapper mapper;
    /**
     * 根据课程id查询教师
     * @param id
     * @return
     */
    @Override
    public List<CourseTeacher> selectCourseTeacher(Integer id) {
        LambdaQueryWrapper<CourseTeacher> lamQuery = new LambdaQueryWrapper<>();
        lamQuery.eq(id != null,CourseTeacher::getCourseId,id);
        List<CourseTeacher> teacherList = this.list(lamQuery);
        if (teacherList!= null && !teacherList.isEmpty()){
            return teacherList;
        }
        return null;
    }

    /**
     * 根据机构id、课程id添加教师
     * 只能在自己机构下添加老师
     * @param courseTeacher
     * @return
     */
    @Override
    public CourseTeacher saveCourseTeacher(Long companyId,CourseTeacher courseTeacher) {
        //判断当前教师是否为当前机构下的老师
        Long courseId = courseTeacher.getCourseId();
        //根据当前老师所教课程找到机构id 判断是否同一机构 是则可以添加
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase != null){
            //判断老师所教课程是否是机构课程
            if (courseBase.getCompanyId().equals(companyId)){
               //是则添添加老师
                courseTeacher.setCreateDate(LocalDateTime.now());
               this.save(courseTeacher);
               log.info("courseTeacherID:{}",courseTeacher.getId());
               //根据课程id和老师名称将添加之后的老师查询出来
                CourseTeacher teacher = this.getById(courseTeacher.getId());
                return teacher;
            }
        }
        return null;
    }

    /**
     * 根据机构id、课程id、教师id修改教师
     * @param companyId
     * @param courseTeacher
     * @return
     */
    @Override
    public CourseTeacher updateCourseTeacher(Long companyId, CourseTeacher courseTeacher) {
        Long courseId = courseTeacher.getCourseId();
        //根据当前老师所教课程找到机构id 判断是否同一机构 是则可以添加
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase != null &&courseBase.getCompanyId().equals(companyId)){
            LambdaQueryWrapper<CourseTeacher> lamQuery = new LambdaQueryWrapper<>();
            lamQuery.eq(CourseTeacher::getId,courseTeacher.getId());
            lamQuery.eq(CourseTeacher::getCourseId,courseTeacher.getCourseId());
            mapper.update(courseTeacher,lamQuery);
            CourseTeacher teacher = this.getById(courseTeacher.getId());
            return teacher;
        }
        return null;
    }

    /**
     * 根据机构id、课程id、教师id删除教师信息
     * @param companyId
     * @param courseId
     * @param teacherId
     * @return
     */
    @Override
    public int deleteCourseTeacher(Long companyId, Long courseId, Long teacherId) {
        //根据当前老师所教课程找到机构id 判断是否同一机构 是则可以添加
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase != null &&courseBase.getCompanyId().equals(companyId)){
            LambdaQueryWrapper<CourseTeacher> lamQuery = new LambdaQueryWrapper<>();
            lamQuery.eq(CourseTeacher::getId,teacherId);
            lamQuery.eq(CourseTeacher::getCourseId,courseId);
            int delete = mapper.delete(lamQuery);
            return delete;
        }
        return 0;
    }
}
