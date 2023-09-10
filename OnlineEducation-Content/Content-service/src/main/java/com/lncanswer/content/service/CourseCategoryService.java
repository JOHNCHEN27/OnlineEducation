package com.lncanswer.content.service;

import com.lncanswer.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description 课程分类service接口
 * @date 2023/9/10 11:50
 */

public interface CourseCategoryService {

    /**
     * 课程查询接口树形结构
     * @param id
     * @return
     */
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);

}
