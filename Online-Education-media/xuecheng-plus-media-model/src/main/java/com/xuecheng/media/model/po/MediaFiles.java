package com.xuecheng.media.model.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 媒资信息
 * </p>
 *
 * @author itcast
 */
@Data
@TableName("media_files")
public class MediaFiles implements Serializable {

    private static final long serialVersionUID = 1L;

    public MediaFiles(String id, Long companyId, String companyName, String filename, String fileType, String tags, String bucket, String filePath, String fileId, String url, String username, LocalDateTime createDate, LocalDateTime changeDate, String status, String remark, String auditStatus, String auditMind, Long fileSize) {
        this.id = id;
        this.companyId = companyId;
        this.companyName = companyName;
        this.filename = filename;
        this.fileType = fileType;
        this.tags = tags;
        this.bucket = bucket;
        this.filePath = filePath;
        this.fileId = fileId;
        this.url = url;
        this.username = username;
        this.createDate = createDate;
        this.changeDate = changeDate;
        this.status = status;
        this.remark = remark;
        this.auditStatus = auditStatus;
        this.auditMind = auditMind;
        this.fileSize = fileSize;
    }

    public MediaFiles() {
    }

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 机构ID
     */
    private Long companyId;

    /**
     * 机构名称
     */
    private String companyName;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件类型（文档，音频，视频）
     */
    private String fileType;

    /**
     * 标签
     */
    private String tags;

    /**
     * 存储目录
     */
    private String bucket;

    /**
     * 存储路径
     */
    private String filePath;


    /**
     * 文件标识
     */
    private String fileId;

    /**
     * 媒资文件访问地址
     */
    private String url;


    /**
     * 上传人
     */
    private String username;

    /**
     * 上传时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime changeDate;

    /**
     * 状态,1:未处理，视频处理完成更新为2
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 审核状态
     */
    private String auditStatus;

    /**
     * 审核意见
     */
    private String auditMind;

    /**
     * 文件大小
     */
    private Long fileSize;

}
