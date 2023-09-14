package com.lncanswer.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lncanswer.content.mapper.TeachplanMapper;
import com.lncanswer.content.mapper.TeachplanMediaMapper;
import com.lncanswer.content.model.dto.SaveTeachplanDto;
import com.lncanswer.content.model.dto.TeachplanDto;
import com.lncanswer.content.model.po.Teachplan;
import com.lncanswer.content.model.po.TeachplanMedia;
import com.lncanswer.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description
 * @date 2023/9/12 13:16
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    private TeachplanMapper teachplanMapper;
    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;
    @Override
    public List<TeachplanDto> selectTeachplanTreeNodes(Long courId) {
        return teachplanMapper.selectTeachplanTreeNodes(courId);
    }

    /**
     * 新增或修改课程计划
     * @param saveTeachplanDto
     */
    @Override
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        //查询是否存在课程
        Long id = saveTeachplanDto.getId();
        if (id!=null){
            //id不为空说明存在课程 直接将课程进行更新
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            teachplanMapper.updateById(teachplan);
        }else {
            //根据课程id、父结点id查询同父同级别的课程计划数量
            Integer count = getTeachplanCount(saveTeachplanDto);
            //不存在则新增课程
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            //设置排序号
            teachplan.setOrderby(count + 1);
            //更新课程计划
            teachplanMapper.insert(teachplan);
        }

    }

    /**
     * 删除课程计划信息 删除大章节时，大章节下面没有小章节方可删除
     * 删除小章节的同时需要将teachplan_media表关联的信息也删除
     * @param id
     * @return
     */
    @Override
    @Transactional
    public int deleteTeachplanById(Integer id) {
        //根据课程计划id判断当前章节
        Teachplan teachplan = teachplanMapper.selectById(id);
        //根据当前课程计划对象的父节点id查询是否是小章节
        Teachplan teachplanParent = teachplanMapper.selectById(teachplan.getParentid());
        if (teachplanParent !=null){
            //如果有父节点说明不是大章节 删除小章节
            teachplanMapper.deleteById(id);
            //同时删除小章节关联的media表
            LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeachplanMedia::getTeachplanId,id);
            int i = teachplanMediaMapper.delete(queryWrapper);
            return i;
        }
        //如果是大章节 需要去判断是否还有小章节如果有小章节不允许删除
        LambdaQueryWrapper<Teachplan> lamQuery = new LambdaQueryWrapper<>();
        lamQuery.eq(Teachplan::getParentid,id);
        List<Teachplan> teachplans = teachplanMapper.selectList(lamQuery);
        //根据当前章节id查找它下面是否还有小章节
        if (teachplans != null && !teachplans.isEmpty()){
            //不为空说明查找到小章节不能删除
            return 0;
        }
        //为空直接删除当前章节
        int i = teachplanMapper.deleteById(id);
        return i;


    }

    /**
     * 根据课程id、课程计划父节点id查询课程计划数量
     * @param saveTeachplanDto
     * @return
     */
    private Integer getTeachplanCount(SaveTeachplanDto saveTeachplanDto) {
        LambdaQueryWrapper<Teachplan> lamQuery = new LambdaQueryWrapper<>();
        lamQuery.eq(Teachplan::getCourseId, saveTeachplanDto.getCourseId());
        lamQuery.eq(Teachplan::getParentid, saveTeachplanDto.getParentid());
        Integer count = teachplanMapper.selectCount(lamQuery);
        return count;
    }
}
