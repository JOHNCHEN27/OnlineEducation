package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;

/**
 * @author LNC
 * @version 1.0
 * @description
 * @date 2023/9/6 10:28
 */
public interface CourseBaseService {
    /**
     * 创建分页查询的业务层实现接口
     */

    public PageResult<CourseBase> selectCourseBasePage(Long CompanyId,
            PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

}
