package com.lncanswer.content.api;

import com.lncanswer.content.model.dto.CourseCategoryTreeDto;
import com.lncanswer.content.service.CourseCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description  课程查询分类接口
 * @date 2023/9/10 11:09
 */
@Api(value = "课程查询分类接口",tags = "课程查询分类接口")
@Slf4j
@RestController
public class CourseCategoryController {

    @Autowired
    private CourseCategoryService categoryService;

    @ApiOperation("查询课程分页集合")
    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes(){
        List<CourseCategoryTreeDto> list = categoryService.queryTreeNodes("1");
        return list;
    }
}
