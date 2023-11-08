package com.xuecheng.content;

import com.xuecheng.content.service.CoursePublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author LNC
 * @version 1.0
 * @description freemarker测试
 * @date 2023/10/20 12:43
 */
@SpringBootTest
public class FreemarkerTest {
    @Autowired
    CoursePublishService coursePublishService;

    //测试静态化页面
//    @Test
//    public void testGenerateHtmlByTemplate() throws IOException, TemplateException {
//        //配置freemarker
//        Configuration configuration = new Configuration(Configuration.getVersion());
//        //加载模板
//        //选指定的模板路径,classpath下的templates
//        String classpath = this.getClass().getResource("/").getPath();
//        configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
//        //设置字符编码
//        configuration.setDefaultEncoding("utf-8");
//
//        //指定模板文件名称
//        Template template = configuration.getTemplate("course_template.ftl");
//
//        //准备数据
//        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(2L);
//
//        Map<String,Object> map = new HashMap<>();
//        map.put("model",coursePreviewInfo);
//
//        //静态化
//        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
//        System.out.println(content);
//
//        //将静态化内容输出到文件中
//        InputStream inputStream = IOUtils.toInputStream(content);
//        //输出流
//        FileOutputStream outputStream = new FileOutputStream("E:\\Java\\test\\freemarkerTest\\test.html");
//        IOUtils.copy(inputStream,outputStream);
//    }

}
