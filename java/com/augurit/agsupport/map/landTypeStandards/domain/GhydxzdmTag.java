package com.augurit.agsupport.map.landTypeStandards.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GhydxzdmTag {
    /**
     * 父类名称
     */
    private String parent = "";

    /**
     * 土地用途代码
     */
    private String code = "";

    /**
     * 土地用途名称
     */
    private String label = "";

    /**
     * 小类名称
     */
    private String name="";

    /**
     * 个数
     */
    private int value = 0;

    /**
     * 总面积（平方米）
     */
    private double area = 0;

    /**
     * 总面积（公顷）
     */
    private double areaHa = 0;

    /**
     * 索引
     */
    private int index=0;

    /**
     * 颜色（编码）
     */
    private String color="";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 此地类的几何图形（json格式）
     */
    private List<String> geometryJson=new ArrayList<String>();
    /**
     * 中类规划用地性质
     */
    private List<GhydxzdmTag> midList = new ArrayList<GhydxzdmTag>();

    /**
     * 中类规划用地性质
     */
    private Map<String, GhydxzdmTag> midGhydxzdmTagMap = new HashMap<String, GhydxzdmTag>();

    /*
     * 返回要素的OID
     */
    private List<Integer> featureOIDs = new ArrayList<Integer>();

    public List<Integer> getFeatureOIDs() {
        return featureOIDs;
    }

    public void setFeatureOIDs(List<Integer> featureOIDs) {
        this.featureOIDs = featureOIDs;
    }

    public List<String> getGeometryJson() {
        return geometryJson;
    }

    public void setGeometryJson(List<String> geometryJson) {
        this.geometryJson = geometryJson;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getAreaHa() {
        return areaHa;
    }

    public void setAreaHa(double areaHa) {
        this.areaHa = areaHa;
    }

    public Map<String, GhydxzdmTag> getMidGhydxzdmTagMap() {
        return midGhydxzdmTagMap;
    }

    public void setMidGhydxzdmTagMap(Map<String, GhydxzdmTag> midGhydxzdmTagMap) {
        this.midGhydxzdmTagMap = midGhydxzdmTagMap;
    }

    public List<GhydxzdmTag> getMidList() {
        return midList;
    }

    public void setMidList(List<GhydxzdmTag> midList) {
        this.midList = midList;
    }
}
