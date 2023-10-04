import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author LNC
 * @version 1.0
 * @description 分块上传文件
 * @date 2023/10/3 15:25
 */
public class BigFileTest {
    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://47.113.185.5:9000")  //服务控制台
                    .credentials("@lncminio007","@lncminio007") //账号密码
                    .build();

    //分块上传文件
    @Test
    public void testUploadBigFile() throws IOException {
        //源文件路径
        File sourceFile = new File("E:\\Java\\ProjectResources\\XueChengOnline\\images\\test.mp4");
        //分块文件存储路径
        String chunkPath = "E:\\Java\\ProjectResources\\XueChengOnline\\Media\\";
        //如果路径目录不存在则创建
        File chunkFolder = new File(chunkPath);
        if (!chunkFolder.exists()){
            chunkFolder.mkdir();
        }

        //分块大小 1024k *1024 =1MB * 5 = 5MB
        long chunkSize = 1024 * 1024 *5;
        //总的分块数量 源文件的总数除以分块数量
        long chunkNum = (long) Math.ceil(sourceFile.length() * 1.0 /chunkSize);
        System.out.println("分块数量：" + chunkNum);

        //读取源文件
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(sourceFile));
        //缓冲区大小 一次读取1024字节
        byte [] bytes = new byte[1024];
        //分块读取
        for (int i = 0; i < chunkNum; i++) {
            //每次读取一个块创建一个文件，使得每一个块对应一个文件
            File file = new File(chunkPath+i);
            if (file.exists()){
                file.delete();
            }
            boolean newFile = file.createNewFile();
            if (newFile) {
                //创建一个缓冲输出流，向文件中写入数据
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
                int len;
                while ((len = bufferedInputStream.read(bytes)) != -1) {
                    //写入目标路径 从bytes数组中第一个位置写，读取多少len数量，就写多少
                    bufferedOutputStream.write(bytes, 0, len);
                    //一个块只写固定的块大小 这里为5MB
                    if (file.length() >= chunkSize){
                        break;
                    }
                }
                bufferedOutputStream.close();
            }
        }

        //关闭流
        bufferedInputStream.close();
    }

    //合并分块文件
    @Test
    public void testMergeBlockFile() throws IOException {
        //所有分块文件的目录
        File chunkFolder = new File("E:\\Java\\ProjectResources\\XueChengOnline\\Media");
        //源文件路径 用来和合并之后的文件做对比
        File sourceFile = new File("E:\\Java\\ProjectResources\\XueChengOnline\\images\\test.mp4");
        //合并的文件
        File mergeFile = new File("E:\\Java\\ProjectResources\\XueChengOnline\\Media_Merge\\MergeTest.mp4");
        //判断此文件是否存在
        if (mergeFile.exists()){
            mergeFile.delete();
        }
        boolean newFile = mergeFile.createNewFile();
        if (newFile){
            //获取分块路径下的所有分块文件
            File[] files = chunkFolder.listFiles();
            //利用数组工具类将类型转换为List类型
            List<File> list = Arrays.asList(files);
            //利用Collections工具类进行按分块文件名称进行升序排序
            Collections.sort(list, Comparator.comparingInt(o -> Integer.parseInt(o.getName())));
            //创建缓冲字节输出流 向合并的文件中写数据
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(mergeFile));
            int len = 0;
            byte [] bytes = new byte[1024];
            //增加for遍历list所有的分块文件
            for (File file : list) {
                //往合并文件中写数据
                BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
                while ((len = bufferedInputStream.read(bytes))!= -1){
                    bufferedOutputStream.write(bytes,0,len);
                }
                bufferedInputStream.close();
            }
            bufferedOutputStream.close();
        }
        //利用md5加密对合并之后的流对比 看是否成功合并
        FileInputStream fileInputStream = new FileInputStream(sourceFile);
        FileInputStream mergeInputStream = new FileInputStream(mergeFile);
        String sourceMD5 = DigestUtils.md5Hex(fileInputStream);
        String mergeMD5 = DigestUtils.md5Hex(mergeInputStream);
        if (sourceMD5.equals(mergeMD5)){
            System.out.println("文件合并成功");
        }else{
            System.out.println("文件合并失败");
        }
    }

    //minio上传分块文件
    @Test
    public void testUploadChunkFile() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //获取分块文件的路径
        File chunkFiles = new File("E:\\Java\\ProjectResources\\XueChengOnline\\Media");
        //拿到文件夹下所有的文件
        File[] files = chunkFiles.listFiles();
        //将所有的分块文件上传到minio
        for (int i = 0; i < files.length; i++) {
            UploadObjectArgs bucket = UploadObjectArgs.builder().bucket("testbucket").filename(files[i].getAbsolutePath())
                    .object("chunkFiles/" + i).build();
            minioClient.uploadObject(bucket);
        }
    }

    //合并minio上传的分块 Minio要求合并的大小最少为5MB
    @Test
    public void testMergeChunkFile() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //获取分块文件的路径
        File chunkFiles = new File("E:\\Java\\ProjectResources\\XueChengOnline\\Media");
        //拿到文件夹下所有的文件
        File[] files = chunkFiles.listFiles();
        //利用stream流来获取合并的源文件列表（每一个分块文件）
        List<ComposeSource> composeSources = Stream.iterate(0, i -> ++i)//从第一个位置开始循环，每循环一次加1
                .limit(files.length) //取所有的分块文件，这里是3
                .map(i -> ComposeSource.builder()
                        .bucket("testbucket")
                        .object("chunkFiles/" + i)
                        .build()) //类型转换
                .collect(Collectors.toList());
        //利用minio提供的ComposeObjectArgs来合并分块文件，分块文件最小必须为5mb
        ComposeObjectArgs build = ComposeObjectArgs.builder().bucket("testbucket").object("mergeFiles/merge01.mp4")
                .sources(composeSources).build();

        //minio上传合并之后的文件
        minioClient.composeObject(build);
    }

    //清除分块文件
    @Test
    public void testCleanChunkFiles(){
        //合并完成分块将分块文件清除 利用stream流
        List<DeleteObject> collect = Stream.iterate(0, i -> ++i)
                .limit(3)
                .map(i -> new DeleteObject("chunkFiles/" + i))
                .collect(Collectors.toList());
        //利用RemoveObjectsArgs对象删除列表
        RemoveObjectsArgs bucket = RemoveObjectsArgs.builder().bucket("testbucket")
                .objects(collect).build();
        minioClient.removeObjects(bucket);
    }

}
