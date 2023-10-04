package com.xuecheng.media.api;

import com.lncanswer.base.result.RestResponse;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author LNC
 * @version 1.0
 * @description 大文件上传接口
 * @date 2023/10/4 11:04
 */
@Api(value = "大文件上传接口",tags = "大文件上传接口")
@RestController
public class BigFilesController {

    @Autowired
    MediaFileService mediaFileService;
    /**
     * 上传之前检查文件是否上传
     * @param fileMd5
     * @return
     */
    @ApiOperation(value = "文件上传前检查文件")
    @PostMapping("/upload/checkfile")
    public RestResponse checkFiles(@RequestParam("fileMd5") String fileMd5){
        return mediaFileService.checkFile(fileMd5);
    }

    /**
     * 检查分块文件是否上传
     * @param fileMd5
     * @param chunk
     * @return
     */
    @ApiOperation(value = "分块文件上传的检测")
    @PostMapping("/upload/checkchunk")
    public RestResponse checkChunk(@RequestParam ("fileMd5")String fileMd5,@RequestParam("chunk") int chunk){
        return mediaFileService.checkChunk(fileMd5,chunk);
    }

    /**
     * 将分块文件上传
     * @param file
     * @param fileMd5
     * @param chunk
     * @return
     */
    @ApiOperation(value = "上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse uploadChunk(@RequestParam("file")MultipartFile file,@RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("chunk") int chunk) throws IOException {
        //创建临时文件
        File tempFile = File.createTempFile("minio",".temp");
        //上传文件拷贝到临时文件
        file.transferTo(tempFile);
        //获取文件的路径
        String absolutePath = tempFile.getAbsolutePath();
        return mediaFileService.uploadChunk(fileMd5,chunk,absolutePath);
    }


    /**
     * 合并分块文件
     * @param fileMd5
     * @param fileName
     * @param chunkTotal
     * @return
     */
    @ApiOperation(value = "合并分块文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse mergeChunks(@RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("fileName") String fileName,
                                    @RequestParam("chunkTotal") int chunkTotal){
        Long companyId = 1232141425L;
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFileType("001002");
        uploadFileParamsDto.setTags("课程视频");
        uploadFileParamsDto.setRemark("");
        uploadFileParamsDto.setFilename(fileName);

        return mediaFileService.mergeChunks(companyId,fileMd5,chunkTotal,uploadFileParamsDto);

    }


}
