package com.augurit.agsupport.map.division.controller;

import com.alibaba.fastjson.JSON;
import com.augurit.agcloud.framework.ui.pager.EasyuiPageInfo;
import com.augurit.agcloud.framework.ui.pager.PageHelper;
import com.augurit.agcloud.framework.ui.result.ResultForm;
import com.augurit.agcloud.framework.util.JsonUtils;
import com.augurit.agsupport.map.mapServiceInfo.arcgis.ArcgisRestParam;
import com.augurit.agsupport.map.mapServiceInfo.arcgis.RestService;
import com.augurit.agsupport.map.division.domain.AgSgDivision;
import com.augurit.agsupport.map.division.domain.AgSgDivisionUrl;
import com.augurit.agsupport.map.division.domain.Division;
import com.augurit.agsupport.map.division.service.IAgSgDivision;
import com.augurit.agsupport.map.division.service.IAgSgDivisionUrl;
import com.augurit.agsupport.map.util.CommonUtil;
import com.augurit.util.ConfigUtil;
import com.augurit.agsupport.map.util.MapServiceUtil;
import com.common.util.Common;
import com.common.util.HttpRequester;
import com.common.util.HttpRespons;
import com.common.util.ReflectBeans;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/agsupport/division")
public class AgSgDivisionController {
    private static Logger logger = LoggerFactory.getLogger(AgSgDivisionController.class);

    @Autowired
    private IAgSgDivision iAgSgDivision;
    @Autowired
    private IAgSgDivisionUrl iAgSgDivisionUrl;

    @RequestMapping("/index.html")
    public ModelAndView index(HttpServletRequest request, Model model) throws Exception {
        return new ModelAndView("agsupport/map/division/division");
    }

    @RequestMapping("/urlIndex.html")
    public ModelAndView urlIndex(HttpServletRequest request, Model model) throws Exception {
        return new ModelAndView("aeaMap/division/importDivisionIndex");
    }

    @RequestMapping("/findList")
    public EasyuiPageInfo findList(AgSgDivision agSgDivision, Page page) throws Exception {
        PageInfo<AgSgDivision> pageInfo= iAgSgDivision.findList(agSgDivision,page);
        return PageHelper.toEasyuiPageInfo(pageInfo);
    }

    /**
     * 获取行政区划所有数据
     *
     * @return
     */
    @RequestMapping("/getAllList")
    public String getAllList() {
        ResultForm result = new ResultForm(true);
        try {
            List<AgSgDivision> list = iAgSgDivision.findAllList();
            result.setMessage(JSON.toJSONString(list));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return JsonUtils.toJson(result);
    }

    /**
     * 获取行政区划Url所有数据
     *
     * @return
     */
    @RequestMapping("/getAllUrlList")
    public String getAllUrlList() {
        ResultForm result = new ResultForm(true);
        try {
            List<AgSgDivisionUrl> list = iAgSgDivisionUrl.findAllList();
            result.setMessage(JSON.toJSONString(list));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return JsonUtils.toJson(result);
    }

    /**
     * 保存或修改
     *
     * @param listStr
     * @param request
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    public String saveOrUpdate(String listStr, HttpServletRequest request) {
        ResultForm resultForm = new ResultForm(true);
        if (StringUtils.isEmpty(listStr)) {
            resultForm.setSuccess(false);
            resultForm.setMessage("保存失败");
            JsonUtils.toJson(resultForm);
        }
        try {
            JSONArray list = JSONArray.fromObject(listStr);
            for (int i = 0; i < list.size(); i++) {
                JSONObject obj = list.getJSONObject(i);
                AgSgDivision agSgDivision = (AgSgDivision) JSONObject.toBean(obj, AgSgDivision.class);
                String code = agSgDivision.getCode();
                String id = agSgDivision.getId() == null ? null : agSgDivision.getId().toString();
                List<AgSgDivision> existList = iAgSgDivision.findByCode(code, id);
                if (existList.size() > 0) {
                    resultForm.setMessage("保存失败，存在相同的编号！");
                    resultForm.setSuccess(false);
                } else {
                    if (agSgDivision.getId() == null) { //新增保存
                        agSgDivision.setId(UUID.randomUUID().toString());
                        iAgSgDivision.save(agSgDivision);
                    } else {//修改
                        iAgSgDivision.update(agSgDivision);
                    }
                }
            }
        } catch (Exception e) {
            resultForm.setMessage("保存失败！");
            resultForm.setSuccess(false);
            e.printStackTrace();
        }
        return JsonUtils.toJson(resultForm);
    }

    /**
     * 删除
     *
     * @param paramIds
     * @return
     */
    @RequestMapping("/delete")
    public String delete(String paramIds, HttpServletRequest request) {
        request.getSession().getMaxInactiveInterval();
        ResultForm resultForm = new ResultForm(true);
        String ids[] = null;
        if (StringUtils.isNotEmpty(paramIds)) {
            ids = paramIds.split(",");
        }
        try {
            if (ids != null && ids.length > 0) {
                iAgSgDivision.deleteBatch(ids);
            }
            resultForm.setMessage("删除成功！");
        } catch (Exception e) {
            resultForm.setMessage("删除失败！");
            resultForm.setSuccess(false);
            e.printStackTrace();
        }
        return JsonUtils.toJson(resultForm);
    }

    /**
     * 获取图层字段及图层名称信息
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/getLayerInfo")
    public String getLayerTable(String url, String layerType) throws Exception {
        ResultForm resultForm = new ResultForm(true);
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(url)) {
            com.alibaba.fastjson.JSONArray fields = new com.alibaba.fastjson.JSONArray();
            String layerName = "";
            switch (layerType) {
                case "040002": //ArcGIS WFS
                    fields = MapServiceUtil.getArcGISWFSLayerFields(url);
                    layerName = url.split("\\?")[1].split("=")[1];
                    break;
                case "020202": //ArcGIS MapServer(Dynamic)
                    fields = MapServiceUtil.getArcGISDynamicLayerFields(url);
                    layerName = MapServiceUtil.getArcGISDynamicLayerName(url);
                    break;
                case "040003": //GeoServer WFS
                    fields = MapServiceUtil.getGeoServerWFSLayerFields(url);
                    layerName = url.split("\\?")[1].split("=")[1];
                    break;
                case "030003": //GeoServer WMS
                    fields = MapServiceUtil.getGeoServerWFSLayerFields(url);
                    layerName = MapServiceUtil.getGeoServerLayerName(url);
                    break;
            }
            map.put("layerName", layerName);
            map.put("fields",fields);
            resultForm.setMessage(JSONObject.fromObject(map).toString());
        }
        return JsonUtils.toJson(resultForm);
    }

    /**
     * 从图层上导入数据
     *
     * @return
     */
    @RequestMapping("/importLayers")
    public String importData(HttpServletRequest request) throws Exception {
        String layerType = request.getParameter("layerType");
        ResultForm resultForm = new ResultForm(true);
        Division all = null;
        //区县
        Division sd = null;
        String surl = Common.checkNull(request.getParameter("cityUrl"), "");//图层路径
        if (StringUtils.isNotEmpty(surl)) {
            String sname = Common.checkNull(request.getParameter("cityNameField"), "NAME");//区县名称
            String scode = Common.checkNull(request.getParameter("cityCodeField"), "CODE");//区县编码
            String sLayerName = Common.checkNull(request.getParameter("cityLayerName"), null);//图层名称

            sd = new Division();
            sd.setLayerName(sLayerName);
            sd.setUrl(surl);
            sd.setNameField(sname);
            sd.setCodeField(scode);
        }

        //街镇
        Division jd = null;
        String jurl = Common.checkNull(request.getParameter("streetUrl"), "");
        if (StringUtils.isNotEmpty(jurl)) {
            String jname = Common.checkNull(request.getParameter("streetNameField"), "NAME");
            String jcode = Common.checkNull(request.getParameter("streetCodeField"), "CODE");
            String jLayerName = Common.checkNull(request.getParameter("streetLayerName"), null);
            jd = new Division();
            jd.setLayerName(jLayerName);
            jd.setUrl(jurl);
            jd.setNameField(jname);
            jd.setCodeField(jcode);
        }


        //村，居委
        Division cd = null;
        String curl = Common.checkNull(request.getParameter("countryUrl"), "");
        if (StringUtils.isNotEmpty(curl)) {
            String cname = Common.checkNull(request.getParameter("countryNameField"), "NAME");
            String ccode = Common.checkNull(request.getParameter("countryCodeField"), "CODE");
            String cLayerName = Common.checkNull(request.getParameter("countryLayerName"), null);
            cd = new Division();
            cd.setLayerName(cLayerName);
            cd.setUrl(curl);
            cd.setNameField(cname);
            cd.setCodeField(ccode);
        }
        if (sd != null && jd != null && cd != null) {
            sd.setChild(jd);
            jd.setChild(cd);
            all = sd;
        } else if (sd != null && jd != null && cd == null) {
            sd.setChild(jd);
            all = sd;
        } else if (sd == null && jd != null && cd != null) {
            jd.setChild(cd);
            all = jd;
        } else if (sd != null && jd == null && cd != null) {
            sd.setChild(cd);
            all = sd;
        } else if (sd == null && jd == null && cd != null) {
            all = cd;
        } else if (sd == null && jd != null && cd == null) {
            all = jd;
        } else if (sd != null && jd == null && cd == null) {
            all = sd;
        }

        //清空表格数据
        iAgSgDivision.truncateTable();
        iAgSgDivisionUrl.truncateTable();

        Boolean success = false;
        switch (layerType) {
            case "020202":  //ArcGIS MapServer(Dynamic)
                success = iAgSgDivision.importArcGISRestLayer(all, null,1,layerType);
                break;
            case "020302":  //ArcGIS MapServer(Tile)
                success = iAgSgDivision.importArcGISRestLayer(all, null,1,layerType);
                break;
            case "040003":  //GeoServer WFS
                success = iAgSgDivision.importGeoserverLayer(all, null,1,layerType);
                break;
            case "030003":  //GeoServer WMS
                success = iAgSgDivision.importGeoserverLayer(all, null,1,layerType);
                break;
        }
        if(success) {
            resultForm.setMessage("导入成功!");
        }else {
            resultForm.setSuccess(false);
            resultForm.setMessage("导入失败!");
            throw new Exception("导入失败!");
        }
        return JsonUtils.toJson(resultForm);
    }

    /**
     * 获取行政区划Url所有数据
     *
     * @return
     */
    @RequestMapping("/getDivitionByPcode")
    public String getDivitionByPcode(HttpServletRequest request) {
        ResultForm result = new ResultForm(true);
        String desc = "%" + request.getParameter("desc") + "%";
        String pCode = "".equals(request.getParameter("pCode")) ? null : request.getParameter("pCode");
        try {
            List<AgSgDivision> list = new ArrayList<AgSgDivision>();
            list = iAgSgDivision.findByPCode(pCode, desc);
            JSONArray array = JSONArray.fromObject(list);
            result.setMessage(array.toString());
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return JsonUtils.toJson(result);
    }

    /**
     * 通过关键字（code或name）获取行政区划空间信息
     *
     * @return
     */
    @RequestMapping("/getGeoByKeyword")
    public String getGeoByKeyword(String keyword) {
        ResultForm result = new ResultForm(true);
        try {
            AgSgDivision agSgDivision = iAgSgDivision.findByKeyword(keyword);
            if (agSgDivision != null) {
                ArcgisRestParam param = new ArcgisRestParam();
                param.setF("json");
                param.setOutFields("*");
                param.setGeometryType(ArcgisRestParam.GEOMETRYTYPE_esriGeometryPolygon);
                param.setSpatialRel(ArcgisRestParam.SPATIALREL_esriSpatialRelContains);
                param.setWhere("NAME like \'"+keyword+"%\' or CODE = \'"+keyword+"\'");
                HttpRequester request = new HttpRequester();
                String url= ConfigUtil.getLanUrl(agSgDivision.getUrl(),null);
                HttpRespons respons = request.sendPost(url+"/query", ReflectBeans.beanToMap(param));
                String json = respons.getContent();
                JsonObject jsonobject = new JsonParser().parse(json)
                        .getAsJsonObject();
                if (!jsonobject.has("features")) {
                    result.setMessage("无相关数据！");
                    result.setSuccess(false);
                    return JsonUtils.toJson(result);
                }
                JsonArray array = jsonobject.getAsJsonArray("features");
                result.setMessage(String.valueOf(array));
            }else{
                result.setMessage("无相关数据！");
                result.setSuccess(false);
                return JsonUtils.toJson(result);
            }
        }catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return JsonUtils.toJson(result);
    }
}

