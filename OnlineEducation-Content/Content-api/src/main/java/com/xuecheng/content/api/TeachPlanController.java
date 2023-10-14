package com.xuecheng.content.api;

import com.xuecheng.base.exception.OnlieEducationException;
import com.xuecheng.base.exception.ValidationGroups;
import com.xuecheng.base.result.ResultClass;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
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

    /**
     * 删除课程计划
     * @param id
     * @return
     */
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

    /**
     * 课程计划向下移动
     * @param id
     * @param
     * @return
     */
    @ApiOperation(value = "课程计划向下移动")
    @PostMapping("/movedown/{id}")
    public ResultClass swapTeachplanOrder(@PathVariable ("id") Integer id){
        if (id != null ){
         teachplanService.movedownTeachplan(id);
         return ResultClass.success();
        }
        return ResultClass.error("参数错误请重试");
    }

    /**
     * 课程计划向上移动
     * @param id
     * @param
     * @return
     */
    @ApiOperation(value = "课程计划向上移动")
    @PostMapping("/moveup/{id}")
    public ResultClass moveupTeachplan(@PathVariable("id") Integer id){
        if (id != null ){
            String s = teachplanService.moveupTeachplan(id);
            if (s == null){
                return ResultClass.success();
            }
            return ResultClass.error(s);
        }
        return ResultClass.error("参数错误请重试");
    }

    /**
     * 课程计划和媒资信息绑定
     * @param bindTeachplanMediaDto
     */
    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
        teachplanService.associationMedia(bindTeachplanMediaDto);
    }


}
