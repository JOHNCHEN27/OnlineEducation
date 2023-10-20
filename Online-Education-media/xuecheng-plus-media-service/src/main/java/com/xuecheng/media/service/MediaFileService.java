package com.xuecheng.media.service;


import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.result.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;

import java.io.File;

/**
 * @description 媒资文件管理业务类
 * @author Mr.M
 * @date 2022/9/10 8:55
 * @version 1.0
 */
public interface MediaFileService {

 /**
  * @description 媒资文件查询方法
  * @param pageParams 分页参数
  * @param queryMediaParamsDto 查询条件
  * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
  * @author Mr.M
  * @date 2022/9/10 8:57
 */
 public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);


 /**
  * 上传文件
  * @param companyId  机构id
  * @param uploadFileParamsDto 接受请求参数类
  * @param localFilePath 文件磁盘路径
  * @return
  */
 public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath,String objectName);

 //代理对象方法 事务优化
 public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucketFiles, String objectName);

 /**
  * 检查文件是否存在
  * @param fileMd5 文件的md5加密
  * @return
  */
 public RestResponse checkFile(String fileMd5);

 /**
  * 检查分块是否存在
  * @param fileMd5
  * @param chunkIndex 分块的序号
  * @return
  */
 public RestResponse checkChunk(String fileMd5,int chunkIndex);

 /**
  * 上传分块
  * @param fileMd5 文件的md5
  * @param chunk   分块序号
  * @param
  * @return
  */
 public RestResponse uploadChunk(String fileMd5,int chunk, String localChunkFilePath);

 /**
  * 合并分块文件
  * @param companyId 机构id
  * @param fileMd5 文件md5加密
  * @param chunkTotal 分块总数量
  * @param uploadFileParamsDto 文件信息
  * @return
  */
 public RestResponse mergeChunks(Long companyId,String fileMd5,int chunkTotal,UploadFileParamsDto uploadFileParamsDto);

 //从minio中下载视频
 File downloadFileFromMinIO(String bucketVideofiles, String chunkFilePath);

 //添加文件到minio
 boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName);

 int removeassociationMedia(Long teachPlanId, String mediaId);

 MediaFiles getFileById(String mediaId);
}
