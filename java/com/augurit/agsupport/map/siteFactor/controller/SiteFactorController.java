package com.augurit.agsupport.map.siteFactor.controller;

import com.alibaba.fastjson.JSON;
import com.augurit.agcloud.agcom.agsupport.domain.AgDic;
import com.augurit.agcloud.agcom.agsupport.sc.dic.service.IAgDic;
import com.augurit.agcloud.framework.ui.result.ResultForm;
import com.augurit.agcloud.framework.util.JsonUtils;
import com.augurit.agsupport.map.siteFactor.domain.AeaMapSiteFactor;
import com.augurit.agsupport.map.siteFactor.domain.AeaMapSiteFactorProject;
import com.augurit.agsupport.map.siteFactor.service.IAeaMapSiteFactor;
import com.augurit.agsupport.map.siteFactor.service.IAeaMapSiteFactorProject;
import com.augurit.agsupport.map.util.CommonUtil;
import com.augurit.agsupport.map.util.DocumentHandler;
import com.common.util.HttpRequester;
import com.common.util.HttpRespons;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 辅助选址-因子配置
 */
@RestController
@RequestMapping("/agsupport/siteFactor")
public class SiteFactorController {
    private static Logger logger = LoggerFactory.getLogger(SiteFactorController.class);
    @Autowired
    private IAeaMapSiteFactor iAeaMapSiteFactor;
    @Autowired
    private IAeaMapSiteFactorProject iAeaMapSiteFactorProject;
    @Autowired
    private IAgDic iAgDic;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    @RequestMapping("/index.html")
    public ModelAndView index(HttpServletRequest request, Model model) throws Exception {
        return new ModelAndView("agsupport/map/siteFactor/siteFactor");
    }

    @RequestMapping("/getAll")
    public String getAll() {
        ResultForm result = new ResultForm(true);
        try {
            List<AeaMapSiteFactor> list = iAeaMapSiteFactor.getAll();
            JSONArray array = JSONArray.fromObject(list);
            result.setMessage(array.toString());
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return JsonUtils.toJson(result);
    }

    @RequestMapping("/getTreeData")
    public String getTreeData() {
        ResultForm result = new ResultForm(true);
        try {
            List<AeaMapSiteFactor> list = iAeaMapSiteFactor.getAll();
            List<AeaMapSiteFactor> parents = list.stream()
                    .filter(item -> StringUtils.isBlank(item.getParentId())).map(item -> {
                        item.setChildren(new ArrayList<AeaMapSiteFactor>());
                        return item;
                    }).collect(Collectors.toList());
            List<AeaMapSiteFactor> childrens = list.stream()
                    .filter(item -> StringUtils.isNotBlank(item.getParentId())).map(item -> {
                        item.setChildren(new ArrayList<AeaMapSiteFactor>());
                        return item;
                    }).collect(Collectors.toList());
            iterator(parents, childrens);
            result.setMessage(JSON.toJSONString(parents));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return JsonUtils.toJson(result);
    }

    @RequestMapping("/saveOrUpdate")
    public String saveOrUpdate(String dataStr) {
        ResultForm result = new ResultForm(true);
        try {
            JSONArray array = JSONArray.fromObject(dataStr);
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                AeaMapSiteFactor aeaMapSiteFactor = (AeaMapSiteFactor) JSONObject.toBean(object, AeaMapSiteFactor.class);
                if (StringUtils.isEmpty(aeaMapSiteFactor.getId()) || "-1".equals(aeaMapSiteFactor.getId())) { //新增保存
                    aeaMapSiteFactor.setId(UUID.randomUUID().toString());
                    iAeaMapSiteFactor.save(aeaMapSiteFactor);
                } else {
                    iAeaMapSiteFactor.update(aeaMapSiteFactor);
                }
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return JsonUtils.toJson(result);
    }

    /**
     * 删除
     *
     * @param paramIds
     * @return
     */
    @RequestMapping("/delete")
    public String delete(String paramIds, HttpServletRequest request) {
        ResultForm result = new ResultForm(true);
        String ids[] = null;
        if (StringUtils.isNotEmpty(paramIds)) {
            ids = paramIds.split(",");
        }
        try {
            if (ids != null && ids.length > 0) {
                iAeaMapSiteFactor.delete(ids);
            }
        } catch (Exception e) {
            result.setSuccess(false);
            e.printStackTrace();
        }
        return JsonUtils.toJson(result);
    }

    /**
     * 根据图层ID导入
     * @param layersStr
     * @param parentId
     * @param request
     * @return
     */
    @RequestMapping("/saveLayers")
    public String saveLayers(String layersStr, String parentId, HttpServletRequest request) {
        ResultForm result = new ResultForm(true);
        try {
            JSONArray layerArray = JSONArray.fromObject(layersStr);
            List<AeaMapSiteFactor> layerList = JSONArray.toList(layerArray,AeaMapSiteFactor.class);
            for(Object obj: layerList) {
                AeaMapSiteFactor layer = (AeaMapSiteFactor) obj;
                layer.setId(UUID.randomUUID().toString());
            }
            iAeaMapSiteFactor.saveLayers(layerList, parentId);
        } catch (Exception e) {
            result.setSuccess(false);
            e.printStackTrace();
        }
        return JsonUtils.toJson(result);
    }

    /**
     * 根据图层ID导入
     *
     * @param layerIds
     * @param request
     * @return
     */
    @RequestMapping("/saveByLayerIds")
    public String saveByLayerIds(String layerIds, String parentId, HttpServletRequest request) {
        ResultForm result = new ResultForm(true);
        try {
            iAeaMapSiteFactor.saveByLayerIds(layerIds.split(","), parentId);
        } catch (Exception e) {
            result.setSuccess(false);
            e.printStackTrace();
        }
        return JsonUtils.toJson(result);
    }

    /**
     * 获取数据列表
     *
     * @param projectCode
     * @param request
     * @return
     */
    @RequestMapping("/getAllByProjectCode")
    public String getAllByProjectCode(String projectCode, HttpServletRequest request) {
        List<AeaMapSiteFactorProject> result = new ArrayList<>();
        try {
            result = iAeaMapSiteFactorProject.getAllByProjectCode(projectCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONArray.fromObject(result).toString();
    }

    /**
     * 保存或修改
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
                    AeaMapSiteFactorProject project = (AeaMapSiteFactorProject) JSONObject.toBean(obj, AeaMapSiteFactorProject.class);
                    if (StringUtils.isEmpty(project.getId())) {
                        project.setId(UUID.randomUUID().toString());
                        iAeaMapSiteFactorProject.save(project);
                    } else {
                        iAeaMapSiteFactorProject.update(project);
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

    /**
     * 根据项目性质编码获取管制分类的数据
     *
     * @param projectCode
     * @param request
     * @return
     */
    @RequestMapping("/getByContorlTypeAndProjectCode")
    public String getByContorlTypeAndProjectCode(String projectCode, String contorlType, HttpServletRequest request) {
        ResultForm resultForm = new ResultForm(true);
        try {
            List<AeaMapSiteFactorProject> result = new ArrayList<>();
            if (StringUtils.isNotEmpty(projectCode) && StringUtils.isNotEmpty(contorlType)) {
                result = getSiteFactorsByProjectCodeAndContorlType(projectCode, contorlType);
            }
            resultForm.setMessage(JSONArray.fromObject(result).toString());
        } catch (Exception e) {
            resultForm.setSuccess(false);
            resultForm.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return JsonUtils.toJson(resultForm);
    }

    /**
     * 根据项目性质编码获取管制分类的树状数据
     *
     * @param projectCode
     * @param request
     * @return
     */
    @RequestMapping("/getTreeDataByContorlTypeAndProjectCode")
    public String getTreeDataByContorlTypeAndProjectCode(String projectCode, String contorlType, HttpServletRequest request) {
        ResultForm resultForm = new ResultForm(true);
        try {
            List<AeaMapSiteFactorProject> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(projectCode) && StringUtils.isNotEmpty(contorlType)) {
                list = getSiteFactorsByProjectCodeAndContorlType(projectCode, contorlType);
            }
            List<AeaMapSiteFactorProject> parents = list.stream()
                    .filter(item -> StringUtils.isBlank(item.getParentId())).map(item -> {
                        item.setChildren(new ArrayList<AeaMapSiteFactorProject>());
                        return item;
                    }).collect(Collectors.toList());
            List<AeaMapSiteFactorProject> childrens = list.stream()
                    .filter(item -> StringUtils.isNotBlank(item.getParentId())).map(item -> {
                        item.setChildren(new ArrayList<AeaMapSiteFactorProject>());
                        return item;
                    }).collect(Collectors.toList());
            iteratorAeaMapSiteFactorProject(parents, childrens);
            resultForm.setMessage(JSON.toJSONString(parents));
        } catch (Exception e) {
            resultForm.setSuccess(false);
            resultForm.setMessage("保存失败！");
            e.printStackTrace();
        }
        return JsonUtils.toJson(resultForm);
    }

    /**
     * 根据项目ID获取因子规则配置
     *
     * @param projectCode
     * @return
     */
    @RequestMapping("/getByProjectCode")
    public String getByProjectCode(String projectCode, HttpServletRequest request) {
        List<AeaMapSiteFactorProject> result = new ArrayList<>();
        try {
            String listStr = getControlTypes(request);
            JSONArray controlTypes = JSONArray.fromObject(listStr);
            for (int i = 0; i < controlTypes.size(); i++) {
                JSONObject jsonObject = controlTypes.getJSONObject(i);
                List<AeaMapSiteFactorProject> list = getSiteFactorsByProjectCodeAndContorlType(projectCode, jsonObject.getString("code"));
                if (list.size() > 0) {
                    AeaMapSiteFactorProject controlTypeP = new AeaMapSiteFactorProject();//用于构建成根据管控要求的树
                    String id = "controlType_" + jsonObject.getString("id");
                    controlTypeP.setId(id);
                    controlTypeP.setFactorId(id);
                    controlTypeP.setType("1");
                    controlTypeP.setSeq(Long.valueOf(i + 1));
                    controlTypeP.setName(jsonObject.getString("name"));
                    for (AeaMapSiteFactorProject siteFactor : list) {
                        if (siteFactor.getType() != null && "1".equals(siteFactor.getType())) {//把目录挂到管控下
                            siteFactor.setParentId(id);
                        }
                    }
                    list.add(controlTypeP);
                }
                result.addAll(list);
            }
            result.sort(new Comparator<AeaMapSiteFactorProject>() {
                @Override
                public int compare(AeaMapSiteFactorProject o1, AeaMapSiteFactorProject o2) {
                    return (int) (o1.getSeq() - o2.getSeq());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONArray.fromObject(result).toString();
    }

    /**
     * 通过项目类型ID和管控类型查询数据
     *
     * @param projectCode
     * @param contorlType
     * @return
     */
    private List<AeaMapSiteFactorProject> getSiteFactorsByProjectCodeAndContorlType(String projectCode, String contorlType) {
        List<AeaMapSiteFactorProject> result = new ArrayList<>();
        try {
            List<AeaMapSiteFactorProject> list = iAeaMapSiteFactorProject.getAllByProjectCode(projectCode);
            //开始封装数据，有效因子的目录数据及有效的因子数据
            List<AeaMapSiteFactorProject> catalogs = new ArrayList<>();
            List<AeaMapSiteFactorProject> nodes = new ArrayList<>();
            for (AeaMapSiteFactorProject project : list) {
                if ("1".equals(project.getType())) {//目录
                    for (int i = 0; i < list.size(); i++) {
                        AeaMapSiteFactorProject project1 = list.get(i);
                        if ("1".equals(project1.getType())) {//目录
                            continue;
                        }
                        if (project1.getParentId().equals(project.getFactorId()) && StringUtils.isNotEmpty(project1.getParentId()) && StringUtils.isNotEmpty(project1.getUrl())) {
                            catalogs.add(project);
                            break;
                        }
                    }
                } else if ("0".equals(project.getType()) && StringUtils.isNotEmpty(project.getUrl())) {//因子且有服务地址的因子
                    nodes.add(project);
                }
            }
            //----结束----
            for (AeaMapSiteFactorProject project : nodes) {
                if (project.getMajor().equals(contorlType)) {
                    result.add(project);
                    for (int j = 0; j < catalogs.size(); j++) {
                        AeaMapSiteFactorProject catalog = catalogs.get(j);
                        if (project.getParentId().equals(catalog.getFactorId()) && !result.contains(catalog)) {
                            result.add(catalog);
                        }
                    }
                }
            }
            result.sort(new Comparator<AeaMapSiteFactorProject>() {
                @Override
                public int compare(AeaMapSiteFactorProject o1, AeaMapSiteFactorProject o2) {
                    return (int) (o1.getSeq() - o2.getSeq());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取管控类型
     *
     * @param request
     * @return
     */
    @RequestMapping("/getControlTypes")
    public String getControlTypes(HttpServletRequest request) {
        ResultForm result = new ResultForm(true);
        JSONArray controlTypes = new JSONArray(); //管控类型
        try {
            List<AgDic> agDics = iAgDic.getAgDicByTypeCode("controlType");
            controlTypes = JSONArray.fromObject(agDics);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return controlTypes.toString();
    }

    @RequestMapping("/siteSelectingReport")
    public String siteSelectingReport(String gridData, String projectName, HttpServletRequest request) throws Exception {
        ResultForm result = new ResultForm(true);
        try {
            JSONArray array = JSONArray.fromObject(gridData);
            String basePath = request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort() + request.getContextPath()
                    + "/";
            String webPath = request.getSession().getServletContext()
                    .getRealPath("/");
            Map<String, Object> content = new HashMap<String, Object>();
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            String filePath = CommonUtil.DEFAULT_ATTACHMENT_FILEPATH
                    .concat(File.separator).concat("辅助选址").concat(File.separator)
                    .concat(String.valueOf(c.get(Calendar.YEAR)))
                    .concat(File.separator)
                    .concat(String.valueOf(c.get(Calendar.MONTH) + 1))
                    .concat(File.separator).concat(sdf.format(c.getTime()))
                    .concat("辅助选址方案报告.doc");
            File f = new File(webPath.concat(filePath));
            if (!f.exists()) {
                content.put("proName", projectName);
                List datas = new ArrayList();
                for (int i = 0; i < array.size(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    jsonObject.put("plan",
                            "方案" + CommonUtil.formatInteger(jsonObject.getInt("planSeq")) + "(" + jsonObject.getString("landId") + ")");// 方案
                    jsonObject
                            .put("location", StringUtils.isEmpty(jsonObject.getString("location")) ? "" : jsonObject.getString("location"));// 位置
                    jsonObject
                            .put("landId", StringUtils.isEmpty(jsonObject.getString("landId")) ? "" : jsonObject.getString("landId"));// 用地编码
                    jsonObject
                            .put("landName", jsonObject.has("landName") ? jsonObject.getString("landName") : "");// 用地性质
                    jsonObject
                            .put("landArea", StringUtils.isEmpty(jsonObject.getString("landArea")) ? "" : jsonObject.getString("landArea"));// 用地面积
                    jsonObject
                            .put("score", jsonObject.getString("score"));// 得分

                    // jsonObject.put("imgs",
                    // jsonObject.getJSONArray("imgs"));//规划情况即图片
                    String resultMessage = StringUtils.isEmpty(jsonObject
                            .getString("resultMessage")) ? "" : jsonObject
                            .getString("resultMessage");
                    resultMessage = resultMessage
                            .replaceAll(
                                    "<p style=\"font-weight: bold;\">",
                                    "<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"002A58AC\"><w:pPr><w:rPr><w:rFonts w:hint=\"fareast\"/><w:b/><w:b-cs/><w:sz w:val=\"24\"/><w:sz-cs w:val=\"21\"/></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:hint=\"fareast\"/><wx:font wx:val=\"仿宋_GB2312\"/><w:b/><w:b-cs/><w:sz w:val=\"24\"/><w:sz-cs w:val=\"21\"/></w:rPr><w:t>");
                    resultMessage = resultMessage
                            .replaceAll(
                                    "<p>",
                                    "<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"002A58AC\"><w:pPr><w:rPr><w:rFonts w:hint=\"fareast\"/><w:b-cs/><w:sz w:val=\"24\"/><w:sz-cs w:val=\"21\"/></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:hint=\"fareast\"/><wx:font wx:val=\"仿宋_GB2312\"/><w:b-cs/><w:sz w:val=\"24\"/><w:sz-cs w:val=\"21\"/></w:rPr><w:t>");
                    resultMessage = resultMessage.replaceAll("</p>",
                            "</w:t></w:r></w:p>");
                    jsonObject.put("resultMessage", resultMessage);
                    datas.add(jsonObject);
                }
                content.put("datas", datas);
                // 生成文档
                DocumentHandler handler = new DocumentHandler(webPath);
                handler.createDoc(content, "siteSelecting.ftl", filePath);
            }
            String url = CommonUtil.DEFAULT_ATTACHMENT_FILEPATH
                    .concat(File.separator).concat("辅助选址").concat(File.separator)
                    .concat(String.valueOf(c.get(Calendar.YEAR))).concat(File.separator)
                    .concat(String.valueOf(c.get(Calendar.MONTH) + 1))
                    .concat(File.separator).concat(sdf.format(c.getTime()))
                    .concat("辅助选址方案报告.doc");
            result.setMessage(basePath + url);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
        }
        return JsonUtils.toJson(result);
    }

    public void iterator(List<AeaMapSiteFactor> parents, List<AeaMapSiteFactor> childrens) {
        for (int i = 0; i < parents.size(); i++) {
            AeaMapSiteFactor currentParentItem = parents.get(i);
            for (int j = 0; j < childrens.size(); j++) {
                AeaMapSiteFactor currentChildItem = childrens.get(j);
                if (currentChildItem.getParentId().equals(currentParentItem.getId())) {
                    String jsonStr = JSON.toJSONString(childrens);
                    List<AeaMapSiteFactor> _children = com.alibaba.fastjson.JSONObject.parseArray(jsonStr, AeaMapSiteFactor.class);
                    _children.remove(j);
                    List<AeaMapSiteFactor> iteratorList = new ArrayList<>();
                    iteratorList.add(currentChildItem);
                    iterator(iteratorList, _children);
                    if (currentParentItem.getChildren() == null) {
                        List<AeaMapSiteFactor> _list = new ArrayList<>();
                        _list.add(currentChildItem);
                        currentParentItem.setChildren(_list);
                    } else {
                        currentParentItem.getChildren().add(currentChildItem);
                    }
                }
            }
        }
    }

    public void iteratorAeaMapSiteFactorProject(List<AeaMapSiteFactorProject> parents, List<AeaMapSiteFactorProject> childrens) {
        for (int i = 0; i < parents.size(); i++) {
            AeaMapSiteFactorProject currentParentItem = parents.get(i);
            for (int j = 0; j < childrens.size(); j++) {
                AeaMapSiteFactorProject currentChildItem = childrens.get(j);
                if (currentChildItem.getParentId().equals(currentParentItem.getFactorId())) {
                    String jsonStr = JSON.toJSONString(childrens);
                    List<AeaMapSiteFactorProject> _children = com.alibaba.fastjson.JSONObject.parseArray(jsonStr, AeaMapSiteFactorProject.class);
                    _children.remove(j);
                    List<AeaMapSiteFactorProject> iteratorList = new ArrayList<>();
                    iteratorList.add(currentChildItem);
                    iteratorAeaMapSiteFactorProject(iteratorList, _children);
                    if (currentParentItem.getChildren() == null) {
                        List<AeaMapSiteFactorProject> _list = new ArrayList<>();
                        _list.add(currentChildItem);
                        currentParentItem.setChildren(_list);
                    } else {
                        currentParentItem.getChildren().add(currentChildItem);
                    }
                }
            }
        }
    }
}
