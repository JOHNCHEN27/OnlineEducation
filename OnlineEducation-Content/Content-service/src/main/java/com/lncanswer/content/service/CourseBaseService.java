package com.lncanswer.content.service;

import com.lncanswer.base.model.PageParams;
import com.lncanswer.base.model.PageResult;
import com.lncanswer.content.model.dto.QueryCourseParamsDto;
import com.lncanswer.content.model.po.CourseBase;

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

    public PageResult<CourseBase> selectCourseBasePage(
            PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

}
