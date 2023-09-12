package com.lncanswer.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lncanswer.base.model.PageParams;
import com.lncanswer.base.model.PageResult;
import com.lncanswer.content.mapper.CourseBaseMapper;
import com.lncanswer.content.service.CourseBaseService;
import com.lncanswer.content.model.dto.QueryCourseParamsDto;
import com.lncanswer.content.model.po.CourseBase;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author LNC
 * @version 1.0
 * @description
 * @date 2023/9/6 10:31
 */
@Service
public class CourseBaseServiceImpl implements CourseBaseService {

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 编写具体的分页查询条件
     * @param pageParams
     * @param queryCourseParamsDto
     * @return
     */
    @Override
    public PageResult<CourseBase> selectCourseBasePage(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {

        //创建分页  设置从当前页码开始 每页查询多少条数据
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        //构造分页查询条件
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //模糊查询姓名 selet * from coursebase where name like "?"
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),
                CourseBase::getName,queryCourseParamsDto.getCourseName());
        //查询课程审核状态
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),
                CourseBase::getAuditStatus,queryCourseParamsDto.getAuditStatus());
        //查询课程的发布状态
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()),
                CourseBase::getStatus,queryCourseParamsDto.getPublishStatus());

        //查询分页 查询出来的结果MybatisPlus会自动存放在page中 可以不用另外的参数接受
        courseBaseMapper.selectPage(page,queryWrapper);

        //返回封装分页结果的实体类 PageResult
        PageResult<CourseBase> pageResult = new PageResult<>(page.getRecords(),page.getTotal(),
                page.getCurrent(),page.getSize());
        //返回结果
        if (pageResult != null)
        {
            //将数据缓存到redis中

        }
        return pageResult;
    }
}
