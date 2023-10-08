package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description 课程分类树形结点Dto
 * @date 2023/9/10 11:04
 */
@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {

    /**
     * 数据库表中有一个是树形结构 需要返回一个树形结构的JSON数据
     * 编写树形结点Dto 继承课程分类 在基础上添加一个集合List 指定类型为自己的类型
     * List集合用来存放树形结点数据
     * Serializable序列化 避免在传送过程中出现问题
     */
    @ApiModelProperty("课程分类树形结点集合")
    List<CourseCategoryTreeDto> childrenTreeNodes;
}
