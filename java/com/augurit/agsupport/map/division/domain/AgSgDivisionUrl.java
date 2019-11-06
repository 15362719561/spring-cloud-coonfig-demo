package com.augurit.agsupport.map.division.domain;

import lombok.Data;

@Data
public class AgSgDivisionUrl {
    private String id;			//主键
    private String url;			//地址
    private String description;	//内容
    private String layerType;   //图层类型
    private String keyword;		//关键字

}
