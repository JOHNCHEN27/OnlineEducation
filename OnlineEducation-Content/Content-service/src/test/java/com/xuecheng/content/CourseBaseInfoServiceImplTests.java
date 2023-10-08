package com.xuecheng.content;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author LNC
 * @version 1.0
 * @description
 * @date 2023/9/12 18:18
 */
@SpringBootTest
public class CourseBaseInfoServiceImplTests {

    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void testUpdateCourseBaseInfoDto(){

     redisTemplate.delete("selectCourseBasePage");

    }
}
