package com.lncanswer.content.api;

import com.lncanswer.base.exception.OnlieEducationException;
import com.lncanswer.content.model.dto.TeachplanDto;
import com.lncanswer.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description
 * @date 2023/9/12 10:02
 */
@RestController
@Slf4j
@RequestMapping("/teachplan")
@Api(value = "课程计划编辑接口",tags = "课程计划编辑接口")
public class TeachPlanController {
    @Autowired
    private TeachplanService teachplanService;

    /**
     * 查询课程计划树形结点数据
     * @param courseId
     * @return
     */
    @ApiOperation("查询课程计划子节点")
    @GetMapping("/{courseId}/tree-nodes")
    public List<TeachplanDto> selectTeachplanTreeNodes(@PathVariable Long courseId){
        List<TeachplanDto> list = teachplanService.selectTeachplanTreeNodes(courseId);
        if (list != null){
            return list;
        }
        OnlieEducationException.cast("查询失败");
        return null;
    }
}
