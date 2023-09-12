package com.lncanswer.content.model.dto;

import com.lncanswer.content.model.po.Teachplan;
import com.lncanswer.content.model.po.TeachplanMedia;
import lombok.*;

import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description 课程计划关联的媒资信息
 * @date 2023/9/12 9:59
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TeachplanDto extends Teachplan {
    //课程计划关联的媒资信息
    public TeachplanMedia teachplanMedia;

    //子节点 子节点的类型也是一个树形结点
    public List<TeachplanDto> teachPlanTreeNodes ;
}
