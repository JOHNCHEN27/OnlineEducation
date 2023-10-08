package com.xuecheng.content.model.dto;

import com.xuecheng.base.exception.ValidationGroups;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;

/**
 * @author LNC
 * @version 1.0
 * @description  保存课程计划dto，包括新增、修改
 * @date 2023/9/14 18:05
 */
@Slf4j
@Data
@ApiModel(value = "课程计划Dto")
public class SaveTeachplanDto {
    /***
     * 教学计划id
     */
    @NotNull(groups = {ValidationGroups.Delete.class},message = "删除课程计划时课程计划id不能为空")
    @ApiModelProperty(value = "教学计划id")
    private Long id;

    /**
     * 课程计划名称
     */
    private String pname;

    /**
     * 课程计划父级Id
     */
    private Long parentid;

    /**
     * 层级，分为1、2、3级
     */
    private Integer grade;

    /**
     * 课程类型:1视频、2文档
     */
    private String mediaType;


    /**
     * 课程标识
     */
    private Long courseId;

    /**
     * 课程发布标识
     */
    private Long coursePubId;


    /**
     * 是否支持试学或预览（试看）
     */
    private String isPreview;



}
