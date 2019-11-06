package com.augurit.agsupport.map.mapServiceInfo.arcgis;

import java.io.IOException;

public class GeometryUtil {
	public WktToJsonUtil WktToJsonUtil = new WktToJsonUtil();
	
	/**
	 * 面，逆时针转顺时针
	 * .net方式获取的面一般是逆时针，需要转成顺时针，才能进行分析、计算
	 * @param polygon
	 * @return
	 * @throws IOException
	 */
	public static String turnEasternToClockwise(String polygon) throws Exception{
		String geometry = "{rings:[";
		polygon = polygon.replaceAll(" ", "");
		polygon = polygon.replaceAll("\\{", "");
		polygon = polygon.replaceAll("}", "");
		int end_i =polygon.indexOf("]]]");
		polygon =polygon.substring(polygon.indexOf("[[[")+3,end_i);
		String[] arr=polygon.split("]],\\[\\[");
		for(int i = 0;i<arr.length;i++){
			String[] xy_str= arr[i].split("],\\[");
			for(int j=xy_str.length -1;j>=0;j--){
				if(j == xy_str.length -1){
					geometry += "[["+xy_str[j]+"]";
				}else if(0<j&&j<xy_str.length -1){
					geometry +=",["+xy_str[j]+"]";
				}else{
					geometry +=",["+xy_str[j]+"]],";
				}
			}
			if(i == arr.length -1){
				geometry += "]}";
			}
		}
		return geometry;
	}
	
	/**
	 * 计算pologons的面积
	 * 
	 * @param polygons 格式：[geo1,geo2...]
	 * @return
	 * @throws IOException
	 */
	public static String callGeoAreaService(String polygons) throws Exception {
		polygons = polygons.replaceAll(" ", "");
		String[] rings = polygons.split("},\\{");
		Double tempArea = 0d;
		for(int k = 0;k<rings.length;k++){
			rings[k] = rings[k].replaceAll("\\{", "");
			rings[k] = rings[k].replaceAll("}", "");
			int end_i =rings[k].indexOf("]]]");
			rings[k] =rings[k].substring(rings[k].indexOf(":")+1,end_i);
			String[] arr=rings[k].split("]],\\[\\[");
			for(int i = 0;i<arr.length;i++){
				Double x1 = 0d;
				Double x2 = 0d;
				Double y1 = 0d;
				Double y2 = 0d;
				String[] xy_str= arr[i].split("],\\[");
				for(int j = 0;j<xy_str.length-1;j++){
					xy_str[j] =xy_str[j].replaceAll("\\[", "");
					xy_str[j] =xy_str[j].replaceAll("]", "");
					xy_str[j+1] =xy_str[j+1].replaceAll("\\[", "");
					xy_str[j+1] =xy_str[j+1].replaceAll("]", "");
					String[] sys1= xy_str[j].split(",");
					String[] sys2= xy_str[j+1].split(",");
					x1 =Double.valueOf(sys1[0]);
					y1 =Double.valueOf(sys1[1]);
					x2 =Double.valueOf(sys2[0]);
					y2 =Double.valueOf(sys2[1]);
					tempArea += (x1 + x2) * (y1 - y2);
				}
			}
		}
		return String.valueOf(Math.abs(tempArea)/2);
	}
	
}
