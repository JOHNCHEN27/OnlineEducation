package com.lncanswer.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lncanswer.content.mapper.TeachplanMapper;
import com.lncanswer.content.mapper.TeachplanMediaMapper;
import com.lncanswer.content.model.dto.SaveTeachplanDto;
import com.lncanswer.content.model.dto.TeachplanDto;
import com.lncanswer.content.model.po.Teachplan;
import com.lncanswer.content.model.po.TeachplanMedia;
import com.lncanswer.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LNC
 * @version 1.0
 * @description
 * @date 2023/9/12 13:16
 */
@Slf4j
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
     * 将课程计划向上移动：和同级目录课程计划交换位置，将两个课程计划的排序字段进行交换
     * @param id
     * @param
     */
    @Override
    public String moveupTeachplan(Integer id) {
        //通过id查找当前课程计划
        Teachplan teachplan = teachplanMapper.selectById(id);
        if (teachplan.getOrderby() == 1){
            return "无法上移";
        }
        //通过parentId找到所有同极目录结点的集合
        List<Teachplan> teachplanList = getTeachplanList(teachplan);
        teachplanList.stream().map((item) ->{
            //判断当前的同级目录结点是不是课程计划对象的前一个
           if (item.getOrderby() == teachplan.getOrderby()-1){
               //item是第三方变量 对它改变不会引起对数据改变
               //如果是则将他们俩的排序数字进行交换
               swap(teachplan, item);
           }
            return item;
        }).collect(Collectors.toList());
       return null;
    }


    /**
     * 将课程计划向下移动，交换同级目录下的课程计划
     * @param id
     * @param
     * @return
     */
    @Override
    public String movedownTeachplan(Integer id) {
        //找当前课程计划的同级目录
        Teachplan teachplan = teachplanMapper.selectById(id);
        List<Teachplan> teachplanList = getTeachplanList(teachplan);
          teachplanList.stream().map((item) ->{
            if (item.getOrderby() == (teachplan.getOrderby()+1)){
                //等于则上面当前遍历课程计划是下一级 进行交换
                swap(teachplan,item);
            }
            return item;
        }).collect(Collectors.toList());
        return null;
    }

    /**
     * 将两个同级结点的顺序进行交换
     * @param teachplan
     * @param item
     */
    private  void swap(Teachplan teachplan, Teachplan item) {
        //获取下一个课程计划的order号  1 2
        int temp = item.getOrderby();
        LambdaQueryWrapper<Teachplan> queryWrapper = getTeachplanLambdaQueryWrapper(item);
        item.setOrderby(teachplan.getOrderby());
        //更新
        teachplanMapper.update(item,queryWrapper);

        LambdaQueryWrapper<Teachplan> queryWrapper1 = getTeachplanLambdaQueryWrapper(teachplan);
        teachplan.setOrderby(temp);
        teachplanMapper.update(teachplan,queryWrapper1);

    }

    /**
     * lambda表达式构造查询条件
     * @param item
     * @return
     */
    private static LambdaQueryWrapper<Teachplan> getTeachplanLambdaQueryWrapper(Teachplan item) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, item.getCourseId());
        queryWrapper.eq(Teachplan::getParentid, item.getParentid());
        queryWrapper.eq(Teachplan::getId, item.getId());
        return queryWrapper;
    }


    /**
     * 查找同级结点集合
     * @param teachplan
     * @return
     */
    private List<Teachplan> getTeachplanList(Teachplan teachplan) {
        LambdaQueryWrapper<Teachplan> lamQuery = new LambdaQueryWrapper<>();
        lamQuery.eq(Teachplan::getParentid, teachplan.getParentid());
        lamQuery.eq(Teachplan::getCourseId,teachplan.getCourseId());
        List<Teachplan> teachplanList = teachplanMapper.selectList(lamQuery);
        return teachplanList;
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
