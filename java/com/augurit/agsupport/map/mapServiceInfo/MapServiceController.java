package com.augurit.agsupport.map.mapServiceInfo;

import com.augurit.agsupport.map.util.ArcgisServiceUtil;
import com.augurit.agsupport.map.util.CommonUtil;
import com.augurit.agsupport.map.util.GeoserverUtil;
import com.augurit.agsupport.map.util.MapServiceUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/agsupport/mapService")
public class MapServiceController {
    @RequestMapping("/getFields")
    public String getFieldsByUrl(String url) {
        JSONObject resultJSON = new JSONObject();
        resultJSON.put("success", true);
        try {
            String mapType = CommonUtil.getAgmpiType();
            JSONArray fields = new JSONArray();
            if(CommonUtil.LEAF_LET.equals(mapType)) {
                fields = GeoserverUtil.getFieldsByUrl(url).getJSONArray("fields");
            }else if(CommonUtil.ARC_GIS.equals(mapType)) {
                fields = ArcgisServiceUtil.getFieldsByUrl(url).getJSONArray("fields");
            }
            resultJSON.put("fields", fields);
        } catch (Exception e) {
            resultJSON.put("success", false);
            resultJSON.put("message", "获取字段列表出错！");
            e.printStackTrace();
        }
        return resultJSON.toString();
    }

    @RequestMapping("/getFieldsByLayerType")
    public String getFieldsByLayerType(String url,String layerType) {
        JSONObject resultJSON = new JSONObject();
        com.alibaba.fastjson.JSONArray fields = new com.alibaba.fastjson.JSONArray();
        try {
            resultJSON.put("success", true);
            switch (layerType) {
                case "020202":  //ArcGIS MapServer(Dynamic)
                    fields = MapServiceUtil.getArcGISDynamicLayerFields(url);
                    break;
                case "040002":  //ArcGIS WFS
                    fields = MapServiceUtil.getArcGISWFSLayerFields(url);
                    break;
                case "040003":  //GeoServer WFS
                    fields = MapServiceUtil.getGeoServerWFSLayerFields(url);
                    break;
                case "030003":  //GeoServer WMS
                    fields = MapServiceUtil.getGeoServerWFSLayerFields(url);
                    break;
            }
            resultJSON.put("fields",fields);
        }catch (Exception e) {
            resultJSON.put("success", false);
            resultJSON.put("fields", new JSONArray());
            resultJSON.put("message", "获取字段列表出错！");
            e.printStackTrace();
        }
        return resultJSON.toString();
    }


    @RequestMapping("/getNameByLayerType")
    public String getLayerNameByLayerType(String url,String layerType) {
        JSONObject resultJSON = new JSONObject();
        String layerName = "";
        try {
            resultJSON.put("success", true);
            switch (layerType) {
                case "020202":  //ArcGIS MapServer(Dynamic)
                    layerName = MapServiceUtil.getArcGISDynamicLayerName(url);
                    break;
                case "040002":  //ArcGIS WFS
                    layerName = MapServiceUtil.getArcGISWFSLayerName(url);
                    break;
                case "040003":  //GeoServer WFS
                    layerName = MapServiceUtil.getGeoServerLayerName(url);
                    break;
                case "030003":  //GeoServer WMS
                    layerName = MapServiceUtil.getGeoServerLayerName(url);
                    break;
            }
            resultJSON.put("name",layerName);
        }catch (Exception e) {
            resultJSON.put("success", false);
            resultJSON.put("name", "");
        }
        return resultJSON.toString();
    }




}
