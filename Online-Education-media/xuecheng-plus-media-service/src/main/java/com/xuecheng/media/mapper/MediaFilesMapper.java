package com.xuecheng.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.media.model.po.MediaFiles;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 媒资信息 Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
public interface MediaFilesMapper extends BaseMapper<MediaFiles> {

    @Delete("delete from media_files where file_id = #{mediaId}")
    int deleteFileId(String mediaId);
}
