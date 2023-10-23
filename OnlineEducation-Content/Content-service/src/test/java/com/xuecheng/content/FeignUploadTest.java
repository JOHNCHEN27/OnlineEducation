package com.xuecheng.content;

import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author LNC
 * @version 1.0
 * @description 测试使用feign远程上传文件
 * @date 2023/10/20 13:42
 */
@SpringBootTest
public class FeignUploadTest {
    @Autowired
    MediaServiceClient mediaServiceClient;

    //Feign远程调用，上传文件
    @Test
    public void test(){
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(new File("E:\\Java\\test\\freemarkerTest\\test.html"));
        mediaServiceClient.uploadFile(multipartFile,"course","test.html");
    }

}
