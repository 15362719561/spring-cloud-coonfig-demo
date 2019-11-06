package com.augurit.agsupport.map.division.service.impl;

import com.augurit.agcloud.framework.ui.pager.PageHelper;
import com.augurit.agsupport.map.mapServiceInfo.arcgis.ArcgisRestParam;
import com.augurit.agsupport.map.mapServiceInfo.arcgis.RestService;
import com.augurit.agsupport.map.division.domain.AgSgDivision;
import com.augurit.agsupport.map.division.domain.AgSgDivisionUrl;
import com.augurit.agsupport.map.division.domain.Division;
import com.augurit.agsupport.map.division.mapper.AgSgDivisionMapper;
import com.augurit.agsupport.map.division.mapper.AgSgDivisionUrlMapper;
import com.augurit.agsupport.map.division.service.IAgSgDivision;
import com.augurit.util.ConfigUtil;
import com.common.util.Common;
import com.common.util.HttpRequester;
import com.common.util.HttpRespons;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AgSgDivisionImpl implements IAgSgDivision {
    @Autowired
    private AgSgDivisionMapper agSgDivisionMapper;
    @Autowired
    private AgSgDivisionUrlMapper agSgDivisionUrlMapper;

    Integer sort = 1;

    @Override
    public PageInfo<AgSgDivision> findList(AgSgDivision agSgDivision, Page page) throws Exception{
        PageHelper.startPage(page);
        List<AgSgDivision> list = agSgDivisionMapper.findList(agSgDivision);
        return new PageInfo<AgSgDivision>(list);
    }

    @Override
    public List<AgSgDivision> findAllList() throws Exception{
        return agSgDivisionMapper.findAllList();
    }
    @Override
    public void save(AgSgDivision agSgDivision) throws Exception{
        agSgDivisionMapper.save(agSgDivision);
    }
    @Override
    public void update(AgSgDivision agSgDivision) throws Exception{
        agSgDivisionMapper.update(agSgDivision);
    }
    @Override
    public void deleteById(String id) throws Exception{
        agSgDivisionMapper.deleteById(id);
    }
    @Override
    public AgSgDivision findById(String id) throws Exception{
        return agSgDivisionMapper.findById(id);
    }
    @Override
    public void deleteBatch(String[] ids) throws Exception {
        agSgDivisionMapper.deleteBatch(ids);
    }
    @Override
    public List<AgSgDivision> findByCode(String code,String id) throws Exception{
        return agSgDivisionMapper.findByCode(code,id);
    }
    @Override
    public List<AgSgDivision> findByPCode(String pCode,String description) throws Exception{
        return agSgDivisionMapper.findByPCode(pCode,description);
    }

    @Override
    public void truncateTable() throws Exception{
        agSgDivisionMapper.truncateTable();
    }
    @Override
    public AgSgDivision findByKeyword(String keyword) throws Exception{
        return agSgDivisionMapper.findByKeyword(keyword);
    }

    /**
     * 导入ArcGIS REST 服务图层
     * @param division   本图层属性
     * @param pCodeValue 父节点code值
     * @param start
     * @param layerType
     * @return
     * @throws Exception
     */
    public boolean importArcGISRestLayer(Division division, String pCodeValue,Integer start,String layerType) throws Exception {
        sort = start;
        try {
            // 先统计有多少数量
            String countJson = RestService.callRestCountOnly(division.getUrl() + "/query", null);
            JsonParser jsonParser = new JsonParser();
            JsonElement je = jsonParser.parse(countJson);
            if (je != null && je.isJsonObject()) {
                JsonObject o = je.getAsJsonObject();
                int count = o.get("count").getAsInt();
                String where = Common.isCheckNull(pCodeValue) ? "1=1"
                        : division.getCodeField() + " like '" + pCodeValue + "%'";

                int step = 1000;// 步频
                // 当大于1000条记录,获取所有id值，再分批查询
                if (count > 1000) {
                    ArcgisRestParam restParam = new ArcgisRestParam("json", where, "");
                    // 获取所有id值
                    String idsJson = RestService.callRestReturnIdsOnly(division.getUrl() + "/query", restParam);
                    JSONObject obj = JSONObject.fromObject(idsJson);
                    String objectIdFieldName = obj.getString("objectIdFieldName");
                    JSONArray idsArray = obj.getJSONArray("objectIds");
                    int arraySize = idsArray.size() / step;
                    String[] idsStr = new String[arraySize + 1];
                    // 分批次
                    for (int i = 0; i*step <= count; i++) {
                        if ((i+1)*step < count) {
                            idsStr[i] = JSONArray.fromObject(idsArray.subList(i * step, (i + 1) * step)).join(",");
                        } else {
                            idsStr[i] = JSONArray.fromObject(idsArray.subList(i * step, idsArray.size() - 1)).join(",");
                        }
                    }
                    // 分批次查询
                    for (int i = 0; i < idsStr.length; i++) {
                        String _where = where + " and " + objectIdFieldName + " in(" + idsStr[i] + ")";
                        String json = RestService.callRestQuery(division.getUrl() + "/query", _where, "",
                                division.getNameField() + "," + division.getCodeField(), "false");
                        // 保存
                        saveDivisionAndUrlWithArcGISRestLayer(json, division, pCodeValue,layerType);
                    }
                } else {
                    // json = RestService.callRestQuery(url, where);
                    String json = RestService.callRestQuery(division.getUrl() + "/query", where, "",
                            division.getNameField() + "," + division.getCodeField(), "false");
                    // 保存
                    saveDivisionAndUrlWithArcGISRestLayer(json, division, pCodeValue,layerType);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     *
     * @param json
     * @param division   图层
     * @param pCodeValue 父节点code值
     * @return
     * @throws Exception
     */
    private void saveDivisionAndUrlWithArcGISRestLayer(String json, Division division, String pCodeValue,String layerType) throws Exception {
        JsonParser jsonParser = new JsonParser();
        AgSgDivisionUrl urlForm = agSgDivisionUrlMapper.getDivisionUrlByUrl(division.getUrl());
        if (urlForm == null) {
            urlForm = new AgSgDivisionUrl();
            // 如果没提供行政区划级别名称，就到图层里取
            if (Common.isCheckNull(division.getLayerName())) {
                // urlJson:{"currentVersion":10.2,"id":3,"name":"行政区域_镇街",...}
                String urlJson = RestService.callRestQuery(division.getUrl());
                division.setLayerName(jsonParser.parse(urlJson).getAsJsonObject().get("name").getAsString());
            }
            String aa = division.getLayerName();
            urlForm.setDescription(aa);
            urlForm.setKeyword(division.getCodeField());// "NAME"
            urlForm.setUrl(division.getUrl());
            urlForm.setId(UUID.randomUUID().toString());
            urlForm.setLayerType(layerType);
            agSgDivisionUrlMapper.save(urlForm);
            //回填ID
            urlForm = agSgDivisionUrlMapper.getDivisionUrlByUrl(division.getUrl());
            // 保存各级行政区划对应的访问地址
//            this.saveUrls(urlForm);
        }
        JsonElement je = jsonParser.parse(json);
        JsonElement features = je.getAsJsonObject().get("features");
        if (features != null && features.isJsonArray()) {
            JsonArray jarr = features.getAsJsonArray();// 转成json格式数据
            for (int i = 0; i < jarr.size(); i++) {
                String itemName = jarr.get(i).getAsJsonObject().get("attributes").getAsJsonObject()
                        .get(division.getNameField()).getAsString();
                String itemCode = jarr.get(i).getAsJsonObject().get("attributes").getAsJsonObject()
                        .get(division.getCodeField()).getAsString();
                AgSgDivision form = new AgSgDivision();
                form.setCode(itemCode);
                form.setName(itemName);
                form.setPcode(pCodeValue);
                form.setRefUrl(urlForm.getId().toString());// 外键
                form.setDescription(division.getLayerName());
                String sortId = String.valueOf(sort);
                form.setSortId(sortId);
                form.setId(UUID.randomUUID().toString());
                this.save(form);
                sort++;
                if (division.getChild() != null) {
                    importArcGISRestLayer(division.getChild(), form.getCode(),sort,layerType);
                }
            }
        }
    }

    /**
     * 导入
     *
     * @param division   本图层属性
     * @param pCodeValue 父节点code值
     * @return
     * @throws Exception
     */
    public boolean importGeoserverLayer(Division division, String pCodeValue, Integer start,String layerType) throws Exception {
        sort = start;
        String url = division.getUrl().split("\\?")[0];
        String layerName = division.getUrl().split("\\?")[1].split("=")[1];
        if(!url.endsWith("ows")){
            String type = url.split("/")[url.split("/").length-1];
            url = url.substring(0, url.length()-type.length())+"ows";
        }
        try {
            String pCodeValueStr = Common.isCheckNull(pCodeValue) ? "*":pCodeValue+"*";
            int step = 1000;// 步频
            // 先取出步频以内的数据，并保存
            Map param = getGeoServerWFSGetFeatureParams(division,pCodeValueStr,layerName,0,step);
            url= ConfigUtil.getLanUrl(url,null);
            HttpRespons httpRespons = new HttpRequester().sendGet(url,param);
            JSONObject featuresJsonObject = JSONObject.fromObject(httpRespons.getContent());
            Integer count = featuresJsonObject.getInt("totalFeatures");
            String features = featuresJsonObject.getString("features");
            // 保存
            saveDivisionAndUrWithGeoServerLayer(features, division, pCodeValue,layerType);
            // 根据totalFeatures判断要素总数是否大于步频，当大于步频时分批获取后面的数据
            if (count > step) {
                double dnum = count/step;
                System.out.print(Math.ceil(dnum));
                int num = (int) Math.ceil(dnum);
                // 分批次查询
                for (int i = 2; i < num; i++) {
                    String features1 = featuresJsonObject.getString("features");
                    saveDivisionAndUrWithGeoServerLayer(features1, division, pCodeValue,layerType);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 获取GeoServer WFS GetFeture接口请求参数
     * @param division
     * @param pCodeValue
     * @param layerName
     * @param startNum
     * @param endNum
     * @return
     */
    private Map getGeoServerWFSGetFeatureParams(Division division, String pCodeValue,String layerName, Integer startNum,Integer endNum){
        Map param = new HashMap<>();
        param.put("request", "GetFeature");
        param.put("service", "wfs");
        param.put("version", "1.0.0");
        param.put("typeName", layerName);
        param.put("outputFormat","application/json");
        param.put("maxFeatures","1000");
        param.put("propertyName",division.getNameField()+","+division.getCodeField());
        String filter ="<Filter>"+
                "<And>"+
                "<PropertyIsLike wildCard=\"*\" singleChar=\".\" escapeChar=\"!\">"+
                "<PropertyName>"+division.getCodeField()+"</PropertyName>"+
                "<Literal>"+pCodeValue+"</Literal>"+
                "</PropertyIsLike>"+
                "<PropertyIsBetween>"+
                "<PropertyName>objectid</PropertyName>"+
                "<LowerBoundary><Literal>"+startNum+"</Literal></LowerBoundary>"+
                "<UpperBoundary><Literal>"+endNum+"</Literal></UpperBoundary>"+
                "</PropertyIsBetween>"+
                "</And>"+
                "</Filter>";
        param.put("FILTER",filter);
        return param;
    }
    /**
     *
     * @param json
     * @param division   图层
     * @param pCodeValue 父节点code值
     * @return
     * @throws Exception
     */
    private void saveDivisionAndUrWithGeoServerLayer(String json, Division division, String pCodeValue,String layerType) throws Exception {
        JsonParser jsonParser = new JsonParser();
        AgSgDivisionUrl urlForm = agSgDivisionUrlMapper.getDivisionUrlByUrl(division.getUrl());
        if (urlForm == null) {
            urlForm = new AgSgDivisionUrl();
            String aa = division.getLayerName();
            urlForm.setDescription(aa);
            urlForm.setKeyword(division.getCodeField());// "NAME"
            urlForm.setId(UUID.randomUUID().toString());
            urlForm.setUrl(division.getUrl());
            urlForm.setLayerType(layerType);
            agSgDivisionUrlMapper.save(urlForm);
            //回填ID
            urlForm = agSgDivisionUrlMapper.getDivisionUrlByUrl(division.getUrl());
            // 保存各级行政区划对应的访问地址
//            this.saveUrls(urlForm);
        }
        JsonElement features = jsonParser.parse(json);
        if (features != null && features.isJsonArray()) {
            JsonArray jarr = features.getAsJsonArray();// 转成json格式数据
            for (int i = 0; i < jarr.size(); i++) {
                String itemName = jarr.get(i).getAsJsonObject().get("properties").getAsJsonObject()
                        .get(division.getNameField()).getAsString();
                String itemCode = jarr.get(i).getAsJsonObject().get("properties").getAsJsonObject()
                        .get(division.getCodeField()).getAsString();
                AgSgDivision form = new AgSgDivision();
                form.setId(UUID.randomUUID().toString());
                form.setCode(itemCode);
                form.setName(itemName);
                form.setPcode(pCodeValue);
                form.setRefUrl(urlForm.getId().toString());// 外键
                form.setDescription(division.getLayerName());
                String sortId = String.valueOf(sort);
                form.setSortId(sortId);
                this.save(form);
                sort++;
                if (division.getChild() != null) {
                    importGeoserverLayer(division.getChild(), form.getCode(),sort,layerType);
                }
            }
        }
    }
}
