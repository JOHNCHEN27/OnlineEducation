package com.xuecheng.content;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.service.CourseBaseService;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author LNC
 * @version 1.0
 * @description
 * @date 2023/9/6 10:45
 */
@SpringBootTest
public class CourseBaseServiceTests {
    @Autowired
    private CourseBaseService courseBaseService;
    /**
     * 每一个功能都需要进行单元测试 每一个属性都要测到，防止错误产生
     * 测试分页查询业务层接口
     */
    @Test
    public void testCourseBaseService(){
        //查询条件
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("java");
        queryCourseParamsDto.setAuditStatus("202004");
        queryCourseParamsDto.setPublishStatus("203001");

        //分页参数
        PageParams pageParams = new PageParams();
        pageParams.setPageNo(1L);//页码
        pageParams.setPageSize(3L);//每页记录数

        PageResult<CourseBase> courseBasePageResult = courseBaseService.selectCourseBasePage(null,pageParams, queryCourseParamsDto);
        System.out.println(courseBasePageResult);
    }


}
