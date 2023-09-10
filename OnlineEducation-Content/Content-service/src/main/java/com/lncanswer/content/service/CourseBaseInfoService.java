package com.lncanswer.content.service;

import com.lncanswer.content.model.dto.AddCourseDto;
import com.lncanswer.content.model.dto.CourseBaseInfoDto;

/**
 * @author LNC
 * @version 1.0
 * @description 课程查询基本信息和课程营销信息接口
 * @date 2023/9/10 19:05
 */
public interface CourseBaseInfoService {

    /**
     * 添加课程基本信息
     * @param companyId 教学机构ID
     * @param addCourseDto 课程基本信息
     * @return
     */
    public CourseBaseInfoDto createCourseBase(Long companyId , AddCourseDto addCourseDto);
}
