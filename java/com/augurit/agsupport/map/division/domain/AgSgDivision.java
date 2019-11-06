package com.augurit.agsupport.map.division.domain;

import lombok.Data;

@Data
public class AgSgDivision {
    private String id;                //主键
    private String code;            //代码
    private String name;            //名称
    private String description;        //内容
    private String pcode;        //父代码
    private String refUrl;        //关联图层序号
    private String sortId;        //行政区划排序序号

    private String url;
    private String keyword;

}
