package com.xuecheng.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 分页查询参数 封装成对象
 * 项目大多数地方都要用到分页参数 放到基础工程中
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PageParams {
    //当前页码  ApiModelProperty注解为属性加上说明 方便接口文档阅读
    @ApiModelProperty("当前页码")
    private Long pageNo = 1L;

    //每页记录数默认值
    @ApiModelProperty("每页记录数默认值")
    private Long pageSize =10L;

}
