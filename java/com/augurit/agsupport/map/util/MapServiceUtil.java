package com.augurit.agsupport.map.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.augurit.util.ConfigUtil;
import com.common.util.HttpRequester;
import com.common.util.HttpRespons;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapServiceUtil {
    /**
     * 获取geoserver wfs服务字段信息
     * @param url
     * @return
     */
    public static JSONArray getGeoServerWFSLayerFields(String url) {
        try{
            JSONArray jsonArray = new JSONArray();
            Map param = new HashMap();
            param.put("request", "DescribeFeatureType");
            param.put("service", "wfs");
            param.put("version", "1.0.0");
            String resultXmlStr = getResponseContent(url,param);
            Document doc = DocumentHelper.parseText(resultXmlStr);
            Element root = doc.getRootElement();
            traverseGeoServerWFSFieldsXML(root, jsonArray);
            return jsonArray;
        }catch (Exception e) {
            return new JSONArray();
        }
    }

    /**
     * 获取arcgis dynamiclayer 服务字段信息
     * @param url
     * @return
     */
    public static JSONArray getArcGISDynamicLayerFields(String url) {
        try{
            Map<String, String> param = new HashMap<>();
            param.put("f", "json");
            String resultStr = getResponseContent(url,param);
            JSONObject obj = JSONObject.parseObject(resultStr);
            JSONArray fields = new JSONArray();
            if(obj.containsKey("fields")) {
                fields = obj.getJSONArray("fields");
            }
            return fields;
        }catch (Exception e) {
            return new JSONArray();
        }
    }

    /**
     * 获取arcgis dynamiclayer 服务图层名称
     * @param url
     * @return
     */
    public static String getArcGISDynamicLayerName(String url) {
        String layerName = "";
        try{
            Map<String, String> param = new HashMap<>();
            param.put("f", "json");
            String resultStr = getResponseContent(url,param);
            JSONObject obj = JSONObject.parseObject(resultStr);
            if(obj.containsKey("name")) {
                layerName = obj.getString("name");
            }
            return layerName;
        }catch (Exception e) {
            return layerName;
        }
    }

    /**
     * 获取arcgis wfs 服务图层名称
     * @param url
     * @return
     */
    public static String getArcGISWFSLayerName(String url) {
        String layerName = "";
        try{
            layerName = url.split("\\?")[1].split("=")[1];
            return layerName;
        }catch (Exception e) {
            return layerName;
        }
    }

    /**
     * geoserver 服务图层名称
     * @param url
     * @return
     */
    public static String getGeoServerLayerName(String url) {
        String layerName = "";
        try{
            layerName = url.split("\\?")[1].split("=")[1];
            return layerName;
        }catch (Exception e) {
            return layerName;
        }
    }

    /**
     * 获取arcgis wfs服务字段信息
     * @param url
     * @return
     */
    public static JSONArray getArcGISWFSLayerFields(String url) {
        try{
            JSONArray jsonArray = new JSONArray();
            Map param = new HashMap();
            param.put("request", "DescribeFeatureType");
            param.put("service", "wfs");
            param.put("version", "1.0.0");
            String resultXmlStr =  getResponseContent(url,param);
            Document doc = DocumentHelper.parseText(resultXmlStr);
            Element root = doc.getRootElement();
            traverseArcGISWFSFieldsXML(root, jsonArray);
            return jsonArray;
        }catch (Exception e) {
            return new JSONArray();
        }
    }


    /**
     * 获取请求结果
     * @param url
     * @param param
     * @return
     * @throws IOException
     */
    private static String getResponseContent(String url, Map param) throws IOException {
        String content = "";
        if (StringUtils.isNotEmpty(url)) {
            url= ConfigUtil.getLanUrl(url,null);
            HttpRespons httpRespons = new HttpRequester().sendGet(url, param);
            content = httpRespons.getContent();
        }
        return content;
    }

    /**
     * 解析geoserver 图层字段信息xml获取所有字段信息
     *
     * @param rootElement
     * @param jsonArray
     */
    private static void traverseGeoServerWFSFieldsXML(Element rootElement, JSONArray jsonArray) {
        List<Element> elements = rootElement.elements();
        if (elements.size() > 0) {
            for (Iterator<Element> it = elements.iterator(); it.hasNext(); ) {
                Element element = (Element) it.next();
                if (element.getName().equalsIgnoreCase("sequence")) {
                    List<Element> childElements = element.elements();
                    for (Iterator<Element> iterator = childElements.iterator(); iterator.hasNext(); ) {
                        Element childElement = (Element) iterator.next();
                        if (childElement.getName().equalsIgnoreCase("element")) {
                            String value = childElement.attributeValue("name");
                            JSONObject jsonObject = new JSONObject();
                            if(childElement.elements().size() == 0) {
                                if (childElement.attributeValue("type") != null && childElement.attributeValue("type").indexOf("MultiPolygonPropertyType") == -1) {
                                    jsonObject.put("name", value);
                                    jsonObject.put("alias", value);
                                    jsonObject.put("type", childElement.attributeValue("type").replace("xsd:", "").replace("xs:",""));
                                    jsonArray.add(jsonObject);
                                }
                            }else {
                                jsonObject.put("name", value);
                                jsonObject.put("alias", value);
                                Element simpleTypeElement = (Element) childElement.elements().get(0);
                                if(simpleTypeElement.elements().size() > 0) {
                                    Element restrictionElement = (Element) simpleTypeElement.elements().get(0);
                                    if(restrictionElement.attributeValue("base") != null) {
                                        jsonObject.put("type",restrictionElement.attributeValue("base").replace("xs:",""));
                                    }
                                }
                                jsonArray.add(jsonObject);
                            }
                        }
                    }
                } else {
                    traverseGeoServerWFSFieldsXML(element, jsonArray);
                }
            }
        }
    }


    /**
     * 解析arcgis图层字段信息xml获取所有字段信息
     *
     * @param rootElement
     * @param jsonArray
     */
    private static void traverseArcGISWFSFieldsXML(Element rootElement, JSONArray jsonArray) {
        List<Element> elements = rootElement.elements();
        if (elements.size() > 0) {
            for (Iterator<Element> it = elements.iterator(); it.hasNext(); ) {
                Element element = (Element) it.next();
                if (element.getName().equalsIgnoreCase("sequence")) {
                    List<Element> childElements = element.elements();
                    for (Iterator<Element> iterator = childElements.iterator(); iterator.hasNext(); ) {
                        Element childElement = (Element) iterator.next();
                        if (childElement.getName().equalsIgnoreCase("element")) {
                            String value = childElement.attributeValue("name");
                            JSONObject jsonObject = new JSONObject();
                            if (childElement.elements().size() == 0) {
                                if (childElement.attributeValue("type") != null && childElement.attributeValue("type").indexOf("MultiPolygonPropertyType") == -1) {
                                    jsonObject.put("name", value);
                                    jsonObject.put("alias", value);
                                    jsonObject.put("type", childElement.attributeValue("type").replace("xsd:", "").replace("xs:", ""));
                                    jsonArray.add(jsonObject);
                                }
                            } else {
                                jsonObject.put("name", value);
                                jsonObject.put("alias", value);
                                Element simpleTypeElement = (Element) childElement.elements().get(0);
                                if (simpleTypeElement.elements().size() > 0) {
                                    Element restrictionElement = (Element) simpleTypeElement.elements().get(0);
                                    if (restrictionElement.attributeValue("base") != null) {
                                        jsonObject.put("type", restrictionElement.attributeValue("base").replace("xs:", ""));
                                    }
                                }
                                jsonArray.add(jsonObject);
                            }
                        }
                    }
                } else {
                    traverseGeoServerWFSFieldsXML(element, jsonArray);
                }
            }
        }
    }
}
