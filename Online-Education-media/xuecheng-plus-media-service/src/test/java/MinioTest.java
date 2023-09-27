import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

/**
 * @author LNC
 * @version 1.0
 * @description Minio测试
 * @date 2023/9/27 15:35
 */
public class MinioTest {
    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://47.113.185.5:9000")  //服务控制台
                    .credentials("@lncminio007","@lncminio007") //账号密码
                    .build();

    /**
     * 上传文件
     */
    @Test
    public void  upload(){
        //通过扩展名得到媒体资源类型 mimeType --需要导入相应的依赖，在base工程的pom文件中已经导入
        //根据扩展名取出mimeType  输入后缀
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".png");
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE; //字节流
        //判断获取的扩展名是否为空
        if (extensionMatch != null){
            mimeType = extensionMatch.getMimeType();
        }
        try {
            UploadObjectArgs  testbucket = UploadObjectArgs.builder()
                    .bucket("testbucket")  //存放数据的bucket桶
                    .filename("E:\\Java\\ProjectResources\\XueChengOnline\\images\\1.png")
                    //.contentType("image/png") //默认根据扩展名确定文件内容类型，也可以指定
                    //.object("1.png")  //默认放在当前桶下，可以指定子目录
                    .object("test/images/1.png")  //指定子目录
                    .contentType("image/png")   //指定扩展名
                    .build();
            //上传文件
            minioClient.uploadObject(testbucket);
            System.out.println("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("上传失败");
        }
    }

    /**
     * 删除文件
     */
    @Test
    public void testDelete() throws Exception {
        //构建remove对象
        RemoveObjectArgs removeObjectArgs =RemoveObjectArgs
                .builder().bucket("testbucket").object("1.png").build();
        //调用MinioClient删除
        minioClient.removeObject(removeObjectArgs);
    }


    /**
     * 查询文件、下载文件
     */
    @Test
    public void testSelectFile()  {
        //构建查询对象
        GetObjectArgs selectBucket = GetObjectArgs.builder().bucket("testbucket").object("test/images/1.png").build();

        try {
            //通过MinioClient查询该对象 --用字节输入流读取文件系统中的对象
            GetObjectResponse inputStream = minioClient.getObject(selectBucket);
            //利用缓冲流写出文件到本地
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("E:\\Java\\ProjectResources\\XueChengOnline\\images\\2.png"));
            //调用apache-commoms工具包写出文件
            IOUtils.copy(inputStream,bufferedOutputStream);

            //检验文件的完整性，利用md5加密
            String source_md5 = DigestUtils.md5Hex(inputStream.toString());
            //下载文件、查询文件的md5加密
            String download_md5 = DigestUtils.md5Hex(bufferedOutputStream.toString());
            System.out.println(source_md5);
            System.out.println(download_md5);
            if (source_md5.equals(download_md5)){
                System.out.println("下载成功");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
