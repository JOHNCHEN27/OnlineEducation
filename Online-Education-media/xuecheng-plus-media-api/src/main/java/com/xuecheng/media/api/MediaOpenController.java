package com.xuecheng.media.api;

import com.xuecheng.base.exception.OnlieEducationException;
import com.xuecheng.base.result.RestResponse;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LNC
 * @version 1.0
 * @description 媒资文件管理
 * @date 2023/10/16 18:32
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RequestMapping("/open")
@RestController
public class MediaOpenController {

    @Autowired
    MediaFileService mediaFileService;

    /**
     * 查询视频播放的url地址
     * @param mediaId
     * @return
     */
    @ApiOperation("预览文件")
    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable("mediaId") String mediaId){
        MediaFiles mediaFiles = mediaFileService.getFileById(mediaId);
        if (mediaFiles ==null || StringUtils.isEmpty(mediaFiles.getUrl())){
            OnlieEducationException.cast("视频还未转码处理");
        }
        return RestResponse.success(mediaFiles.getUrl());
    }
}
