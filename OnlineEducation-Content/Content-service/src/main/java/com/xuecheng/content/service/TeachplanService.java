package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.TeachplanMedia;

import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description TeachPlan接口
 * @date 2023/9/12 13:16
 */
public interface TeachplanService {

    public List<TeachplanDto> selectTeachplanTreeNodes(Long courId);

    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto);

    public int  deleteTeachplanById(Integer id);

    public String moveupTeachplan(Integer id);

    public String movedownTeachplan(Integer id);

    /**
     * 教学计划绑定媒资信息
     * @param bindTeachplanMediaDto
     * @return
     */
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

}
