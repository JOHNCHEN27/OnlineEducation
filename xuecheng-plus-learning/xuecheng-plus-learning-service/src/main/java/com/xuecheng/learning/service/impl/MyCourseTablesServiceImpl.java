package com.xuecheng.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.OnlieEducationException;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.mapper.XcChooseCourseMapper;
import com.xuecheng.learning.mapper.XcCourseTablesMapper;
import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.model.po.XcChooseCourse;
import com.xuecheng.learning.model.po.XcCourseTables;
import com.xuecheng.learning.service.MyCourseTablesService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description
 * @date 2023/11/9 15:19
 */
@Service
public class MyCourseTablesServiceImpl implements MyCourseTablesService {
    @Autowired
    XcCourseTablesMapper xcCourseTablesMapper;
    @Autowired
    XcChooseCourseMapper xcChooseCourseMapper;

    @Autowired
    ContentServiceClient contentServiceClient;


    /**
     * 添加选课
     * @param userId 用户id
     * @param courseId 课程id
     * @return
     */
    @Transactional
    @Override
    public XcChooseCourseDto addChooseCourse(String userId, Long courseId) {
        //查询课程信息
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        //获取课程的收费标准 判断是否收费
        String charge = coursepublish.getCharge();

        XcChooseCourse chooseCourse = null;
        if ("201000".equals(charge)){
            //201000表示课程免费,添加课程到选课记录
            chooseCourse = addFreeCourse(userId,coursepublish);
            //添加到我的课程表
            XcCourseTables courseTables = addCourseTables(chooseCourse);
        }else {
            //添加收费课程
            chooseCourse =  addChargeCourse(userId,coursepublish);
        }
        XcChooseCourseDto xcChooseCourseDto = new XcChooseCourseDto();
        BeanUtils.copyProperties(chooseCourse,xcChooseCourseDto);
        //获取学习资格
        XcCourseTablesDto xcCourseTablesDto = getLearningStatus(userId,courseId);
        xcChooseCourseDto.setLearnStatus(xcCourseTablesDto.getLearnStatus());
        return xcChooseCourseDto;

    }

    /**
     * 判断学习资格
     * @param usreId 用户id
     * @param courseId 课程id
     * @return
     * [{"code":"702001","desc":"正常学习"},
     * {"code":"702002","desc":"没有选课或选课后没有支付"},
     * {"code":"702003","desc":"已过期需要申请续期或重新支付"}]
     */
    @Override
    public XcCourseTablesDto getLearningStatus(String usreId, Long courseId) {
        //查询课程表 是否存在此课程
        XcCourseTables courseTables = getCourseTables(usreId, courseId);
        if (courseTables == null){
            //查询课程为空说明还未支付或者没有选课
            XcCourseTablesDto xcCourseTablesDto = new XcCourseTablesDto();
            //没有选课或选课后没有支付
            xcCourseTablesDto.setLearnStatus("702002");
            return xcCourseTablesDto;
        }
        //不为空说明已经存在我的课程，判断是否过期
        XcCourseTablesDto xcCourseTablesDto = new XcCourseTablesDto();
        BeanUtils.copyProperties(courseTables,xcCourseTablesDto);
        boolean isExpires = courseTables.getValidtimeEnd().isBefore(LocalDateTime.now());
        if (!isExpires){
            //未过期
            xcCourseTablesDto.setLearnStatus("702001");
            return xcCourseTablesDto;

        }else
        {
            //已过期
            xcCourseTablesDto.setLearnStatus("702003");
            return xcCourseTablesDto;
        }
    }

    //添加收费课程
    public XcChooseCourse addChargeCourse(String userId, CoursePublish coursepublish) {
        //判断是否支付成功,存在未支付记录直接返回
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(XcChooseCourse::getUserId,userId)
                .eq(XcChooseCourse::getCourseId,coursepublish.getId())
                .eq(XcChooseCourse::getOrderType,"700002") //收费订单
                .eq(XcChooseCourse::getStatus,"701002"); //待支付状态
        List<XcChooseCourse> xcChooseCourses = xcChooseCourseMapper.selectList(queryWrapper);
        if (xcChooseCourses != null && xcChooseCourses.size()>0){
            return xcChooseCourses.get(0);
        }
        //不存在则创建收费课程
        XcChooseCourse chooseCourse = new XcChooseCourse();
        chooseCourse.setCourseId(coursepublish.getId());
        chooseCourse.setUserId(userId);
        chooseCourse.setCourseName(coursepublish.getCompanyName());
        chooseCourse.setCoursePrice(coursepublish.getPrice());
        chooseCourse.setCompanyId(coursepublish.getCompanyId());
        chooseCourse.setOrderType("700002"); //收费课程
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setStatus("701002"); //待支付
        chooseCourse.setValidDays(coursepublish.getValidDays());
        chooseCourse.setValidtimeStart(LocalDateTime.now());
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(coursepublish.getValidDays()));
        xcChooseCourseMapper.insert(chooseCourse);
        return chooseCourse;

    }

    //根据选课记录添加选课记录到我的课程表
    public XcCourseTables addCourseTables(XcChooseCourse chooseCourse) {
        String status = chooseCourse.getStatus();
        //判断是否选课成功
        if (!"701001".equals(status)){
            OnlieEducationException.cast("选课未成功，无法添加到课程表");
        }
        //查询我的课程表
        XcCourseTables courseTables = getCourseTables(chooseCourse.getUserId(), chooseCourse.getCourseId());
        if (courseTables != null){
            return courseTables;
        }

        XcCourseTables xcCourseTables = new XcCourseTables();
        xcCourseTables.setChooseCourseId(chooseCourse.getId());
        xcCourseTables.setUserId(chooseCourse.getUserId());
        xcCourseTables.setCourseId(chooseCourse.getCourseId());
        xcCourseTables.setCompanyId(chooseCourse.getCompanyId());

        xcCourseTables.setCourseName(chooseCourse.getCourseName());
        xcCourseTables.setCreateDate(LocalDateTime.now());
        xcCourseTables.setValidtimeStart(chooseCourse.getValidtimeStart());
        xcCourseTables.setValidtimeEnd(chooseCourse.getValidtimeEnd());
        xcCourseTables.setCourseType(chooseCourse.getOrderType());

         xcCourseTablesMapper.insert(xcCourseTables);
        return xcCourseTables;
    }

    //查询课程表 判断课程是否已经存在
    public XcCourseTables getCourseTables(String userId, Long courseId) {
        XcCourseTables courseTables = xcCourseTablesMapper.selectOne(new LambdaQueryWrapper<XcCourseTables>()
                .eq(XcCourseTables::getChooseCourseId, courseId)
                .eq(XcCourseTables::getUserId, userId));
        return courseTables;
    }

    //添加免费课程方法 免费课程加入选课记录表，我的选课表
    public XcChooseCourse addFreeCourse(String userId, CoursePublish coursepublish) {
        //查询选课记录表中是否以及存在此课程
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(XcChooseCourse::getUserId,userId)
                .eq(XcChooseCourse::getCourseId,coursepublish.getId())
                .eq(XcChooseCourse::getOrderType,"700001") //免费课程
                .eq(XcChooseCourse::getStatus,"701001"); //选课成功
        List<XcChooseCourse> xcChooseCourses = xcChooseCourseMapper.selectList(queryWrapper);
        if (xcChooseCourses != null && xcChooseCourses.size() >0){
            return xcChooseCourses.get(0);
        }

        //如果查询选课表中选课不存在，则手动添加插入
        XcChooseCourse chooseCourse = new XcChooseCourse();
        chooseCourse.setCourseId(coursepublish.getId());
        chooseCourse.setUserId(userId);
        chooseCourse.setCourseName(coursepublish.getName());
        chooseCourse.setCoursePrice(0f); //免费课程价格为0
        chooseCourse.setCompanyId(coursepublish.getCompanyId());
        chooseCourse.setOrderType("700001"); //免费课程
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setStatus("701001"); //选课成功
        chooseCourse.setValidDays(365);
        chooseCourse.setValidtimeStart(LocalDateTime.now());
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(365));

        xcChooseCourseMapper.insert(chooseCourse);
        return chooseCourse;
    }
}
