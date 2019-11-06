package com.augurit.agsupport.map.complianceCheck.controller;

import com.augurit.agcloud.framework.ui.result.ResultForm;
import com.augurit.agcloud.framework.util.JsonUtils;
import com.augurit.agcom.common.LoginHelpClient;
import com.augurit.agsupport.map.complianceCheck.domain.AeaMapComplianceCheck;
import com.augurit.agsupport.map.complianceCheck.domain.AeaMapComplianceProject;
import com.augurit.agsupport.map.complianceCheck.service.IAeaMapComplianceCheck;
import com.augurit.agsupport.map.complianceCheck.service.IAeaMapComplianceProject;
import com.augurit.agsupport.map.landTypeStandards.service.IAgSgLandType;
import com.augurit.agsupport.map.util.CommonUtil;
import com.augurit.util.ConfigUtil;
import com.augurit.agsupport.map.util.DocumentHandler;
import com.common.util.Common;
import com.common.util.HttpRequester;
import com.common.util.HttpRespons;
import eu.bitwalker.useragentutils.UserAgent;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 合规性审查功能
 */
@RestController
@RequestMapping("/agsupport/complianceCheck")
public class ComplianceCheckController {

    @Value("${hurd.url}")
    private String hurdUrl;

    private static Logger logger = LoggerFactory.getLogger(ComplianceCheckController.class);

    //运维配置的图层数据标识
    private static String layerIdentify_kg = "LayerName_KG"; //控制性详细规划
    private static String layerIdentify_tg = "LayerName_TG"; //土地利用总体规划
    private static String layerIdentify_czkj = "LayerName_CZKJ"; //基本农田保护红线
    private static String layerIdentify_nykj = "LayerName_NYKJ"; //农业空间
    private static String layerIdentify_stkj = "LayerName_STKJ"; //生态空间
    private static String layerIdentify_czbj = "LayerNAME_CZKFBJ";//城市增长边界
    private static String layerIdentify_jbnt = "LayerName_JBNT"; //基本农田
    private static String layerIdentify_sths = "LayerName_STHX"; //生态保护红线

    @Autowired
    private IAeaMapComplianceCheck iAeaMapComplianceCheck;

    @Autowired
    private IAeaMapComplianceProject iAeaMapComplianceProject;

    @Autowired
    private IAgSgLandType iAgSgLandType;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    @RequestMapping("/index.html")
    public ModelAndView index(HttpServletRequest request, Model model) throws Exception {
        return new ModelAndView("agsupport/map/complianceCheck/complianceCheck");
    }

    @RequestMapping("/list")
    public String list(AeaMapComplianceCheck aeaMapComplianceCheck) throws Exception {
        ResultForm result = new ResultForm(true);
        try {
            List<AeaMapComplianceCheck> conflictCheckLayers = iAeaMapComplianceCheck.getAll(aeaMapComplianceCheck);
            JSONArray array = JSONArray.fromObject(conflictCheckLayers);
            result.setMessage(array.toString());

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return JsonUtils.toJson(result);
    }

    /**
     * 保存或修改合规性检查图层信息
     *
     * @param aeaMapComplianceCheck
     * @param request
     * @return
     */
    @RequestMapping("/save")
    public String saveParam(AeaMapComplianceCheck aeaMapComplianceCheck, HttpServletRequest request) {
        JSONObject jsonObject = null;
        try {
            if (StringUtils.isEmpty(aeaMapComplianceCheck.getId())) {
                aeaMapComplianceCheck.setId(UUID.randomUUID().toString());
                iAeaMapComplianceCheck.saveAeaMapComplianceCheck(aeaMapComplianceCheck);
                jsonObject = this.getLogInfo(request, "保存合规性检查图层信" +
                        "息--" + aeaMapComplianceCheck.getLayerName());
            } else {
                iAeaMapComplianceCheck.updatAeaMapComplianceCheck(aeaMapComplianceCheck);
                jsonObject = this.getLogInfo(request, "修改合规性检查图层信息--" + aeaMapComplianceCheck.getLayerName());
            }
            jsonObject.put("operResult", "成功");
            logger.info(jsonObject.toString());
            return JsonUtils.toJson(new ResultForm(true));
        } catch (Exception e) {
            jsonObject.put("operResult", "失败");
            if (e.getMessage() != null) {
                jsonObject.put("exceptionMessage", e.getMessage().substring(0, e.getMessage().length() > 500 ? 500 : e.getMessage().length()));
            }
            logger.error(jsonObject.toString());
            e.printStackTrace();
        }
        return JsonUtils.toJson(new ResultForm(false, "保存失败！"));
    }

    /**
     * 保存或修改合规性检查图层信息
     *
     * @param listStr
     * @param request
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    public String saveOrUpdate(String listStr, HttpServletRequest request) {
        ResultForm resultForm = new ResultForm(true);
        try {
            if (StringUtils.isNotEmpty(listStr)) {
                JSONArray list = JSONArray.fromObject(listStr);
                for (int i = 0; i < list.size(); i++) {
                    JSONObject obj = list.getJSONObject(i);
                    AeaMapComplianceCheck aeaMapComplianceCheck = (AeaMapComplianceCheck) JSONObject.toBean(obj, AeaMapComplianceCheck.class);
                    if (StringUtils.isEmpty(aeaMapComplianceCheck.getId())) { //新增保存
                        aeaMapComplianceCheck.setId(UUID.randomUUID().toString());
                        iAeaMapComplianceCheck.saveAeaMapComplianceCheck(aeaMapComplianceCheck);
                    } else {//修改
                        iAeaMapComplianceCheck.updatAeaMapComplianceCheck(aeaMapComplianceCheck);
                    }
                }
            }
        } catch (Exception e) {
            resultForm.setSuccess(false);
            resultForm.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return JsonUtils.toJson(resultForm);
    }

    /**
     * 删除合规性检查图层配置
     *
     * @param paramIds
     * @return
     */
    @RequestMapping("/delete")
    public String delete(String paramIds, HttpServletRequest request) {
        JSONObject jsonObject = this.getLogInfo(request, "批量删除合规性检查图层配置");
        String ids[] = null;
        if (StringUtils.isNotEmpty(paramIds)) {
            ids = paramIds.split(",");
        }
        try {
            if (ids != null && ids.length > 0) {
                iAeaMapComplianceCheck.deleteBatch(ids);
            }
            jsonObject.put("operResult", "成功");
            logger.info(jsonObject.toString());
            return JsonUtils.toJson(new ResultForm(true));
        } catch (Exception e) {
            jsonObject.put("operResult", "失败");
            if (e.getMessage() != null) {
                jsonObject.put("exceptionMessage", e.getMessage().substring(0, e.getMessage().length() > 500 ? 500 : e.getMessage().length()));
            }
            logger.error(jsonObject.toString());
            e.printStackTrace();
        }
        return JsonUtils.toJson(new ResultForm(false));
    }

    /**
     * 获取日志基本信息 2017-12-07
     *
     * @param request
     * @param funcName
     * @return
     */
    private JSONObject getLogInfo(HttpServletRequest request, String funcName) {
        JSONObject jsonObject = new JSONObject();
        String loginName = LoginHelpClient.getLoginName(request);
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        jsonObject.put("loginName", loginName);
        jsonObject.put("sysName", "agsupport");
        jsonObject.put("ipAddress", Common.getIpAddr(request));
        jsonObject.put("browser", userAgent.getBrowser().getName());
        jsonObject.put("funcName", funcName);
        return jsonObject;
    }

    /**
     * 生成报告
     *
     * @param dataString
     * @param request
     * @return
     */
    @RequestMapping("/grantReport")
    public String grantReport(String dataString, HttpServletRequest request) {
        ResultForm result = new ResultForm(true);
        try {
            String basePath = request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort() + request.getContextPath()
                    + "/";
            String webPath = request.getSession().getServletContext().getRealPath("/");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            String filePath = CommonUtil.DEFAULT_ATTACHMENT_FILEPATH
                    .concat(File.separator).concat("合规性检查").concat(File.separator)
                    .concat(String.valueOf(c.get(Calendar.YEAR)))
                    .concat("-")
                    .concat(String.valueOf(c.get(Calendar.MONTH) + 1))
                    .concat("-")
                    .concat(String.valueOf(c.get(Calendar.DATE)))
                    .concat(File.separator).concat(sdf.format(c.getTime()))
                    .concat("合规性检查.doc");
            Map<String, Object> dataMap = new HashMap<String, Object>();
            Map<String, Object> paramData = new HashMap<String, Object>();
            JSONObject projectData = null;
            JSONObject projectStatus = new JSONObject();//同步项目各项状态对象
            if (StringUtils.isNotEmpty(dataString)) {
                Map<String, Object> resultMessage = new HashMap<String, Object>();//报告结论信息等
                projectData = JSONObject.fromObject(dataString);
                paramData.put("areaUnit", "m²"); //面积单位
                paramData.put("projectName", projectData.containsKey("projectName") ? projectData.getString("projectName") : ""); //项目名称
                paramData.put("useLandSum", projectData.containsKey("useLandSum") ? projectData.getString("useLandSum") : "0"); //用地面积
                paramData.put("buildEnterprise", projectData.containsKey("buildEnterprise") ? projectData.getString("buildEnterprise") : ""); //建设单位
                String cgscjy = ""; //城市总体规划符合性审查建议
                String kgscjy = ""; // 控制性详细规划规划符合性审查建议
                String tgscjy = ""; //土地利用总体规划符合性审查建议
                Boolean hasCheckedCG = false;//城市总体是否有勾选检测项
                Boolean hasCheckedKG = false;//控制性详细规划是否有勾选检测项
                Boolean hasCheckedTG = false;//土地利用总体规划是否有勾选检测项
                paramData.put("result", "通过");// 综合结论
                JSONArray layerList = projectData.getJSONArray("layerList");//获取检测图层数据集合
                for (int i = 0; i < layerList.size(); i++) {
                    JSONObject layer = layerList.getJSONObject(i);

                    String layerName = layer.getString("layerName");
                    String layerIdentify = layer.getString("remark");
                    if (layerIdentify_tg.equals(layerIdentify)) { //土地利用总体规划
                        JSONObject tgLayerCheckData = grantTg(layer, projectData, basePath);
                        double nydArea = 0d;
                        double jsydArea = 0d;
                        double qtArea = 0d;
                        if (tgLayerCheckData.getBoolean("ck")) {
                            hasCheckedTG = true;
                            nydArea = tgLayerCheckData.containsKey("nydArea") ? tgLayerCheckData.getDouble("nydArea") : 0d;
                            jsydArea = tgLayerCheckData.containsKey("jsydArea") ? tgLayerCheckData.getDouble("jsydArea") : 0d;
                            qtArea = tgLayerCheckData.containsKey("qtArea") ? tgLayerCheckData.getDouble("qtArea") : 0d;
                            String tempStr = "";
                            if (nydArea > 0) {
                                //String tempStr = StringUtils.isEmpty(tgscjy) ? "不通过：" : "，";
                                tempStr += "占用基本农田" + String.format("%.2f", nydArea) + paramData.get("areaUnit").toString() + ",";
                                paramData.put("result", "不通过");
                            }
                            if (qtArea > 0) {
                                tempStr += "占用水域或其他非建设用地" + String.format("%.2f", nydArea) + paramData.get("areaUnit").toString() + ",";
                                paramData.put("result", "不通过");
                            }
                            tempStr = StringUtils.isEmpty(tempStr) ? "" : "不通过：" + tempStr.substring(0, tempStr.length() - 1) + "。";
                            tgscjy = tempStr;
                        }
                        paramData.put("zyjbntArea", nydArea > 0 ? String.format("%.2f", nydArea) : nydArea); //占用土规基本农田
                        paramData.put("ghjsydArea", jsydArea > 0 ? String.format("%.2f", jsydArea) : jsydArea); //规划建设用地地类情况
                        paramData.put("tgjsydImg", layer.has("imgSrc") ? layer.getString("imgSrc") : "");
                        paramData.put("tgjsyds", new ArrayList());
                        String tgydxz = (nydArea > 0 || qtArea > 0) ? "1" : "0";
                        projectStatus.put("tgydxz", tgydxz);//土地利用总体规划是否有非建设用地（0：建设用地，1：非建设用地）
                    } else if (layerIdentify_kg.equals(layerIdentify)) { //控制性详细规划
                        JSONObject kgLayerCheckData = grantKg(layer, projectData);
                        if (kgLayerCheckData.getBoolean("ck")) {
                            hasCheckedKG = true;
                            paramData.put("landType", kgLayerCheckData.getString("rangeCn"));
                            Boolean hasDiffLandType = kgLayerCheckData.containsKey("hasDiffLandType") ? kgLayerCheckData.getBoolean("hasDiffLandType") : false;
                            if (hasDiffLandType) {
                                kgscjy = "不通过：规划用地性质申报指标与实际指标不符！";
                                paramData.put("result", "不通过");
                            }
                        }
                        paramData.put("jsydImg", layer.has("imgSrc") ? layer.getString("imgSrc") : "");
                        List<JSONObject> dataList = (List<JSONObject>) kgLayerCheckData.getJSONArray("dataList");
                        paramData.put("jsyds", dataList);
                        projectStatus.put("cgydxz", kgLayerCheckData.has("cgydxz") ? kgLayerCheckData.getString("cgydxz") : "");//控制性详细规划是否有非建设用地（0：建设用地，1：非建设用地）
                    } else { //三区三线
                        if (layer.getBoolean("ck")) { //当前图层有做检测
                            hasCheckedCG = true;
                            if (layer.getInt("screenshot") == 1 && layer.has("imgSrc")) { //构建图片
                                if (layer.getInt("checkStatus") == 0) {
                                    if (!(layerIdentify_czkj.equals(layerIdentify) || layerIdentify_czbj.equals(layerIdentify))) {
                                        String tempStr = StringUtils.isEmpty(cgscjy) ? "不通过：" : ",";
                                        cgscjy += tempStr + layerName + "占用面积："
                                                + (layer.containsKey("area") ? layer.getString("area") : "0")
                                                + paramData.get("areaUnit").toString();
                                        paramData.put("result", "不通过");
                                    }
                                }
                                String area = layer.containsKey("area") ? layer.getString("area") : "0";
                                String imgSrc = layer.containsKey("imgSrc") ? layer.getString("imgSrc") : "";
                                if (layerIdentify_czkj.equals(layerIdentify)) {
                                    paramData.put("czkjImgName", layerName + ":");
                                    paramData.put("czkjImg", imgSrc);
                                    paramData.put("czkjArea", area);
                                    //paramData.put("czkjArea", paramData.get("useLandSum").toString()); //假数据（演示用）
                                } else if (layerIdentify_nykj.equals(layerIdentify)) {
                                    paramData.put("nykjImgName", layerName + ":");
                                    paramData.put("nykjImg", imgSrc);
                                    paramData.put("nykjArea", area);
                                } else if (layerIdentify_stkj.equals(layerIdentify)) {
                                    paramData.put("stkjImgName", layerName + ":");
                                    paramData.put("stkjImg", imgSrc);
                                    paramData.put("stkjArea", area);
                                } else if (layerIdentify_czbj.equals(layerIdentify)) {
                                    paramData.put("cszzImgName", layerName + ":");
                                    paramData.put("cszzImg", imgSrc);
                                    paramData.put("cszzArea", area);
                                    //paramData.put("cszzArea",paramData.get("useLandSum").toString());//假数据（演示用）

                                    String developBoundary = Double.valueOf(area) - Double.valueOf(paramData.get("useLandSum").toString()) > 0 ? "0" : "1";//开发边界（0：开发边界以内，1：开发边界以外）
                                    projectStatus.put("developBoundary", developBoundary);
                                } else if (layerIdentify_jbnt.equals(layerIdentify)) {
                                    paramData.put("jbntImgName", layerName + ":");
                                    paramData.put("jbntImg", imgSrc);
                                    paramData.put("jbntArea", area);

                                    String involvingBasicFarmland = Double.valueOf(area) > 0 ? "0" : "1";//涉及基本农田（0：涉及，1：不涉及）
                                    projectStatus.put("involvingBasicFarmland", involvingBasicFarmland);
                                } else if (layerIdentify_sths.equals(layerIdentify)) {
                                    paramData.put("stbhImgName", layerName + ":");
                                    paramData.put("stbhImg", imgSrc);
                                    paramData.put("stbhArea", area);

                                    String involveRedLine = Double.valueOf(area) > 0 ? "0" : "1";//是否涉及生态红线（0：涉及，1：不涉及）
                                    projectStatus.put("involveRedLine", involveRedLine);
                                }
                            }
                        }
                    }
                }
                paramData.put("cgscjy", hasCheckedCG ? (StringUtils.isEmpty(cgscjy) ? "通过" : cgscjy) : "未检测");// 城市总体规划符合性审查建议
                paramData.put("tgscjy", hasCheckedTG ? (StringUtils.isEmpty(tgscjy) ? "通过" : tgscjy) : "未检测");//土地利用总体规划符合性审查建议
                paramData.put("kgscjy", hasCheckedKG ? (StringUtils.isEmpty(kgscjy) ? "通过" : kgscjy) : "未检测");// 控制性详细规划规划符合性审查建议
                dataMap.put("data", paramData);

                if (projectData.has("taskInstDbid") && StringUtils.isNotEmpty(projectData.getString("taskInstDbid"))) {
                    String complianceCheckPass = "不通过".equals(paramData.get("result")) ? "0" : "1";//是否合规性检测通过（0：否，1：是）
                    String gyzdjqxmTyps = "N,T,D";
                    if (projectData.has("projectTypeCode")) {//是否公园、重大基础设施（0：否，1：是）
                        String sfgyzdjqxm = "0";
                        String projectTypeCode = projectData.getString("projectTypeCode");
                        for (String type : gyzdjqxmTyps.split(",")) {
                            if (projectTypeCode.indexOf(type) > -1) {
                                sfgyzdjqxm = "1";
                                break;
                            }
                        }
                        projectStatus.put("sfgyzdjqxm", sfgyzdjqxm);
                    }
                    projectStatus.put("userName", projectData.getString("userName"));
                    projectStatus.put("loginName", projectData.getString("loginName"));
                    projectStatus.put("taskInstDbid", projectData.getString("taskInstDbid"));
                    projectStatus.put("complianceCheckPass", complianceCheckPass);
                    syncProjectStatus(projectStatus);
                }

                File f = new File(webPath.concat(filePath));
                // 如果已经存在该文档，直接打开
                if (!f.exists()) {
                    // 生成文档
                    DocumentHandler handler = new DocumentHandler(webPath);
                    handler.createDoc(dataMap, "complianceCheck_V1.0.ftl", filePath);
                }
                String url = CommonUtil.DEFAULT_ATTACHMENT_FILEPATH
                        .concat(File.separator).concat("合规性检查").concat(File.separator)
                        .concat(String.valueOf(c.get(Calendar.YEAR)))
                        .concat("-")
                        .concat(String.valueOf(c.get(Calendar.MONTH) + 1))
                        .concat("-")
                        .concat(String.valueOf(c.get(Calendar.DATE)))
                        .concat(File.separator).concat(sdf.format(c.getTime()))
                        .concat("合规性检查.doc");
                resultMessage.put("reportUrl", basePath + url);
                result.setMessage(JSONObject.fromObject(resultMessage).toString());
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return JsonUtils.toJson(result);
    }

    /**
     * 控规数据处理
     *
     * @param kgType
     * @param projectData
     * @return
     */
    private JSONObject grantKg(JSONObject kgType, JSONObject projectData) {
        List list = new ArrayList();
        boolean hasDiffLandType = false;
        String cgydxz = "0";// //城规用地性质（0：建设用地，1：非建设用地）
        try {
            if (kgType.getBoolean("ck") == true) {
                double sumArea = 0;
                JSONObject configField = kgType.getJSONObject("configField");
                JSONArray dataList = kgType.getJSONArray("dataList");
                int twoCount = Math.round(dataList.size() / 2);
                for (int j = 0; j <= twoCount; j++) {
                    if (j * 2 == dataList.size()) {
                        continue;
                    }
                    Map twoMap = new HashMap();
                    JSONObject dataObj1 = dataList.getJSONObject(j * 2);
                    dataObj1.put("seq", CommonUtil.formatInteger(j * 2 + 1));
                    projectData.put("landTypeName", kgType.containsKey("rangeCn") ? kgType.getString("rangeCn") : "");
                    projectData.put("landTypeCode", kgType.containsKey(("range")) ? kgType.getString("range") : "");
                    dataObj1 = checkKg(dataObj1, projectData, configField);
                    double jrjzmj1 = StringUtils.isEmpty(dataObj1.getString("jrjzmj")) ? 0 : dataObj1.getDouble("jrjzmj"); //计容建筑面积
                    String ydxzCheck = StringUtils.isEmpty(dataObj1.getString("ydxzCheck")) ? "是" : dataObj1.getString("ydxzCheck");//用地性质与实际指标是否相符
                    if ("否".equals(ydxzCheck)) {
                        hasDiffLandType = true;
                    }

                    if ("0".equals(cgydxz)) {
                        cgydxz = getStatus(dataObj1, kgType.getString("typeField"));
                    }

                    sumArea += jrjzmj1;
                    twoMap.put("onejsyd", dataObj1);
                    twoMap.put("jsydcount", "one");
                    if (j * 2 + 1 < dataList.size()) {
                        JSONObject dataObj2 = dataList.getJSONObject(j * 2 + 1);
                        dataObj2.put("seq", CommonUtil.formatInteger(j * 2 + 1 + 1));
                        dataObj2 = checkKg(dataObj2, projectData, configField);
                        double jrjzmj2 = StringUtils.isEmpty(dataObj2.getString("jrjzmj")) ? 0 : dataObj2.getDouble("jrjzmj");
                        sumArea += jrjzmj2;
                        twoMap.put("twojsyd", dataObj2);
                        twoMap.put("jsydcount", "two");

                        if ("0".equals(cgydxz)) {
                            cgydxz = getStatus(dataObj2, kgType.getString("typeField"));
                        }
                    }
                    list.add(twoMap);
                }
                kgType.put("area", sumArea == 0 ? 0 : String.format("%.2f", sumArea));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        kgType.put("dataList", list);
        kgType.put("cgydxz", cgydxz);
        kgType.put("hasDiffLandType", hasDiffLandType);
        return kgType;
    }

    /**
     * 控规判断是否有非建设用地
     *
     * @param dataObj
     * @param typeField
     * @return
     */
    private String getStatus(JSONObject dataObj, String typeField) {
        String cgydxz = "0";
        String fjsydType = "E,G";
        try {
            String landCode = dataObj.getString(typeField);
            for (String type : fjsydType.split(",")) {//控制性详细规划是否有非建设用地（0：建设用地，1：非建设用地）
                if (landCode.indexOf(type) > -1) {
                    cgydxz = "1";
                    break;
                }
            }
        } catch (Exception e) {

        }
        return cgydxz;
    }

    /**
     * 控规数据检测
     *
     * @param kg
     * @param projectData
     * @return
     */
    private JSONObject checkKg(JSONObject kg, JSONObject projectData, JSONObject configField) {
        //用地性质
        String fjsydType = "E,G";
        String ydxz = "";
        String landNameField = configField.getString("kg_landname_field"); //控规用地类型名称字段
        String landCodeField = configField.getString("kg_landcode_field"); //控规用地类型编码字段
        String buildDenField = configField.getString("kg_buildden_field"); // 控规建筑密度字段
        String buildHght_Field = configField.getString("kg_buildhght_field"); //控规限高字段
        String buildRatioField = configField.getString("kg_buildratio_field"); //控规容积率字段
        String greenRatioField = configField.getString("kg_greenratio_field"); //控规绿地率字段
        if (kg.containsKey(landNameField)) {
            ydxz = StringUtils.isEmpty(kg.getString(landNameField)) ? "" : kg.getString(landNameField);
        }

        kg.put("ydxz", ydxz);
        String landCode = "";
        if (kg.containsKey(landCodeField)) {
            landCode = kg.getString(landCodeField);
        }
        boolean jsyd = true;// 是否为建设用地,true是,false否
        String emptyValue = "";
        String[] fjsydTypes = fjsydType.split(",");
        for (String type : fjsydTypes) {
            if (landCode.indexOf(type) > -1) {
                jsyd = false;
                emptyValue = "--";
                break;
            }
        }
        boolean flag = false;
        String landTypeCode = "";
        if (projectData.has("landTypeCode")) {
            landTypeCode = projectData.getString("landTypeCode");
        }
        if (StringUtils.isNotEmpty(landTypeCode)) {
            flag = landTypeCode.indexOf(landCode) > -1;
        }
        String ydxzCheck = flag && StringUtils.isNotEmpty(ydxz) ? "是" : "否";
        String ydxzColor = flag && StringUtils.isNotEmpty(ydxz) ? "auto" : "FF0000";
        kg.put("ydxzCheck", ydxzCheck);
        kg.put("ydxzColor", ydxzColor);

        // 容积率
        double xmRjl = 0;
        if (projectData.has("xmRjl")) {
            try {
                xmRjl = projectData.getDouble("xmRjl");
            } catch (Exception e) {
                xmRjl = -1;
            }
        }
        String rjlStr = "";
        if (kg.containsKey(buildRatioField)) {
            rjlStr = kg.getString(buildRatioField);
        }
        rjlStr = StringUtils.isEmpty(rjlStr) ? emptyValue : rjlStr;
        double rjl = 0;
        String rjlColor = "auto";
        String rjlCheck = emptyValue;
        Boolean isRjlEmpty = false;
        try {
            rjl = Double.valueOf(rjlStr.trim());
        } catch (Exception e) {
            rjlStr = emptyValue;
            isRjlEmpty = true;
        }
        kg.put("rjl", rjlStr);
        if (!"--".equals(rjlStr) && !"".equals(rjlStr) && xmRjl != -1) {
            rjlColor = rjl >= xmRjl ? "auto" : "FF0000";
            rjlCheck = rjl >= xmRjl ? "是" : "否";
        } else {
            rjlCheck = "是";
        }
        kg.put("rjlCheck", rjlCheck);
        kg.put("rjlColor", rjlColor);

        // 绿地率
        double xmLdl = 0;
        if (projectData.has("xmLdl")) {
            try {
                xmLdl = projectData.getDouble("xmLdl");
            } catch (Exception e) {
                xmLdl = -1;
            }
        }

        String ldlStr = "";
        if (kg.containsKey(greenRatioField)) {
            ldlStr = kg.getString(greenRatioField);
        }
        ldlStr = StringUtils.isEmpty(ldlStr) ? emptyValue : ldlStr;
        double ldl = 0;
        String ldlColor = "auto";
        String ldlCheck = emptyValue;
        try {
            ldl = Double.valueOf(ldlStr.trim());
        } catch (Exception e) {
            ldlStr = emptyValue;
        }
        kg.put("ldl", ldlStr);
        if (!"--".equals(ldlStr) && !"".equals(ldlStr) && xmLdl != -1) {
            ldlColor = ldl < xmLdl ? "auto" : "FF0000";
            ldlCheck = ldl < xmLdl ? "是" : "否";
        } else {
            ldlCheck = "是";
        }
        kg.put("ldlCheck", ldlCheck);
        kg.put("ldlColor", ldlColor);

        // 建筑密度
        double xmJzmd = 0;
        if (projectData.has("xmJzmd")) {
            try {
                xmJzmd = projectData.getDouble("xmJzmd");
            } catch (Exception e) {
                xmJzmd = -1;
            }
        }

        String jzmdStr = emptyValue;
        if (kg.containsKey(buildDenField)) {
            jzmdStr = kg.getString(buildDenField);
        }
        jzmdStr = StringUtils.isEmpty(jzmdStr) ? emptyValue : jzmdStr;
        double jzmd = 0;
        String jzmdColor = "auto";
        String jzmdCheck = emptyValue;
        try {
            jzmd = Double.valueOf(ldlStr.trim());
        } catch (Exception e) {
            jzmdStr = emptyValue;
        }
        kg.put("jzmd", jzmdStr);
        if (!"--".equals(jzmdStr) && !"".equals(jzmdStr) && xmJzmd != -1) {
            jzmdColor = jzmd >= xmJzmd ? "auto" : "FF0000";
            jzmdCheck = jzmd >= xmJzmd ? "是" : "否";
        } else {
            jzmdCheck = "是";
        }
        kg.put("jzmdCheck", jzmdCheck);
        kg.put("jzmdColor", jzmdColor);

        // 限高
        double xmXg = 0;
        if (projectData.has("xmXg")) {
            try {
                xmXg = projectData.getDouble("xmXg");
            } catch (Exception e) {
                xmXg = -1;
            }
        }
        String xgStr = emptyValue;
        if (kg.containsKey(buildHght_Field)) {
            xgStr = kg.getString(buildHght_Field);
        }
        xgStr = StringUtils.isEmpty(xgStr) ? emptyValue : xgStr;
        double xg = 0;
        String xgColor = "auto";
        String xgCheck = emptyValue;
        try {
            xg = Double.valueOf(xgStr.trim());
        } catch (Exception e) {
            xgStr = emptyValue;
        }
        kg.put("xg", xgStr);
        if (!"--".equals(xgStr) && !"".equals(xgStr) && xmXg != -1) {
            xgColor = xg >= xmXg ? "auto" : "FF0000";
            xgCheck = xg >= xmXg ? "是" : "否";
        } else {
            xgCheck = "是";
        }
        if ("".equals(xgStr) && xmXg == -1) {
            xgCheck = emptyValue;
        }
        kg.put("xgCheck", xgCheck);
        kg.put("xgColor", xgColor);

        // 计容建筑面积
        if (isRjlEmpty || "水域".equals(ydxz.trim())) {
            kg.put("jrjzmj", "");
            if ("水域".equals(ydxz.trim())) {
                kg.put("rjl", "");
                kg.put("ldl", "");
                kg.put("jzmd", "");
                kg.put("xg", "");
                kg.put("cssjyq", "");
                kg.put("ptss", "");
            }
        } else {
            double area = kg.getDouble("area");
            double jrjzmj = area * rjl;
            kg.put("jrjzmj", jrjzmj == 0 ? 0 : String.format("%.2f", jrjzmj));
        }
        return kg;
    }

    /**
     * 土规数据处理
     *
     * @param tgType
     * @param projectData
     * @param basePath
     * @return
     */
    private JSONObject grantTg(JSONObject tgType, JSONObject projectData, String basePath) {
        double nydArea = 0d;
        double jsydArea = 0d;
        double qtArea = 0d;
        JSONArray dataList = tgType.getJSONArray("dataList");
       /* String range = tgType.getString("range");
        JSONArray ytfls = new JSONArray(); //土地规划用途分类
        try {
            HttpRespons httpRespons = new HttpRequester().sendGet(basePath + "agsupport/dic/getAgDicByTypeCode/A120");
            ytfls = JSONArray.fromObject(httpRespons.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray dataList = new JSONArray();*/
        /*if (tgType.getBoolean("ck") == true) {
            dataList = tgType.getJSONArray("dataList");
            tgType.remove("dataList");
        }
        List<JSONObject> list = new ArrayList<JSONObject>();
        for (int j = 0; j < ytfls.size(); j++) {//根据分类拼装数据
            JSONObject ytfl = ytfls.getJSONObject(j);
            ytfl.put("area", 0);
            String ytflCode = ytfl.getString("code");
            for (int i = 0; i < dataList.size(); i++) {
                JSONObject data = dataList.getJSONObject(i);
                String type = data.getString(tgType.getString("typeField"));
                if (StringUtils.isNotEmpty(type) && ytflCode.charAt(0) == type.charAt(0)) {
                    ytfl.put("area", ytfl.getDouble("area") + data.getDouble("area"));
                }
            }
            ytfl.put("area", String.format("%.2f", ytfl.getDouble("area")));
            list.add(ytfl);
        }*/
        /**
         * 检测条件
         */
        /*if (StringUtils.isNotEmpty(range)) {
            String newRange = "";
            for (int i = 0; i < range.split(",").length; i++) {
                String val = range.split(",")[i];
                if (StringUtils.isEmpty(val)) {
                    continue;
                }
                val = val.substring(0, 1);
                if ("".equals(newRange)) {
                    newRange = val;
                } else {
                    newRange += "," + val;
                }
            }
            range = newRange;
        }
        for (int j = 0; j < list.size(); j++) {
            JSONObject ytfl = list.get(j);
            String ytflCode = ytfl.getString("code");
            long checkStatus = 0;//不通过
            if (StringUtils.isNotEmpty(range)) {
                if (tgType.getLong("regulation") == 1) {//允许
                    if (range.indexOf(ytflCode.substring(0, 1)) > -1) { // 通过
                        checkStatus = 1;
                    }
                } else {//不允许
                    if (range.indexOf(ytflCode.substring(0, 1)) == -1) { // 通过
                        checkStatus = 1;
                    }
                }

                if ("1".charAt(0) == ytflCode.charAt(0)) {//农用地 不能占用
                    if (ytfl.getDouble("area") > 0) {
                        nydArea = nydArea + ytfl.getDouble("area");
                        ytfl.put("checkStatus", 0);
                        tgType.put("checkStatus", 0);
                    }
                } else if ("2".charAt(0) == ytflCode.charAt(0)) {//建设用地
                    jsydArea = jsydArea + ytfl.getDouble("area");
                } else if ("3".charAt(0) == ytflCode.charAt(0)) {//其他土地

                }
            }
            ytfl.put("checkStatus", checkStatus);
        }
        tgType.put("dataList", list);*/
        for (int i = 0; i < dataList.size(); i++) {
            JSONObject data = dataList.getJSONObject(i);
            String type = data.getString(tgType.getString("typeField"));
            if (StringUtils.isEmpty(type)) continue;
            if ("1".charAt(0) == type.charAt(0)) {//农用地 不能占用
                if (data.getDouble("area") > 0) {
                    nydArea = nydArea + data.getDouble("area");
                }
            } else if ("2".charAt(0) == type.charAt(0)) {//建设用地
                jsydArea = jsydArea + data.getDouble("area");
            } else if ("3".charAt(0) == type.charAt(0)) {//其他土地
                qtArea = qtArea + data.getDouble("area");
            }
        }

        tgType.put("nydArea", nydArea);
        tgType.put("jsydArea", jsydArea);
        tgType.put("qtArea", qtArea);
        return tgType;
    }

    private String getCheckResult() {
        return null;
    }

    /**
     * 根据图层ID导入
     *
     * @param layerIds
     * @param request
     * @return
     */
    @RequestMapping("/saveByLayerIds")
    public String saveByLayerIds(String layerIds, HttpServletRequest request) {
        JSONObject jsonObject = this.getLogInfo(request, "根据图层ID导入");
        try {
            iAeaMapComplianceCheck.saveByLayerIds(layerIds.split(","));
            jsonObject.put("operResult", "成功");
            logger.info(jsonObject.toString());
            return JsonUtils.toJson(new ResultForm(true));
        } catch (Exception e) {
            jsonObject.put("operResult", "失败");
            if (e.getMessage() != null) {
                jsonObject.put("exceptionMessage", e.getMessage().substring(0, e.getMessage().length() > 500 ? 500 : e.getMessage().length()));
            }
            logger.error(jsonObject.toString());
            e.printStackTrace();
        }
        return JsonUtils.toJson(new ResultForm(false, "保存失败！"));
    }

    /**
     * 根据图层ID导入
     *
     * @param layersStr
     * @param request
     * @return
     */
    @RequestMapping("/saveLayers")
    public String saveLayers(String layersStr, HttpServletRequest request) {
        JSONObject jsonObject = this.getLogInfo(request, "根据图层ID导入");
        JSONArray layerArray = JSONArray.fromObject(layersStr);
        try {
            List<AeaMapComplianceCheck> layerList = JSONArray.toList(layerArray, AeaMapComplianceCheck.class);
            for(Object obj: layerList) {
                AeaMapComplianceCheck layer = (AeaMapComplianceCheck) obj;
                layer.setId(UUID.randomUUID().toString());
            }
            iAeaMapComplianceCheck.saveLayers(layerList);
            jsonObject.put("operResult", "成功");
            logger.info(jsonObject.toString());
            return JsonUtils.toJson(new ResultForm(true));
        } catch (Exception e) {
            jsonObject.put("operResult", "失败");
            if (e.getMessage() != null) {
                jsonObject.put("exceptionMessage", e.getMessage().substring(0, e.getMessage().length() > 500 ? 500 : e.getMessage().length()));
            }
            logger.error(jsonObject.toString());
            e.printStackTrace();
        }
        return JsonUtils.toJson(new ResultForm(false, "保存失败！"));
    }

    /**
     * 同步项目合规性检测结果及其他状态数据
     *
     * @param projectStatus
     */
    private void syncProjectStatus(JSONObject projectStatus) {
        try {
            Map param = new HashMap<>();
            param.put("jsonStr", projectStatus.toString());
            String url = hurdUrl + "/rest/ProjectRests/saveComplianceCheckResult";
            url= ConfigUtil.getLanUrl(url,null);
            HttpRespons httpRespons = new HttpRequester().sendPost(url, param);
            String content = httpRespons.getContent();
        } catch (Exception e) {
            logger.error(e.toString());
            e.printStackTrace();
        }
    }

    @RequestMapping("/getProjectTypes")
    public String getProjectTypes() {
        String url = hurdUrl + "/rest/system/getItemsByTypeCode/GBHYDMFBND-2017";
        HttpRespons httpRespons = null;
        try {
            url= ConfigUtil.getLanUrl(url,null);
            httpRespons = new HttpRequester().sendGet(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String content = httpRespons.getContent();
        return content;
    }

    /**
     * 获取图层数据列表
     *
     * @param request
     * @return
     */
    @RequestMapping("/getAllByItemCode")
    public String getAllByItemCode(String itemCode, String status, HttpServletRequest request) {
        ResultForm result = new ResultForm(true);
        try {
            List<AeaMapComplianceProject> list = new ArrayList<AeaMapComplianceProject>();
            if (status == null) {
                list = iAeaMapComplianceProject.getAllByItemCode(itemCode);
            } else {
                AeaMapComplianceProject project = new AeaMapComplianceProject();
                project.setItemCode(itemCode);
                project.setStatus(status);
                list = iAeaMapComplianceProject.getAllByItemCodeAndStatus(project);
            }
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
     * 保存或修改合规性检查图层项目关联信息
     *
     * @param listStr
     * @param request
     * @return
     */
    @RequestMapping("/saveOrUpdateProject")
    public String saveOrUpdateProject(String listStr, HttpServletRequest request) {
        ResultForm resultForm = new ResultForm(true);
        try {
            if (StringUtils.isNotEmpty(listStr)) {
                JSONArray list = JSONArray.fromObject(listStr);
                for (int i = 0; i < list.size(); i++) {
                    JSONObject obj = list.getJSONObject(i);
                    AeaMapComplianceProject aeaMapComplianceProject = (AeaMapComplianceProject) JSONObject.toBean(obj, AeaMapComplianceProject.class);
                    if (StringUtils.isEmpty(aeaMapComplianceProject.getId())) { //新增保存
                        aeaMapComplianceProject.setId(UUID.randomUUID().toString());
                        iAeaMapComplianceProject.save(aeaMapComplianceProject);
                    } else {//修改
                        iAeaMapComplianceProject.update(aeaMapComplianceProject);
                    }
                }
            }
        } catch (Exception e) {
            resultForm.setSuccess(false);
            resultForm.setMessage("保存失败！");
            e.printStackTrace();
        }
        return JsonUtils.toJson(resultForm);
    }
}
