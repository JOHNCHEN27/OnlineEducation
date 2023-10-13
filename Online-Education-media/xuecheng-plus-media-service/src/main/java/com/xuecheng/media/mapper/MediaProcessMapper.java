package com.xuecheng.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {

    //根据分片总数和分片序号查询每个分片需要处理的任务 保证不重复
    @Select("select * from media_process t where t.id % #{shardTotal} = #{shardIndex} and (t.status = '1' or t.status ='3') and " +
            "t.fial_count < 3 limit #{count}")
    List<MediaProcess> selectListByShardIndex(int shardTotal, int shardIndex, int count);


    /*
    status=4 表示 任务处理中，乐观锁实现实现分布式处理视频保证任务只被处理一次
     */
    @Update("update media_process m  set m.status = '4' where id = #{id} and ( m.status =1 or m.status =3) and m.fail_count <3 ")
    int startTask(@Param("id") long id);
}
