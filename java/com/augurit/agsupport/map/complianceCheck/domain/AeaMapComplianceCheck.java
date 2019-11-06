package com.augurit.agsupport.map.complianceCheck.domain;

import lombok.Data;

/**
 * 合规性检测图层实体类
 */
@Data
public class AeaMapComplianceCheck {
    private String id; //主键
    private String layerName; //图层名称
    private String layerWhere;//图层条件
    private Long seq;//排序序号
    private String showField;//前端显示字段
    private String typeField;//类型判断字段
    private String regulation;//规则
    private String screenshot;//是否截图1是0否
    private String status;//是否启用1是0否
    private String remark;//备注说明
    private String layerUrl;//图层服务地址
    private String layerType;//图层分类
    private String source;//数据来源:-1手动,串ID表示从图层目录关联供级联删除
    private String region;//所在区域,用于数据区分
}
