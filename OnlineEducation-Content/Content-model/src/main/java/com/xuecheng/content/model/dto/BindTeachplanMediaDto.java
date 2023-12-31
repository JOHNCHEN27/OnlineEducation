package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author LNC
 * @version 1.0
 * @description 绑定课程计划媒资信息
 * @date 2023/10/14 13:54
 */
@Data
@ApiModel(value = "BindTeachplanMediaDto",description = "教学计划-媒资绑定提交数据")
public class BindTeachplanMediaDto {

    @ApiModelProperty(value = "媒资文件id", required = true)
    private String mediaId;

    @ApiModelProperty(value = "媒资文件名称",required = true)
    private String fileName;

    @ApiModelProperty(value = "课程计划标识",required = true)
    private Long teachplanId;
}
