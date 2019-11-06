package com.augurit.agsupport.map.util;

import com.augurit.util.ConfigUtil;
import com.common.util.HttpRequester;
import com.common.util.HttpRespons;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

public class GeoserverUtil {
    private static JSONObject iterateElement(Element element) {
        List<Element> node = element.elements();
        JSONObject obj = new JSONObject();
        List list = null;
        for (Element child : node) {
            list = new LinkedList();
            String text = child.getTextTrim();
            if (StringUtils.isBlank(text)) {
                if (child.elements().size() == 0) {
                    continue;
                }
                if (obj.containsKey(child.getName())) {
                    list = (List) obj.get(child.getName());
                }
                list.add(iterateElement(child)); //遍历child的子节点
                obj.put(child.getName(), list);
            } else {
                if (obj.containsKey(child.getName())) {
                    Object value = obj.get(child.getName());
                    try {
                        list = (List) value;
                    } catch (ClassCastException e) {
                        list.add(value);
                    }
                }
                if (child.elements().size() == 0) { //child无子节点时直接设置text
                    obj.put(child.getName(), text);
                } else {
                    list.add(text);
                    obj.put(child.getName(), list);
                }
            }
        }
        return obj;
    }

    /**
     * 根据geoServer图层服务地址获取字段信息
     * 示例：http://192.168.32.84:8080/geoserver/GZ/wms?typeName=GZ:期末地类图斑
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static JSONArray getJSONArrayFieldsByUrl(String url) throws Exception {
        JSONArray jsonArray = new JSONArray();
        Map param = new HashMap<>();
        param.put("request", "DescribeFeatureType");
        param.put("service", "wfs");
        param.put("version", "1.0.0");
        String xmlStr = getContent(url, param);
        getFieldsByXMlStr(xmlStr, jsonArray);
        return jsonArray;
    }

    public static JSONObject getFieldsByUrl(String url) {
        JSONObject resultJSON = new JSONObject();
        resultJSON.put("success", true);
        try {
            if (StringUtils.isEmpty(url)) {//传入的服务地址为空
                resultJSON.put("success", false);
                resultJSON.put("message", "传入的服务地址为空！");
                return resultJSON;
            }
            JSONArray jsonArray = new JSONArray();
            Map param = new HashMap<>();
            param.put("request", "DescribeFeatureType");
            param.put("service", "wfs");
            param.put("version", "1.0.0");
            String xmlStr = getContent(url, param);
            getFieldsByXMlStr(xmlStr, jsonArray);
            resultJSON.put("fields", jsonArray);
        } catch (Exception e) {
            resultJSON.put("success", false);
            resultJSON.put("message", e.getMessage());
        }
        return resultJSON;
    }

    private static String getContent(String url, Map param) throws IOException {
        String content = "";
        if (StringUtils.isNotEmpty(url)) {
            url= ConfigUtil.getLanUrl(url,null);
            HttpRespons httpRespons = new HttpRequester().sendGet(url, param);
            content = httpRespons.getContent();
        }
        return content;
    }

    public static void getFieldsByXMlStr(String xmlStr, JSONArray jsonArray) throws DocumentException {
        Document doc = DocumentHelper.parseText(xmlStr);
        Element root = doc.getRootElement();
        traverseXMLFields(root, jsonArray);
    }

    public static JSONArray queryData(String url, String where, String geo) throws IOException {
        JSONArray jsonArray = new JSONArray();
        Map param = new HashMap<>();
        param.put("request", "DescribeFeatureType");
        param.put("service", "wfs");
        param.put("version", "1.0.0");
        String xmlStr = getContent(url, param);
        return jsonArray;
    }

    /**
     * 解析xml获取所有字段信息
     *
     * @param rootElement
     * @param jsonArray
     */
    private static void traverseXMLFields(Element rootElement, JSONArray jsonArray) {
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
                    traverseXMLFields(element, jsonArray);
                }
            }
        }
    }

    public static void main(String args[]) {
        String xml = "<xsd:schema xmlns:GZ=\"http://192.168.32.84:8080/geoserver/gz\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" targetNamespace=\"http://192.168.32.84:8080/geoserver/gz\">" +
                "<xsd:import namespace=\"http://www.opengis.net/gml\" schemaLocation=\"http://192.168.32.84:8080/geoserver/schemas/gml/2.1.2/feature.xsd\"/>" +
                "<xsd:complexType name=\"期末地类图斑Type\">" +
                "<xsd:complexContent>" +
                "<xsd:extension base=\"gml:AbstractFeatureType\">" +
                "<xsd:sequence>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"the_geom\" nillable=\"true\" type=\"gml:MultiPolygonPropertyType\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"OBJECTID\" nillable=\"true\" type=\"xsd:int\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"BSM\" nillable=\"true\" type=\"xsd:double\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"YSDM\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"TBH\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"XZQHDM\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"XZQHMC\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"DLBM\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"DLMJ\" nillable=\"true\" type=\"xsd:double\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"QMDLBM\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"LYYSDM\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"MS\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"LSYSXZB1\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"LSZBMJ1\" nillable=\"true\" type=\"xsd:double\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"LSYSXZB2\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"LSZBMJ2\" nillable=\"true\" type=\"xsd:double\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"LSYSXZB3\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"LSZBMJ3\" nillable=\"true\" type=\"xsd:double\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"GHFLBM\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"QMGHFLBM\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"RKSJ\" nillable=\"true\" type=\"xsd:dateTime\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"GXSJ\" nillable=\"true\" type=\"xsd:dateTime\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"GHFLMC\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"QMGHFLMC\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"DLMC\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"TBYBH\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"TBBH\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"QSXZ\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"QSDWDM\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"QSDWMC\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"ZLDWDM\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"ZLDWMC\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"GDLX\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"KCLX\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"KCDLBM\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"TKXS\" nillable=\"true\" type=\"xsd:double\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"TBMJ\" nillable=\"true\" type=\"xsd:double\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"XZDWMJ\" nillable=\"true\" type=\"xsd:double\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"LXDWMJ\" nillable=\"true\" type=\"xsd:double\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"TKMJ\" nillable=\"true\" type=\"xsd:double\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"TBDLMJ\" nillable=\"true\" type=\"xsd:double\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"PZWH\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"BGJLH\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"BGRQ\" nillable=\"true\" type=\"xsd:dateTime\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"DLBZ\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"GDPDJ\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"QNGHFLBM\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"TDYTFQDM\" nillable=\"true\" type=\"xsd:double\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"TDKJGZFQDM\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"GNFQM\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"XZM\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"TBMJXS\" nillable=\"true\" type=\"xsd:double\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"TBDLMJXS\" nillable=\"true\" type=\"xsd:double\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"ZM\" nillable=\"true\" type=\"xsd:string\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"TXMJ\" nillable=\"true\" type=\"xsd:double\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"SHAPE_Leng\" nillable=\"true\" type=\"xsd:double\"/>" +
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"SHAPE_Area\" nillable=\"true\" type=\"xsd:double\"/>" +
                "</xsd:sequence>" +
                "</xsd:extension>" +
                "</xsd:complexContent>" +
                "</xsd:complexType>" +
                "<xsd:element name=\"期末地类图斑\" substitutionGroup=\"gml:_Feature\" type=\"GZ:期末地类图斑Type\"/>" +
                "</xsd:schema>";
        JSONObject json = null;
        try {
            String jsonArray = getFieldsByUrl("http://192.168.32.84:8080/geoserver/GZ/ows?typeName=GZ:期末地类图斑").toString();
            //getFieldsByXMlStr(xml, jsonArray);
            System.out.print(jsonArray.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
