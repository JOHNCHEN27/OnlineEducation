package com.xuecheng.content;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description 课程查询业务层方法测试
 * @date 2023/9/10 16:38
 */
@SpringBootTest
public class CourseCategoryTests {

    //注入service对象
    @Autowired
    private CourseCategoryService service;

    /**
     * 测试课程分类树形结点返回结果
     */
    @Test
    public void testCourseCategoryTreeNodes(){
        List<CourseCategoryTreeDto> list=  service.queryTreeNodes("1");
        System.out.println(list);
    }
}
