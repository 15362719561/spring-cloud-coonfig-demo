package com.augurit.agsupport.map.landTypeStandards.domain;

import lombok.Data;

import java.util.List;

@Data
public class AgSgLandType {
    private String id;              //主键
    private String code;            //代码
    private String name;            //名称
    private String parentId;        //父id
    private String versionCode;     //版本，如GB50137-2011
    private String type;            //类型
    private String color;           //颜色
    private String lineColor;       //边界颜色
    private String codeSort;        //级别
    private Boolean editStatus;     //是否编辑状态（前端使用）
    private String rowKey;          //行唯一关键字（前端使用）
    private String isDefault;       //是否默认加载版本
    private List<AgSgLandType> children;
}
