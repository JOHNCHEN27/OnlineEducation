package com.lncanswer.content.service.impl;

import com.lncanswer.content.mapper.TeachplanMapper;
import com.lncanswer.content.model.dto.TeachplanDto;
import com.lncanswer.content.service.TeachplanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description
 * @date 2023/9/12 13:16
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    private TeachplanMapper teachplanMapper;
    @Override
    public List<TeachplanDto> selectTeachplanTreeNodes(Long courId) {
        return teachplanMapper.selectTeachplanTreeNodes(courId);
    }
}
