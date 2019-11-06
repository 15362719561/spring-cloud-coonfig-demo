package com.augurit.agsupport.map.siteFactor.domain;

import lombok.Data;

import java.util.List;

/**
 * 因子与项目关联关系表
 */
@Data
public class AeaMapSiteFactorProject {
    private String id;
    private String factorId;
    private String projectName;
    private String projectCode;
    private String effect;
    private String weight;
    private String rangeValue;
    private String remark;
    private String typeWhere;
    private String allowIntersect;

    private String parentId;
    private String name;
    private String alias;
    private String url;
    private String typeField;
    private String areaField;
    private String typeFieldCn;
    private String areaFieldCn;
    private String major;
    private Long seq;
    private String type;
    private String isDataLayer;//地块筛选图层
    private List<AeaMapSiteFactorProject> children;
}
