package com.lncanswer.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lncanswer.content.model.dto.TeachplanDto;
import com.lncanswer.content.model.po.Teachplan;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
public interface TeachplanMapper extends BaseMapper<Teachplan> {

    public List<TeachplanDto> selectTeachplanTreeNodes(long courId);

}
