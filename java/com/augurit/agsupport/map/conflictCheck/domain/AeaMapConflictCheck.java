package com.augurit.agsupport.map.conflictCheck.domain;

import lombok.Data;
/**
 * 冲突检测实体类
 */
@Data
public class AeaMapConflictCheck {

    private String id; //主键

    private String layerName; //图层名称

    private String layerAliasName; //图层别名

    private String layerUrl; //图层地址

    private String typeField; //类型编码字段

    private String nameField; //类型名称字段

    private String remark; //备注

    private Long seq;//排序序号

    private String layerType; //图层类型
}
