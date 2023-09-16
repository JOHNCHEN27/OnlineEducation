package com.lncanswer.content.model.po;

import com.baomidou.mybatisplus.annotation.*;
import com.lncanswer.base.exception.ValidationGroups;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程-教师关系表
 * </p>
 *
 * @author itcast
 */
@Data
@TableName("course_teacher")
public class CourseTeacher implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @NotNull(groups = {ValidationGroups.Update.class},message = "教师id不能为空")
    @NotNull(groups = {ValidationGroups.Delete.class},message = "教师id不能为空")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 课程标识
     */
    @NotNull(groups = {ValidationGroups.Update.class},message = "课程id不能为空")
    @NotNull(groups = {ValidationGroups.Delete.class},message = "课程id不能为空")
    @NotNull(groups = {ValidationGroups.Inster.class},message = "课程id不能为空")
    private Long courseId;

    /**
     * 教师标识
     */
    private String teacherName;

    /**
     * 教师职位
     */
    private String position;

    /**
     * 教师简介
     */
    private String introduction;

    /**
     * 照片
     */
    private String photograph;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;


}
