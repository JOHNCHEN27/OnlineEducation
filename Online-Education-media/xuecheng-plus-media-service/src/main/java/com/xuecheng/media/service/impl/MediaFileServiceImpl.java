package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.lncanswer.base.exception.OnlieEducationException;
import com.lncanswer.base.model.PageParams;
import com.lncanswer.base.model.PageResult;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @description TODO
 * @author Mr.M
 * @date 2022/9/10 8:58
 * @version 1.0
 */
@Slf4j
 @Service
public class MediaFileServiceImpl implements MediaFileService {

  @Autowired
 MediaFilesMapper mediaFilesMapper;

  //注入MinioClient委托Bean对象
 @Autowired
 MinioClient minioClient;

 //事务优化，注入代理对象 非事务方法调用事务方法事务不生效，通过提升为接口，用代理对象来调用
 @Autowired
 MediaFileService currentPoxt;

 //获取配置文件中属性，普通文件bucket
 @Value("$(minio.bucket.files)")
 private String bucket_Files;

 @Override
 public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

  //构建查询条件对象
  LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
  
  //分页对象
  Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
  // 查询数据内容获得结果
  Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
  // 获取数据列表
  List<MediaFiles> list = pageResult.getRecords();
  // 获取数据总数
  long total = pageResult.getTotal();
  // 构建结果集
  PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
  return mediaListResult;

 }

 /**
  * 上传文件实现
  * @param companyId  机构id
  * @param uploadFileParamsDto 接受请求参数类
  * @param localFilePath 文件磁盘路径
  * @return
  */
 @Override
 public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath) {
  //根据路径创建File 用来对此文件进行md5加密
  File file = new File(localFilePath);
  if (!file.exists()){
   OnlieEducationException.cast("文件不存在");
  }
  //获取文件名称
  String filename = uploadFileParamsDto.getFilename();
  //获取文件的后缀
  String extension = filename.substring(filename.lastIndexOf("."));
  //自定义方法来获取文件类型
  String mimeType = getMimeType(extension);

  //获取文件的Md5值
  String fileMd5 = getFileMd5(file);

  //文件的默认路径
  String defaultFoladerPath = getDefaultFoladerPath();

  //上传到minio中bucket桶的对象名
  String objectName = defaultFoladerPath + fileMd5 + extension;

  //将文件上传到minio中
  boolean b = addMediaFilesToMinIO(localFilePath, mimeType, bucket_Files, objectName);

  //设置文件大小
  uploadFileParamsDto.setFileSize(file.length());
  //将文件信息存储到数据库中
  MediaFiles mediaFiles = currentPoxt.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_Files, objectName);
  UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
  BeanUtils.copyProperties(mediaFiles,uploadFileResultDto);
  return uploadFileResultDto;
 }

 /**
  * 存储文件信息到数据库中
  * @param companyId
  * @param fileMd5
  * @param uploadFileParamsDto
  * @param bucketFiles
  * @param objectName
  * @return
  */
 @Transactional
 public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucketFiles, String objectName) {
  //从数据库中查询文件是否已经存在，md5值是数据库mediaFile表的主键
  MediaFiles mediaFiles =  mediaFilesMapper.selectById(fileMd5);
  if (mediaFiles == null){
   //创建一个MediaFiles存储到数据库中
   mediaFiles = new MediaFiles();
   //将基本信息拷贝
   BeanUtils.copyProperties(uploadFileParamsDto,mediaFiles);
   //设置其他信息
   mediaFiles.setId(fileMd5);
   mediaFiles.setCompanyId(companyId);
   mediaFiles.setFileId(fileMd5);
   mediaFiles.setBucket(bucketFiles);
   mediaFiles.setFilePath(objectName);
   mediaFiles.setCreateDate(LocalDateTime.now());
   mediaFiles.setAuditStatus("002003");
   mediaFiles.setStatus("1");
   //设置完成保存文件信息到数据库
   int insertNum = mediaFilesMapper.insert(mediaFiles);
   if (insertNum <=0){
    log.error("保存文件信息到数据库失败:{}",mediaFiles.toString());
    OnlieEducationException.cast("保存文件信息失败");
   }
   log.debug("保存文件信息到数据库成功:{}",mediaFiles.toString());
  }
  return mediaFiles;
 }

 /**
  * 上传文件到MinIOZ中
  * @param localFilePath
  * @param mimeType
  * @param bucket
  * @param objectName
  * @return
  */
 private boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName) {

  try {
   UploadObjectArgs uploadFile = UploadObjectArgs.builder().bucket(bucket).object(objectName).filename(localFilePath)
           .contentType(mimeType)
           .build();
   //MinioClient委托上传
   minioClient.uploadObject(uploadFile);
   log.info("上传文件成功,bucket：{},objectName:{}",bucket,objectName);
   return true;
  } catch (Exception e) {
   e.printStackTrace();
   log.error("上传文件失败，错误原因：{}",e.getMessage(),e);

  }
  return false;
 }

 /**
  * 获取文件的m默认路径
  * @return
  */
 private String getDefaultFoladerPath() {
  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
  String folder = simpleDateFormat.format(new Date()).replace("-","/") + "/";
  return folder;
 }

 /**
  * 根据文件获取文件的md5值
  * @param file
  */
 private String getFileMd5(File file) {
  try {
   FileInputStream fileInputStream = new FileInputStream(file);
   //利用工具类进行md5加密
   String fileMd5 = DigestUtils.md5Hex(fileInputStream);
   return fileMd5;
  } catch (IOException e) {
   throw new RuntimeException(e);
  }

 }

 /**
  * 根据文件后缀获取文件类型
  * @param extension
  */
 private String getMimeType(String extension) {
  if (extension == null){
   extension = "";
  }
  //根据扩展名取出mimeType
  ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
  //通用字节流
  String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
  if (extension!= null){
    mimeType = extensionMatch.getMimeType();
  }
  return mimeType;
 }
}
