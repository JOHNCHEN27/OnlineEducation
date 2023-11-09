package com.xuecheng.learning.service;

import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;

/**
 * @author LNC
 * @version 1.0
 * @description 我的课程service接口
 * @date 2023/11/9 15:17
 */
public interface MyCourseTablesService {

    /**
     * 添加选课接口
     * @param userId 用户id
     * @param courseId 课程id
     * @return
     */
     XcChooseCourseDto addChooseCourse(String userId, Long courseId);

    /**
     * 判断学习资格
     * @param usreId 用户id
     * @param courseId 课程id
     * @return
     */
     XcCourseTablesDto getLearningStatus(String usreId,Long courseId);
}
