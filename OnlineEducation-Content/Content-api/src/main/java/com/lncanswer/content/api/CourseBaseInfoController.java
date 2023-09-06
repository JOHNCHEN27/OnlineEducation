package com.lncanswer.content.api;

import com.lncanswer.base.model.PageParams;
import com.lncanswer.base.model.PageResult;
import com.lncanswer.content.service.CourseBaseService;
import com.lncanswer.content.model.dto.QueryCourseParamsDto;
import com.lncanswer.content.model.po.CourseBase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LNC
 * @version 1.0
 * @date 2023/9/5 13:53
 */
@Api(value = "课程信息编辑接口",tags = "课程信息编辑接口") //swagger接口文档说明
@RestController
public class CourseBaseInfoController {
    @Autowired
    private CourseBaseService courseBaseService;

    /**
     * 查询课程信息分页接口   分页信息用PageParams实体类封装接收，
     * 具体查询信息用QueryCourseParamsDto 实体类封装接收 注解为JSON格式
     * @param pageParams
     * @param queryCourseParamsDto
     * @return
     */
    @ApiOperation("课程查询接口") //对此方法的接口描述
    @PostMapping ("/course/list")//RequestMapping会生成所有类型的接口上传方法
    public PageResult<CourseBase> list    //required = false 表示此参数不是必填项
            (PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto){
//        //利用Swagger进行接口测试
//        CourseBase courseBase =new CourseBase();
//        courseBase.setName("测试名称");
//        courseBase.setCreateDate(LocalDateTime.now());
//        List<CourseBase> courseBases = new ArrayList<>();
//        courseBases.add(courseBase);
//        PageResult<CourseBase> pageResult = new PageResult<>(courseBases,10,1,10);

        PageResult<CourseBase> pageResult = courseBaseService.selectCourseBasePage(pageParams,queryCourseParamsDto);
           return pageResult;
    }

}
