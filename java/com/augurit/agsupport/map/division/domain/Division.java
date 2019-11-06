package com.augurit.agsupport.map.division.domain;

import lombok.Data;

@Data
public class Division {
	private String layerName;//行政区图层名称
	private String url;//图层地址
	private String nameField;//行政区名称字段
	private String codeField;//行政区编码字段
	private Division child;
}
