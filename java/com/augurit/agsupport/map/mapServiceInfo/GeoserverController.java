package com.augurit.agsupport.map.mapServiceInfo;

import com.augurit.agsupport.map.util.GeoserverUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agsupport/geoserver")
public class GeoserverController {
    @RequestMapping("/getFields")
    public String getFieldsByUrl(String url) {
        JSONObject resultJSON = new JSONObject();
        resultJSON.put("success", true);
        try {
            JSONArray fields = GeoserverUtil.getJSONArrayFieldsByUrl(url);
            resultJSON.put("fields", fields);
        } catch (Exception e) {
            resultJSON.put("success", false);
            resultJSON.put("message", "获取字段列表出错！");
            e.printStackTrace();
        }
        return resultJSON.toString();
    }
}
