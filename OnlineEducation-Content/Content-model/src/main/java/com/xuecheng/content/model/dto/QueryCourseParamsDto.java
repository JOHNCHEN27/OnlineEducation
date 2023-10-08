package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 课程查询参数 封装成对象
 * 属于课程查询的特有参数 在内容数据模块中定义
 */
@Data
@ToString
public class QueryCourseParamsDto {
    //审核状态
    @ApiModelProperty("审核状态")
    private String auditStatus;
    //课程名称
    @ApiModelProperty("课程名称")
    private String courseName;
    //发布状态
    private String publishStatus;

}
