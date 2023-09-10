package com.lncanswer.content.service.impl;

import com.lncanswer.content.mapper.CourseBaseMapper;
import com.lncanswer.content.mapper.CourseCategoryMapper;
import com.lncanswer.content.mapper.CourseMarketMapper;
import com.lncanswer.content.model.dto.CourseCategoryTreeDto;
import com.lncanswer.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author LNC
 * @version 1.0
 * @description
 * @date 2023/9/10 11:52
 */
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Autowired
    private CourseMarketMapper courseMarketMapper;

    /**
     *查询课程分类树形结点数据
     * @param id
     * @return
     */
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        //查询数据库拿到课程分类树形结点集合
        List<CourseCategoryTreeDto> list = courseCategoryMapper.selectTreeNodes(id);

        //将list转成map,以便于使使用，排除根节点 将每个结点转成map中的键值对 键是id 值是结点本身
        Map<String,CourseCategoryTreeDto> map = list.stream().filter(
                //fitle过滤掉根节点  为真才会执行后面的条件
                item-> !id.equals(item.getId()))
                .collect(Collectors.toMap(
                        key -> key.getId(),value->value, // 把id值作为键，结点本身作为数据
                        (key1,key2) ->key2  //当有两个key时，以后面来的key为准
        ));

        //定义一个最终的list
        List<CourseCategoryTreeDto> courseCategoryTreeDtos =new ArrayList<>();

        //依次遍历每个元素，排除根节点 fitler条件为真才执行后面的方法 否则跳过
        list.stream().filter(item -> !id.equals(item.getId()))
                .forEach(item ->{
                    //判断当前结点是否等于传进来的根节点id
                    if(item.getParentid().equals(id)){
                        //如果是将此节点添加到最终返回的list中
                        courseCategoryTreeDtos.add(item);
                    }

                    //去转化的map集合中找到当前结点的父节点 父节点为根节点的会直接跳过
                    CourseCategoryTreeDto categoryTreeParent = map.get(item.getParentid());
                    if (categoryTreeParent != null){
                        //如果父结点不为空 存在父节点的话 判断父节点的ChildrenTreeNodes是否为空
                        if (categoryTreeParent.getChildrenTreeNodes()==null){
                            //为空，给此属性创建出来 用来存放子节点
                            categoryTreeParent.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                        }
                        categoryTreeParent.getChildrenTreeNodes().add(item);
                    }
                });
        //最后返回最终列表
        return courseCategoryTreeDtos;
    }
}
