package com.lncanswer.content.model.dto;

import com.lncanswer.base.exception.ValidationGroups;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author LNC
 * @version 1.0
 * @description 修改课程 Dto 
 * @date 2023/9/11 10:22
 */
@Data
public class EditCourseDto extends AddCourseDto {

    @ApiModelProperty(value = "课程id",required = true)
    @NotNull(groups = {ValidationGroups.Update.class},message = "修改课程id不能为空")
    private Long id;
}
