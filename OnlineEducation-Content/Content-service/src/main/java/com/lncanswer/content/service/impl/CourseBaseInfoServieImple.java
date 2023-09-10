package com.lncanswer.content.service.impl;

import com.lncanswer.base.exception.OnlieEducationException;
import com.lncanswer.content.mapper.CourseBaseMapper;
import com.lncanswer.content.mapper.CourseCategoryMapper;
import com.lncanswer.content.mapper.CourseMarketMapper;
import com.lncanswer.content.model.dto.AddCourseDto;
import com.lncanswer.content.model.dto.CourseBaseInfoDto;
import com.lncanswer.content.model.po.CourseBase;
import com.lncanswer.content.model.po.CourseCategory;
import com.lncanswer.content.model.po.CourseMarket;
import com.lncanswer.content.service.CourseBaseInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author LNC
 * @version 1.0
 * @description
 * @date 2023/9/10 19:07
 */
@Service
public class CourseBaseInfoServieImple implements CourseBaseInfoService {
    @Autowired
    private CourseCategoryMapper courseCategoryMapper;
    @Autowired
    private CourseMarketMapper courseMarketMapper;
    @Autowired
    private CourseBaseMapper courseBaseMapper;

    /**
     * 新增课程
     * @param companyId 教学机构ID
     * @param addCourseDto 课程基本信息
     * @return
     */
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {

        //合法性校验
        if(StringUtils.isBlank(addCourseDto.getName())){
            throw new RuntimeException("课程名称为空");
        }
        if (StringUtils.isBlank(addCourseDto.getMt())){
            throw new RuntimeException("课程分类为空");
        }
        if (StringUtils.isBlank(addCourseDto.getSt())){
            throw new RuntimeException("课程分类为空");
        }
        if (StringUtils.isBlank(addCourseDto.getGrade())){
            throw new RuntimeException("课程等级为空");
        }
        if (StringUtils.isBlank(addCourseDto.getTeachmode())){
            throw new RuntimeException("教育模式为空");
        }
        if (StringUtils.isBlank(addCourseDto.getCharge())){
            throw new RuntimeException("收费规则为空");
        }
        if (StringUtils.isBlank(addCourseDto.getUsers())){
            throw new RuntimeException("使用人群为空");
        }

        //新增对象
        CourseBase courseBase =new CourseBase();
        //将填写的参数信息传递给新增对象
        BeanUtils.copyProperties(addCourseDto,courseBase);
        //设置审核状态
        courseBase.setAuditStatus("202002");
        //设置发布状态
        courseBase.setStatus("203001");
        //机构id
        courseBase.setCompanyId(companyId);
        //添加时间
        courseBase.setCreateDate(LocalDateTime.now());
        //将新增课程信息插入到课程基本信息表
        int insert = courseBaseMapper.insert(courseBase);
        if (insert<=0){
            throw new RuntimeException("新增课程基本信息失败");
        }

        //向课程营销表中保存课程营销信息
        CourseMarket courseMarket = new CourseMarket();
        //获取课程id
        Long courseId = courseBase.getId();
        BeanUtils.copyProperties(addCourseDto,courseMarket);
        //设置课程id
        courseMarket.setId(courseId);

        //将课程营销信息插入，调用自定义方法
        int i = saveCourseMarket(courseMarket);
        if (i <=0){
            throw new RuntimeException("保存课程营销信息失败");
        }
        //查询课程基本信息及营销信息并返回 调用自定义方法
        return getCourseBaseInfo(courseId);
    }


    public int saveCourseMarket(CourseMarket courseMarket){
        //收费规则
        String charge = courseMarket.getCharge();
        //健壮性判断
        if (StringUtils.isBlank(charge)){
            throw new RuntimeException("收费规则没有选择");
        }

        //判断收费规则是否为收费
        if (charge.equals("201001")){
            //如果是收费 判断是否输入有效数值
            if (courseMarket.getPrice() == null || courseMarket.getPrice().floatValue() <=0){
                throw new OnlieEducationException("课程为收费，价格不能为空并且价格必须大于0");
            }
        }
        //根据id从课程营销表查询
        CourseMarket courseMarketobj = courseMarketMapper.selectById(courseMarket.getId());
        //判断是否已经存在该信息 存在则更新，不存在则添加
        if (courseMarketobj == null){
            //不存在则添加信息并返回
            return courseMarketMapper.insert(courseMarket);
        }else {
            //存在则更新信息 将传入的对象copy给已经存在的对象 再将该对象更新
            BeanUtils.copyProperties(courseMarket,courseMarketobj);
            courseMarketobj.setId(courseMarket.getId());
            return courseMarketMapper.updateById(courseMarketobj);
        }
    }

    /**
     * 根据id查询课程基本信息、课程营销信息
     * @param courseId
     * @return
     */
    public CourseBaseInfoDto getCourseBaseInfo(long courseId){
        //查询课程基本信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        //如果为空 直接返回
        if (courseBase == null){
            return null;
        }
        //查询课程营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //创建返回类型对象
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        //将课程基本信息、课程营销信息对象进行拷贝 利用BeanUtils工具类
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        //拷贝课程营销信息之前判断一下 是否为空 为空不进行拷贝
        if (courseMarket != null){
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }

        //查询分类名称 为返回对象设置
        CourseCategory courseCategoryBySt =courseCategoryMapper.selectById(courseBase.getSt());
        //设置分类名称
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        //查询大分类名称
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());

        //返回查询对象
        return courseBaseInfoDto;
    }
}
