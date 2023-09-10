package com.lncanswer.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lncanswer.content.model.dto.CourseCategoryTreeDto;
import com.lncanswer.content.model.po.CourseCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

    /**
     * 递归查询课程分类树形结点语句
     */
    public List<CourseCategoryTreeDto> selectTreeNodes(String id);

}
