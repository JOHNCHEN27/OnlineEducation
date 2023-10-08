package com.xuecheng.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description  测试分页数据
 * @date 2023/9/6 9:57
 */
@SpringBootTest
public class CourseBaseMapperTests {

    //注入Mapper接口测试
    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Test
    public void testCourseBaseMapper(){
        CourseBase courseBase = courseBaseMapper.selectById(74L);
        //断言判断不为空，为空拒绝执行
        Assertions.assertNotNull(courseBase);

        //测试分页查询接口
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //构造查询条件
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("java");
        queryCourseParamsDto.setAuditStatus("202004");
        queryCourseParamsDto.setPublishStatus("203001");

        //拼接查询条件 根据课程审核状态
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),
                CourseBase::getAuditStatus,queryCourseParamsDto.getAuditStatus());
        //根据课程名称模糊查询
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),
                CourseBase::getName,queryCourseParamsDto.getCourseName());

        //分页参数
        PageParams pageParams = new PageParams();
        pageParams.setPageNo(1L);
        pageParams.setPageSize(5L);

        //构建分页 当前页码为第几页 每页展示多少条数据
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        //查询分页结果
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page,queryWrapper);

        //拿掉分页结果后封装到PageResult实体类中返回
        //Records中封装的是返回数据
        List<CourseBase> items = pageResult.getRecords();

        //总记录数
        long  total  = pageResult.getTotal();

        //封装到PageResult实体类中
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(items,total,pageResult.getCurrent(),pageResult.getSize());

        System.out.println(courseBasePageResult);
    }






}
