package com.augurit.agsupport.map.util;

import com.augurit.util.ConfigUtil;
import com.common.util.HttpRequester;
import com.common.util.HttpRespons;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * arcgis 相关工具类
 */
public class ArcgisServiceUtil {
    /**
     * 根据服务地址获取该服务的字段信息
     *
     * @param url
     * @return
     */
    public static JSONObject getFieldsByUrl(String url) {//http://192.168.31.84:6080/arcgis/rest/services/gz/%E4%B8%89%E7%B1%BB%E7%A9%BA%E9%97%B4/MapServer/0
        JSONObject resultJSON = new JSONObject();
        resultJSON.put("success",true);
        try {
            if(StringUtils.isEmpty(url)){//传入的服务地址为空
                resultJSON.put("success",false);
                resultJSON.put("message","传入的服务地址为空！");
                return resultJSON;
            }
            Map<String, String> param = new HashMap<>();
            param.put("f", "json");
            url= ConfigUtil.getLanUrl(url,null);
            HttpRespons httpRespons = new HttpRequester().sendPost(url, param);
            JSONObject obj = JSONObject.fromObject(httpRespons.getContent());
            JSONArray fields = new JSONArray();
            if(obj.has("fields")){
                fields = obj.getJSONArray("fields");
            }
            resultJSON.put("fields",fields);
        }catch (Exception e){
            resultJSON.put("success",false);
            resultJSON.put("message",e.getMessage());
        }
        return resultJSON;
    }
}
