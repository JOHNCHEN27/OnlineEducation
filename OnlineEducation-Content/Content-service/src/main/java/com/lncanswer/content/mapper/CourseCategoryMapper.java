package com.lncanswer.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lncanswer.content.model.po.CourseCategory;
import org.apache.ibatis.annotations.Mapper;


/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

}
