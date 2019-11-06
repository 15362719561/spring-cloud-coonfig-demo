package com.augurit.agsupport.map.siteFactor.domain;

import lombok.Data;

import java.util.List;

/**
 * 因子对象
 */
@Data
public class AeaMapSiteFactor {
    private String id;
    private String parentId;
    private String name;
    private String alias;
    private String url;
    private String remark;
    private String typeField;
    private String areaField;
    private String typeFieldCn;
    private String areaFieldCn;
    private String major;
    private String seq;
    private String type;
    private String source;//数据来源:-1手动增加，串ID表示从图层目录关联供级联删除
    private String isDataLayer;//地块筛选图层
    private String layerType; //图层类型
    private List<AeaMapSiteFactor> children;
}
