package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description 课程预览信息数据模型
 * @date 2023/10/16 15:52
 */
@ApiModel(value = "课程预览信息数据模型")
@Data
@ToString
public class CoursePreviewDto {

    @ApiModelProperty("课程基本信息，课程营销信息")
    //课程基本信息，课程营销信息
    CourseBaseInfoDto courseBase;

    @ApiModelProperty("课程计划信息")
    //课程计划信息
    List<TeachplanDto> teachplans;
}
