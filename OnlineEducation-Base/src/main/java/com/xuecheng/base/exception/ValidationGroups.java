package com.xuecheng.base.exception;

/**
 * @author LNC
 * @version 1.0
 * @description 分组校验
 * @date 2023/9/11 9:29
 */
public class ValidationGroups {
    public interface Inster{};
    public interface Update{};
    public interface Delete{};

    /**
     * 有时候在同一个属性上设置一个校验规则不能满足要求，
     * 比如：订单编号由系统生成，在添加订单时要求订单编号为空，
     * 在更新 订单时要求订单编写不能为空。此时就用到了分组校验，
     * 同一个属性定义多个校验规则属于不同的分组，
     * 比如：添加订单定义@NULL规则属于insert分组，
     * 更新订单定义@NotEmpty规则属于update分组，
     * insert和update是分组的名称，是可以修改的。
     */
}
