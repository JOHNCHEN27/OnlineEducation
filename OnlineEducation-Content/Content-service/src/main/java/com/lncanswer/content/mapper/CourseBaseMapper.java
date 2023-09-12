package com.lncanswer.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lncanswer.content.model.po.CourseBase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 课程基本信息 Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
public interface CourseBaseMapper extends BaseMapper<CourseBase> {

    //查询所有信息添加到redis缓存中
    @Select("select * from course_base ")
    public List<CourseBase> selectAll();
}
