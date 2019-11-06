package com.augurit.agsupport.map.mapServiceInfo.arcgis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * wkt格式转esri的JSON格式 工具类
 * @author lianghuaxin
 * @date 2015-08-07
 */
public class WktToJsonUtil {
	
	/**
	 * POLYGON的wkt格式，转为esri的JSON格式
	 * 支持格式:
	 * "POLYGON((1 1,5 1,5 5,1 5,1 1))"
	 * "POLYGON((1 1,5 1,5 5,1 5,1 1),(2 2,2 3,3 3,3 2,2 2))"
	 * @param wkt
	 * @return
	 */
	public static String polygonWKTtoJson(String wkt) {
		String jsonT = "{\"rings\":[[%s]]}";
		String wkts = wkt.replace("),(",")),((");
		wkts = wkts.replace("), (",")),((");
		String regex = ".+?\\(\\((.+?)\\)\\)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(wkts);
		StringBuffer parts = new StringBuffer();
		while (matcher.find()) {
			String group = matcher.group(1).trim();
			String[] xys = group.split(",");
			String[] partArray = new String[xys.length];
			for(int i = 0; i < xys.length; i++){
				String[] xy = xys[i].trim().split(" ");
				partArray[i] = String.format("[%s,%s]", xy[0], xy[1]);
			}
			String coords = join(partArray, ',');
			if(!parts.toString().equals("")){
				parts.append("][");
			}
			parts.append(coords);
		} 
		return String.format(jsonT, parts);
	}
	
	/**
	 * MULTIPOLYGON的wkt格式，转为esri的JSON格式
	 * 支持格式:
	 * "MULTIPOLYGON (((1 1,5 1,5 5,1 5,1 1)))"
	 * "MULTIPOLYGON(((1 1,5 1,5 5,1 5,1 1)),((6 3,9 2,9 4,6 3)))"
	 * "MULTIPOLYGON(((0 0, 0 3, 3 3, 3 0, 0 0), (1 1, 1 2, 2 1, 1 1)), ((9 9, 9 10, 10 9, 9 9)))"
	 * @param wkt
	 * @return
	 */
	public static String mutilPolygonWKTtoJson(String wkt) {
		String jsonT = "{\"rings\":[[%s]]}";
		String wkts = wkt.replace(")),((","))),(((");
		wkts = wkts.replace(")), ((","))),(((");
		String regex = ".+?\\(\\(\\((.+?)\\)\\)\\)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(wkts);
		StringBuffer parts = new StringBuffer();
		while (matcher.find()) {
			String group = matcher.group(1).trim();
			String[] xys = group.split(",");
			String[] partArray = new String[xys.length];
			for(int i = 0; i < xys.length; i++){
				String[] xy = xys[i].trim().split(" ");
				partArray[i] = String.format("[%s,%s]", xy[0], xy[1]);
			}
			String coords = join(partArray, ',');
			if(!parts.toString().equals("")){
				parts.append("],[");
			}
			parts.append(coords);
		} 
		//"MULTIPOLYGON(((0 0, 0 3, 3 3, 3 0, 0 0), (1 1, 1 2, 2 1, 1 1)), ((9 9, 9 10, 10 9, 9 9)))";
		String json = parts.toString().replace("(", "[").replace(")", "]");
		return String.format(jsonT, json);
	}
	
	/**
	 * LINESTRING的wkt格式，转为esri的JSON格式
	 * 支持格式:
	 * LINESTRING(3 4,10 50,20 25)
	 * 暂时不支持，MULTILINESTRING((3 4,10 50,20 25),(-5 -8,-10 -8,-15 -4))		
	 * @param wkt
	 * @return
	 */
	public static String polylineWKTtoJson(String wkt) {
		String jsonT = "{\"paths\":[[%s]]}";
		String regex = ".+?\\((.+?)\\)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(wkt);
		if (matcher.matches()) { 
			String group = matcher.group(1).trim();
			String[] xys = group.split(",");
			String[] partArray = new String[xys.length];
			for(int i = 0; i < xys.length; i++){
				String[] xy = xys[i].trim().split(" ");
				partArray[i] = String.format("[%s,%s]", xy[0], xy[1]);
			}
			String coords = join(partArray, ',');
			return String.format(jsonT, coords);
		} else {
			return "";
		} 
	}
	
	/**
	 * POINT的wkt格式，转为esri的JSON格式
	 * 支持格式:
	 * POINT(6 10)
	 * 暂时不支持，MULTIPOINT(3.5 5.6, 4.8 10.5)
	 * @param wkt
	 * @return
	 */
	public static String pointWKTtoJson(String wkt) {
		String jsonT = "{\"x\":%s,\"y\":%s}";
		String regex = ".+?\\((.+?)\\)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(wkt);
		if (matcher.matches()) {
			String group = matcher.group(1).trim();
			String[] xy = group.split(" ");
			return String.format(jsonT, xy[0], xy[1]);
		} else {
			return "";
		} 
	}
	
	public static String join(Object[] array, char separator) {  
        if (array == null) {  
            return null;  
        }  
        int arraySize = array.length;  
        int bufSize = (arraySize == 0 ? 0 : ((array[0] == null ? 16 : array[0].toString().length()) + 1) * arraySize);  
        StringBuffer buf = new StringBuffer(bufSize);  
  
        for (int i = 0; i < arraySize; i++) {  
            if (i > 0) {  
                buf.append(separator);  
            }  
            if (array[i] != null) {  
                buf.append(array[i]);  
            }  
        }  
        return buf.toString();  
    }

}
