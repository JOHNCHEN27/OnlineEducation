package com.lncanswer.content.service;

import com.lncanswer.content.model.dto.SaveTeachplanDto;
import com.lncanswer.content.model.dto.TeachplanDto;

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
}
