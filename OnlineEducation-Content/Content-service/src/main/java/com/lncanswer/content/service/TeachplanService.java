package com.lncanswer.content.service;

import com.lncanswer.content.model.dto.TeachplanDto;

import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description
 * @date 2023/9/12 13:16
 */
public interface TeachplanService {

    public List<TeachplanDto> selectTeachplanTreeNodes(Long courId);
}
