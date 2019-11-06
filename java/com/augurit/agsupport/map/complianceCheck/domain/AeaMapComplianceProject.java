package com.augurit.agsupport.map.complianceCheck.domain;

import lombok.Data;

/**
 * 合规性检测图层与项目类型关联关系表
 */
@Data
public class AeaMapComplianceProject {
    private String id;
    private String itemCode;
    private String itemName;
    private String regulation;
    private String status;
    private String screenshot;
    private String rangeCode;
    private String rangeCn;

    private String checkId;
    private String layerName; //图层名称
    private String layerWhere;//图层条件
    private String showField;//前端显示字段
    private String typeField;//类型判断字段
    private String layerUrl;//图层服务地址
    private String layerType;//图层分类
    private String remark; //图层标识
    private String region;////所在区域,用于数据区分
}
