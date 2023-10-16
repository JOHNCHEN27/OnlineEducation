package com.xuecheng.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.base.exception.OnlieEducationException;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.model.po.CoursePublishPre;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.CourseTeacherService;
import com.xuecheng.content.service.TeachplanService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description 课程预览、课程发布信息实现类
 * @date 2023/10/16 15:56
 */
@Service
public class CoursePublishServiceImpl implements CoursePublishService {

    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @Autowired
    CourseBaseMapper courseBaseMapper;


    @Autowired
    TeachplanService teachplanService;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;

    @Autowired
    CourseTeacherService courseTeacherService;


    /**
     * 查询课程预览信息
     * @param courseId 课程id
     * @return
     */
    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        //根据课程id查询课程基本信息、课程营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);

        //课程计划信息
        List<TeachplanDto> teachplanDtos = teachplanService.selectTeachplanTreeNodes(courseId);

        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(teachplanDtos);
        return coursePreviewDto;
    }

    /**
     * 提交课程审核
     * @param companyId 机构id
     * @param courseId 课程id
     */
    @Override
    public void commitAudit(Long companyId, Long courseId) {
        //校验约束
        CourseBase courseBase = courseBaseMapper.selectById(companyId);

        //获取课程审核状态
        String auditStatus = courseBase.getAuditStatus();
        if (auditStatus.equals("202003")){
            //202003表示课程为审核中
            OnlieEducationException.cast("当前课程为审核状态，审核完成可以再次提交");
        }

        //判断机构id
        if (!courseBase.getCompanyId().equals(companyId)){
            OnlieEducationException.cast("不允许提交其他机构的课程");
        }
        //判断图片是否填写
        if (StringUtils.isEmpty(courseBase.getPic())){
            OnlieEducationException.cast("清上传课程图片");
        }

        //添加课程预发布记录
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        //查询课程基本信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        BeanUtils.copyProperties(courseBaseInfo,coursePublishPre);

        //课程营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(companyId);
        //将课程y营销信息转为JSON
        String courseMarketJson = JSON.toJSONString(courseMarket);
        coursePublishPre.setMarket(courseMarketJson);

        //查询课程计划信息
        List<TeachplanDto> teachplanDtos = teachplanService.selectTeachplanTreeNodes(courseId);
        if (teachplanDtos == null || teachplanDtos.size()<0){
            OnlieEducationException.cast("提交失败，请添加课程计划");
        }
        //将课程计划信息转为JSON
        String teachplanListJson = JSON.toJSONString(teachplanDtos);
        coursePublishPre.setTeachplan(teachplanListJson);

        //查询课程教师信息
        List<CourseTeacher> courseTeachers = courseTeacherService.selectCourseTeacher(courseId);
        //将课程教师信息转化为JSON
        String courseTeahcherListJSON = JSON.toJSONString(courseTeachers);
        coursePublishPre.setTeachers(courseTeahcherListJSON);
        //设置预发布表记录状态
        coursePublishPre.setStatus("202003");
        //设置教学机构id
        coursePublishPre.setCompanyId(companyId);
        //设置时间
        coursePublishPre.setCreateDate(LocalDateTime.now());
        //查询预发布表当前课程是否存在，存在则进行更新，不存在则插入
        CoursePublishPre coursePublishPreinsert = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPreinsert == null){
            //添加课程预发布记录
            coursePublishPreMapper.insert(coursePublishPre);
        }else {
            //存在则更新课程预发布记录
            coursePublishPreMapper.updateById(coursePublishPre);
        }

        //更新课程基本信息表的状态
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);

    }
}
