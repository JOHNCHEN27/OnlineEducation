package com.xuecheng.learning.service;

import com.xuecheng.base.model.PageResult;
import com.xuecheng.learning.model.dto.MyCourseTableParams;
import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.model.po.XcCourseTables;

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

    /**
     * 保存选课成功状态
     * @param chooseCourseId
     * @return
     */
     boolean saveChooseCourseSuccess(String chooseCourseId);

    /**
     * 我的课程表
     * @param params
     * @return
     */
     PageResult<XcCourseTables> mycoursetables(MyCourseTableParams params);
}
