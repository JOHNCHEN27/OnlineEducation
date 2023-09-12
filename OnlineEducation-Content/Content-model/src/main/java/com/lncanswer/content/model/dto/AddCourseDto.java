package com.lncanswer.content.model.dto;

import com.lncanswer.base.exception.ValidationGroups;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * @author LNC
 * @version 1.0
 * @description 新增课程 Dto
 * @date 2023/9/10 17:00
 */
@ApiModel(value="AddCourseDto", description="新增课程基本信息")
@Data
public class AddCourseDto {
    /**
     * 由于请求参数与数据库表实体类有差异 需要自定义一个Dto类来接受请求参数 并用于返回
     * controller层参数校验规则 利用Hibernate Validator完成
     * NotEmpry注解是校验规则 Size注解也是校验规则，一个规定属性不能为空，一个规定属性的大小
     */
    //开启分组校验 规定为哪个分组
    @NotEmpty(groups = {ValidationGroups.Inster.class},message = "添加课程名称不能为空")
    @NotEmpty(groups = {ValidationGroups.Update.class},message = "修改课程名称不能为空")
    //@NotEmpty(message = "课程名称不能为空")
    @ApiModelProperty(value = "课程名称", required = true)
    private String name;

    @NotEmpty(groups = {ValidationGroups.Inster.class},message = "适用人群不能为空")
    @Size(message = "适用人群内容过少",min = 10)
    @ApiModelProperty(value = "适用人群", required = true)
    private String users;

    @ApiModelProperty(value = "课程标签")
    private String tags;

    @NotEmpty(message = "课程分类不能为空")
    @ApiModelProperty(value = "大分类", required = true)
    private String mt;

    @NotEmpty(message = "课程分类不能为空")
    @ApiModelProperty(value = "小分类", required = true)
    private String st;

    @NotEmpty(message = "课程等级不能为空")
    @ApiModelProperty(value = "课程等级", required = true)
    private String grade;

    @ApiModelProperty(value = "教学模式（普通，录播，直播等）", required = true)
    private String teachmode;

    @ApiModelProperty(value = "课程介绍")
    private String description;

    @ApiModelProperty(value = "课程图片", required = true)
    private String pic;

    @NotEmpty(message = "收费规则不能为空")
    @ApiModelProperty(value = "收费规则，对应数据字典", required = true)
    private String charge;

    @ApiModelProperty(value = "价格")
    private Float price;
    @ApiModelProperty(value = "原价")
    private Float originalPrice;


    @ApiModelProperty(value = "qq")
    private String qq;

    @ApiModelProperty(value = "微信")
    private String wechat;
    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "有效期")
    private Integer validDays;
}
