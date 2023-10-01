package com.xuecheng.media.api;

import com.alibaba.nacos.common.http.param.MediaType;
import com.lncanswer.base.model.PageParams;
import com.lncanswer.base.model.PageResult;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @description 媒资文件管理接口
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
 @Api(value = "媒资文件管理接口",tags = "媒资文件管理接口")
 @RestController
public class MediaFilesController {


  @Autowired
  MediaFileService mediaFileService;


 @ApiOperation("媒资列表查询接口")
 @PostMapping("/files")
 public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto){
  Long companyId = 1232141425L;
  return mediaFileService.queryMediaFiels(companyId,pageParams,queryMediaParamsDto);

 }

    /**
     * 上传文件
     * multipart/form-data表单文件上传  consumes指定前端请求内容类型
     * @param file
     * @return
     */
   @ApiOperation("上传文件")
   @RequestMapping(value = "/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA)
    public UploadFileResultDto upload(@RequestPart("filedata")MultipartFile file,
                                      @RequestParam(value = "folder",required = false) String folder,
                                      @RequestParam(value = "objectName",required = false) String objectName) throws IOException {
       //硬编码
       Long companyId = 1232141425L;
       UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
       //封装参数信息
       uploadFileParamsDto.setFileSize(file.getSize());
       uploadFileParamsDto.setFileType("001001"); //文件类型图片
       //文件名称
       uploadFileParamsDto.setFilename(file.getOriginalFilename());
       //创建临时文件
       File tempFile = File.createTempFile("minio","temp");
       //上传的文件拷贝到临时文件
       file.transferTo(tempFile);
       //文件路径
       String absolutePath = tempFile.getAbsolutePath();
       //上传文件
       UploadFileResultDto uploadFileResultDto = mediaFileService.uploadFile(companyId, uploadFileParamsDto, absolutePath);
       return uploadFileResultDto;

   }


}
