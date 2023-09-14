package com.lncanswer.content.api;

import com.lncanswer.base.exception.OnlieEducationException;
import com.lncanswer.base.exception.ValidationGroups;
import com.lncanswer.base.result.ResultClass;
import com.lncanswer.content.model.dto.SaveTeachplanDto;
import com.lncanswer.content.model.dto.TeachplanDto;
import com.lncanswer.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
     *
     * @param courseId
     * @return
     */
    @ApiOperation("查询课程计划子节点")
    @GetMapping("/{courseId}/tree-nodes")
    public List<TeachplanDto> selectTeachplanTreeNodes(@PathVariable Long courseId) {
        List<TeachplanDto> list = teachplanService.selectTeachplanTreeNodes(courseId);
        if (list != null) {
            return list;
        }
        OnlieEducationException.cast("查询失败");
        return null;
    }

    /**
     * 课程计划查询和修改
     * @param teachplanDto
     */
    @ApiOperation(value = "课程计划创建或者修改",notes = "课程id、课程父节点id不为空")
    @PostMapping
    public void saveTeachplan(@RequestBody SaveTeachplanDto teachplanDto){
        teachplanService.saveTeachplan(teachplanDto);
    }

    @ApiOperation(value = "删除课程计划",notes = "课程计划id不能为空")
    @DeleteMapping("/{id}")
    public ResultClass deleteTeachplanById(@PathVariable ("id") @Validated({
            ValidationGroups.Delete.class}) Integer id){
        int i = teachplanService.deleteTeachplanById(id);
        if (i != 0){
            return new ResultClass();
        }
        return ResultClass.error("课程计划信息还有子级信息，无法操作");

    }
}
