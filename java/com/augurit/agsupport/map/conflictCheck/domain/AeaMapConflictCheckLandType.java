package com.augurit.agsupport.map.conflictCheck.domain;

import lombok.Data;
@Data
public class AeaMapConflictCheckLandType {
    private String id; //主键
    private String landName; //用地类型名称
    private String landCode; //用地类型代码
    private String construct; //（0:非建设用地,1：建设用地）
    private String layerType; //关联的冲突图层id
}
