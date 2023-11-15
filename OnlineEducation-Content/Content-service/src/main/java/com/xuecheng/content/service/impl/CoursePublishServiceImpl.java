package com.xuecheng.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.base.exception.CommonError;
import com.xuecheng.base.exception.OnlieEducationException;
import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.CourseTeacherService;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LNC
 * @version 1.0
 * @description 课程预览、课程发布信息实现类
 * @date 2023/10/16 15:56
 */
@Slf4j
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

    @Autowired
    CoursePublishMapper coursePublishMapper;

    @Autowired
    MqMessageService mqMessageService;

    @Autowired
    MediaServiceClient mediaServiceClient;



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

    /**
     * 课程发布
     * @param companyId
     * @param courseId
     */
    @Transactional
    @Override
    public void coursePublish(Long companyId, Long courseId) {
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null){
            OnlieEducationException.cast("请先提交课程审核，审核通过之后才可以发布");
        }
        //只允许本机构的来发布课程
        if (!coursePublishPre.getCompanyId().equals(companyId)){
            OnlieEducationException.cast("不允许提交其他机构的课程");
        }
        //课程审核通过才可以发布
        if (!coursePublishPre.getStatus().equals("202004")){
            OnlieEducationException.cast("课程审核通过之后才可以发布课程");
        }

        //保存课程发布信息
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre,coursePublish);
        coursePublish.setStatus("203002");
        //更新课程发布表之前查询是否存在，存在则更新不存在则插入
        CoursePublish coursePublish1 = coursePublishMapper.selectById(courseId);
        if (coursePublish1 == null){
            //课程发布表中不存在则插入
            coursePublishMapper.insert(coursePublish);
        }else {
            coursePublishMapper.updateById(coursePublish);
        }

        //插入信息到消息表中 使用SDK示例来完成
        saveCoursePublishMessage(courseId);

        //更新课程基本表的状态
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setStatus("203002");
        courseBaseMapper.updateById(courseBase);

        //删除课程预发布表的信息
        coursePublishPreMapper.deleteById(courseId);
    }

    /**
     * 生成课程静态化页面
     * @param courseId
     * @return
     */
    @Override
    public File generateCourseHtml(Long courseId) {
        //创建静态化文件
        File htmlFile = null;
        try {
            //配置freemarker
            Configuration configuration = new Configuration(Configuration.getVersion());

            //加载模板
            String classpath = this.getClass().getResource("/").getPath();
            configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
            //设置字符编码
            configuration.setDefaultEncoding("utf-8");

            //指定模板文件名称
            Template template = configuration.getTemplate("course_template.ftl");

            //准备数据
            CoursePreviewDto coursePreviewInfo = this.getCoursePreviewInfo(courseId);

            Map<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewInfo);

            //静态化
            //参数1：模板，参数2：数据模型
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
//            System.out.println(content);
            //将静态化内容输出到文件中
            InputStream inputStream = IOUtils.toInputStream(content);
            //创建静态化文件
            htmlFile = File.createTempFile("coursepublish",".html");
            log.debug("课程静态化，生成静态文件:{}",htmlFile.getAbsolutePath());
            //输出流
            FileOutputStream outputStream = new FileOutputStream(htmlFile);
            IOUtils.copy(inputStream, outputStream);


        }catch (Exception e){
            log.debug("课程静态化异常：{}",e.toString());
            OnlieEducationException.cast("课程静态化异常");
        }
        return htmlFile;
    }

    /**
     * 上传静态化文件
     * @param courseId
     * @param file
     */
    @Override
    public void uploadCourseHtml(Long courseId, File file) {
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        String course = mediaServiceClient.uploadFile(multipartFile,null,"course/"+courseId+".html");
        if (course==null){
            OnlieEducationException.cast("上传静态文件异常");
        }
    }

    /**
     * 查询课程发布信息
     * @param courseId
     * @return
     */
    @Override
    public CoursePublish getCoursepublish(Long courseId) {
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        if (coursePublish == null){
            throw new OnlieEducationException("查询发布课程不存在");
        }

        return coursePublish;
    }

    /**
     * 保存消息表记录
     * @param courseId
     */
    public void saveCoursePublishMessage(Long courseId) {
        MqMessage mqMessage = mqMessageService.addMessage("course_publish",String.valueOf(courseId),null,null);
        if (mqMessage == null){
            OnlieEducationException.cast(CommonError.UNKOWN_ERROR);
        }
    }


}
