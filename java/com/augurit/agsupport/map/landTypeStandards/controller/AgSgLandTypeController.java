package com.augurit.agsupport.map.landTypeStandards.controller;

import com.alibaba.fastjson.JSON;
import com.augurit.agcloud.framework.ui.result.ResultForm;
import com.augurit.agcloud.framework.util.JsonUtils;
import com.augurit.agsupport.map.landTypeStandards.domain.AgSgLandType;
import com.augurit.agsupport.map.landTypeStandards.service.IAgSgLandType;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/agsupport/landTypeStandards")
public class AgSgLandTypeController {
    private static Logger logger = LoggerFactory.getLogger(AgSgLandTypeController.class);

    @Autowired
    private IAgSgLandType iAgSgLandType;

    @RequestMapping("/index.html")
    public ModelAndView index(HttpServletRequest request, Model model) throws Exception {
        return new ModelAndView("agsupport/map/landTypeStandards/landTypeStandards");
    }

    /**
     * 获取用地分类列表
     *
     * @return
     */
    @RequestMapping("/getAllList")
    public String getAllList(HttpServletRequest request) {
        ResultForm result = new ResultForm(true);
        String code = request.getParameter("code");
        String name = request.getParameter("name");
        String type = request.getParameter("type");
        String version = request.getParameter("version");
        AgSgLandType agSgLandType = new AgSgLandType();
        agSgLandType.setCode(code);
        agSgLandType.setName(name);
        agSgLandType.setType(type);
        agSgLandType.setVersionCode(version);
        try {
            List<AgSgLandType> list = new ArrayList<AgSgLandType>();
            list = iAgSgLandType.findAllList(agSgLandType);
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
     * 获取用地分类列表(树结构)
     * @param request
     * @return
     */
    @RequestMapping("/getLandTypeTreeData")
    public String getLandTypeTreeData(AgSgLandType agSgLandType,HttpServletRequest request){
        ResultForm result = new ResultForm(true);
        try {
            List<AgSgLandType> list = iAgSgLandType.findAllList(agSgLandType);
            List<AgSgLandType> parents = list.stream()
                    .filter(item ->StringUtils.isBlank(item.getParentId())).map(item -> {
                        item.setChildren(new ArrayList<AgSgLandType>());
                        return item;
                    }).collect(Collectors.toList());
            List<AgSgLandType> childrens = list.stream()
                    .filter(item -> StringUtils.isNotBlank(item.getParentId())).map(item -> {
                        item.setChildren(new ArrayList<AgSgLandType>());
                        return item;
                    }).collect(Collectors.toList());
            iterator(parents,childrens);
            result.setMessage(JSON.toJSONString(parents));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return JsonUtils.toJson(result);
    }

    /**
     * 根据分类、版本号、层级、是否默认版本获取用地性质树状数据
     * @param versionCode
     * @param type
     * @param codeSort
     * @param isDefault
     * @return
     */
    @RequestMapping("/getTargetLandTypeTreeData")
    public String getTargetLandTypeTreeData(String versionCode, String type, String codeSort,String isDefault) {
        ResultForm resultForm = new ResultForm(true);
        try {
            List<AgSgLandType> list =  iAgSgLandType.getTargetLandTypes(versionCode,type,null,isDefault);
            List<AgSgLandType> parents = list.stream()
                    .filter(item ->codeSort.equals(item.getCodeSort())).map(item -> {
                        item.setChildren(new ArrayList<AgSgLandType>());
                        return item;
                    }).collect(Collectors.toList());
            List<AgSgLandType> childrens = list.stream()
                    .filter(item -> !codeSort.equals(item.getCodeSort())).map(item -> {
                        item.setChildren(new ArrayList<AgSgLandType>());
                        return item;
                    }).collect(Collectors.toList());
            iterator(parents,childrens);
            resultForm.setMessage(JSON.toJSONString(parents));
        }catch (Exception e) {
            resultForm.setSuccess(false);
            resultForm.setMessage("获取用地信息失败！");
        }
        return JsonUtils.toJson(resultForm);
    }

    /**
     * 根据分类、版本号、层级、是否默认版本获取用地性质
     * @param versionCode
     * @param type
     * @param codeSort
     * @param isDefault
     * @return
     */
    @RequestMapping("/getTargetLandTypes")
    public String getTargetLandTreeTypes(String versionCode, String type, String codeSort,String isDefault) {
        ResultForm resultForm = new ResultForm(true);
        try {
            List<AgSgLandType> list =  iAgSgLandType.getTargetLandTypes(versionCode,type,codeSort,isDefault);
            resultForm.setMessage(JSON.toJSONString(list));
        }catch (Exception e) {
            resultForm.setSuccess(false);
            resultForm.setMessage("获取用地信息失败！");
        }
        return JsonUtils.toJson(resultForm);
    }
    /**
     * 切换默认版本
     * @param versionCode
     * @param isDefault
     * @return
     */
    @RequestMapping("/toggleDefaultLandVersion")
    public String toggleDefaultLandVersion(String versionCode,String type,String isDefault) {
        ResultForm resultForm = new ResultForm(true);
        try {
            if(StringUtils.isNotEmpty(isDefault)) {
                iAgSgLandType.toggleDefaultLandVersion(versionCode,type,isDefault);
            }
        }catch (Exception e) {
            resultForm.setSuccess(false);
        }
        return JsonUtils.toJson(resultForm);
    }

    /**
     * 获取所有用地类型分类
     * @return
     */
    @RequestMapping("/getAllTypes")
    public String getAllTypes() {
        ResultForm resultForm = new ResultForm(true);
        try {
            List<String> list = iAgSgLandType.getAllTypes();
            resultForm.setMessage(JSON.toJSONString(list));
        }catch (Exception e) {
            resultForm.setSuccess(false);
        }
        return JsonUtils.toJson(resultForm);
    }

    /**
     * 获取所有用地类型下所有版本
     * @return
     */
    @RequestMapping("/getVersionsByType")
    public String getVersionsByType(String type) {
        ResultForm resultForm = new ResultForm(true);
        try {
            List<String> list = iAgSgLandType.getVersionsByType(type);
            resultForm.setMessage(JSON.toJSONString(list));
        }catch (Exception e) {
            resultForm.setSuccess(false);
        }
        return JsonUtils.toJson(resultForm);
    }

    /**
     * 保存或修改用地分类配置
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
                    AgSgLandType agSgLandType = (AgSgLandType) JSONObject.toBean(obj, AgSgLandType.class);
                    if (StringUtils.isBlank(agSgLandType.getId())) { //新增保存
                        agSgLandType.setId(UUID.randomUUID().toString());
                        iAgSgLandType.save(agSgLandType);
                    } else {//修改
                        iAgSgLandType.update(agSgLandType);
                    }
                }
            }else {
                resultForm.setMessage("保存失败！");
                resultForm.setSuccess(false);
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
        ResultForm resultForm = new ResultForm(true);
        String ids[] = null;
        if (StringUtils.isNotEmpty(paramIds)) {
            ids = paramIds.split(",");
        }
        try {
            if (ids != null && ids.length > 0) {
                iAgSgLandType.deleteBatch(ids);
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
     * 通过名称获取用地分类标准信息
     *
     * @return
     */
    @RequestMapping("/getInfoByName")
    public String getInfoByName() {
        ResultForm result = new ResultForm(true);
        try {
            List<AgSgLandType> list = new ArrayList<AgSgLandType>();
            AgSgLandType agSgLandType = new AgSgLandType();
            list = iAgSgLandType.findAllList(agSgLandType);
            JSONArray array = JSONArray.fromObject(list);
            result.setMessage(array.toString());
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return JsonUtils.toJson(result);
    }

    public void iterator(List<AgSgLandType> parents,List<AgSgLandType> childrens){
        for(int i = 0; i < parents.size(); i++) {
            AgSgLandType currentParentItem = parents.get(i);
            currentParentItem.setEditStatus(false);
            for(int j = 0;j < childrens.size(); j ++) {
                AgSgLandType currentChildItem = childrens.get(j);
                currentChildItem.setEditStatus(false);
                if(currentChildItem.getParentId().equals(currentParentItem.getId())) {
                    String jsonStr = JSON.toJSONString(childrens);
                    List<AgSgLandType> _children = com.alibaba.fastjson.JSONObject.parseArray(jsonStr,AgSgLandType.class);
                    _children.remove(j);
                    List<AgSgLandType> iteratorList = new ArrayList<>();
                    iteratorList.add(currentChildItem);
                    iterator(iteratorList,_children);
                    if(currentParentItem.getChildren() == null) {
                        List<AgSgLandType> _list = new ArrayList<>();
                        _list.add(currentChildItem);
                        currentParentItem.setChildren(_list);
                    }else {
                        currentParentItem.getChildren().add(currentChildItem);
                    }
                }
            }
        }
    }
}

