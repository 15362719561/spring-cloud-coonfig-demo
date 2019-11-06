package com.augurit.agsupport.map.conflictCheck.controller;


import com.augurit.agsupport.map.util.DocumentHandler;
import com.augurit.agsupport.map.conflictCheck.domain.AeaMapConflictCheck;
import com.augurit.agsupport.map.conflictCheck.domain.AeaMapConflictCheckLandType;
import com.augurit.agsupport.map.util.ImportExecl;
import com.augurit.agsupport.map.conflictCheck.service.IConflictCheck;
import com.augurit.agcloud.framework.ui.pager.EasyuiPageInfo;
import com.augurit.agcloud.framework.ui.pager.PageHelper;
import com.augurit.agcloud.framework.ui.result.ResultForm;
import com.augurit.agcloud.framework.util.JsonUtils;
import com.augurit.agcom.common.LoginHelpClient;
import com.common.util.Common;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import eu.bitwalker.useragentutils.UserAgent;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/agsupport/conflictCheck")
public class ConflictCheckController {

    private static Logger logger = LoggerFactory.getLogger(ConflictCheckController.class);

    @Autowired
    private IConflictCheck iConflictCheck;


    @RequestMapping("/index.html")
    public ModelAndView demo(HttpServletRequest request, Model model) throws Exception {
        return new ModelAndView("agsupport/map/conflictCheck/conflictCheck");
    }

    @RequestMapping("/getConflictCheckLayers")
    public String getConflictCheckLayers() {
        ResultForm result = new ResultForm(true);
        try {
            List<AeaMapConflictCheck> conflictCheckLayers = iConflictCheck.getConflictCheckLayers();
            JSONArray array = JSONArray.fromObject(conflictCheckLayers);
            result.setMessage(array.toString());

        }catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return JsonUtils.toJson(result);
    }

    @RequestMapping("/findConflictCheckLayerList")
    public EasyuiPageInfo findList(AeaMapConflictCheck aeaMapConflictCheck, Page page) throws Exception{
        PageInfo<AeaMapConflictCheck> pageInfo = iConflictCheck.findConflictCheckLayerList(aeaMapConflictCheck,page);
        return PageHelper.toEasyuiPageInfo(pageInfo);
    }

    @RequestMapping("/saveConflictCheckLayer")
    public String saveConflictCheckLayer(AeaMapConflictCheck aeaMapConflictCheck,HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        try {
            if(StringUtils.isEmpty(aeaMapConflictCheck.getId())) {
                aeaMapConflictCheck.setId(UUID.randomUUID().toString());
                iConflictCheck.saveConflictCheckLayer(aeaMapConflictCheck);
                jsonObject = this.getLogInfo(request, "保存冲突检测图层信息--" + aeaMapConflictCheck.getLayerName());
            }else {
                iConflictCheck.updateConflictCheckLayer(aeaMapConflictCheck);
                jsonObject = this.getLogInfo(request, "修改冲突检测图层信息--" + aeaMapConflictCheck.getLayerName());
            }
            jsonObject.put("operResult", "成功");
            logger.info(jsonObject.toString());
            return JsonUtils.toJson(new ResultForm(true));
        }catch (Exception e) {
            jsonObject.put("operResult", "失败");
            if (e.getMessage() != null) {
                jsonObject.put("exceptionMessage", e.getMessage().substring(0, e.getMessage().length() > 500 ? 500 : e.getMessage().length()));
            }
            logger.error(jsonObject.toString());
            e.printStackTrace();
        }
        return JsonUtils.toJson(new ResultForm(false, "保存失败！"));
    }

    @RequestMapping("/deleteConflictCheckLayer")
    public String deleteConflictCheckLayer(String paramIds, HttpServletRequest request) {
        JSONObject jsonObject = this.getLogInfo(request, "批量删除冲突检测图层配置");
        String ids[] = null;
        if (StringUtils.isNotEmpty(paramIds)) {
            ids = paramIds.split(",");
        }
        try {
            if (ids != null && ids.length > 0) {
                iConflictCheck.deleteBatchConflictCheckLayer(ids);
            }
            jsonObject.put("operResult", "成功");
            logger.info(jsonObject.toString());
            return JsonUtils.toJson(new ResultForm(true));
        }catch (Exception e) {
            jsonObject.put("operResult", "失败");
            if (e.getMessage() != null) {
                jsonObject.put("exceptionMessage", e.getMessage().substring(0, e.getMessage().length() > 500 ? 500 : e.getMessage().length()));
            }
            logger.error(jsonObject.toString());
            e.printStackTrace();
        }
        return JsonUtils.toJson(new ResultForm(false));
    }

    @RequestMapping("/saveConflictCheckLandType")
    public String saveConflictCheckLandType(AeaMapConflictCheckLandType aeaMapConflictCheckLandType, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        String layerType = aeaMapConflictCheckLandType.getLayerType();
        if(StringUtils.isEmpty(layerType)) {
            jsonObject.put("operResult", "失败");
            return JsonUtils.toJson(new ResultForm(false));
        }
        try {
            if(StringUtils.isEmpty(aeaMapConflictCheckLandType.getId())) {
                aeaMapConflictCheckLandType.setId(UUID.randomUUID().toString());
                iConflictCheck.saveConflictCheckLandType(aeaMapConflictCheckLandType);
                jsonObject = this.getLogInfo(request, "保存冲突检测图层用地类型配置--" + aeaMapConflictCheckLandType.getLandName());
            }else {
                iConflictCheck.updateConflictCheckLandType(aeaMapConflictCheckLandType);
                jsonObject = this.getLogInfo(request, "更新冲突检测图层用地类型配置--" + aeaMapConflictCheckLandType.getLandName());
            }
            return JsonUtils.toJson(new ResultForm(true));
        }catch (Exception e) {
            jsonObject.put("operResult", "失败");
            if (e.getMessage() != null) {
                jsonObject.put("exceptionMessage", e.getMessage().substring(0, e.getMessage().length() > 500 ? 500 : e.getMessage().length()));
            }
            logger.error(jsonObject.toString());
            e.printStackTrace();
        }
        return JsonUtils.toJson(new ResultForm(false));
    }

    @RequestMapping("/findConflictCheckLandType")
    public EasyuiPageInfo findConflictCheckLandType(AeaMapConflictCheckLandType aeaMapConflictCheckLandType, Page page, HttpServletRequest request) throws Exception {
        PageInfo<AeaMapConflictCheckLandType> pageInfo = iConflictCheck.findConflictCheckLandType(aeaMapConflictCheckLandType,page);
        return PageHelper.toEasyuiPageInfo(pageInfo);
    }

    @RequestMapping("/getConflictCheckLandType")
    public String getConflictCheckLandType(AeaMapConflictCheckLandType aeaMapConflictCheckLandType,HttpServletRequest request) throws Exception {
        ResultForm result = new ResultForm(true);
        try {
            List<AeaMapConflictCheckLandType> list = iConflictCheck.getConflictCheckLandType(aeaMapConflictCheckLandType);
            JSONArray array = JSONArray.fromObject(list);
            result.setMessage(array.toString());

        }catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return JsonUtils.toJson(result);
    }

    @RequestMapping("/updateBatchConflictCheckLandType")
    public String updateBatchConflictCheckLandType(String listStr,HttpServletRequest request) throws Exception {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArray = JSONArray.fromObject(listStr);
            List<AeaMapConflictCheckLandType> list = JSONArray.toList(jsonArray,AeaMapConflictCheckLandType.class);
            iConflictCheck.updateBatchConflictCheckLandType(list);
            jsonObject = this.getLogInfo(request, "更新冲突检测图层用地类型配置" );
            return JsonUtils.toJson(new ResultForm(true));
        }catch (Exception e){
            jsonObject.put("operResult", "失败");
            if (e.getMessage() != null) {
                jsonObject.put("exceptionMessage", e.getMessage().substring(0, e.getMessage().length() > 500 ? 500 : e.getMessage().length()));
            }
            logger.error(jsonObject.toString());
            e.printStackTrace();
        }
        return JsonUtils.toJson(new ResultForm(false));
    }

    @RequestMapping("/deleteBatchConflictCheckLandType")
    public String deleteBatchConflictCheckLandType(String paramIds,HttpServletRequest request) throws Exception {
        JSONObject jsonObject = this.getLogInfo(request, "批量删除冲突检测图层用地性质配置");
        String ids[] = null;
        if (StringUtils.isNotEmpty(paramIds)) {
            ids = paramIds.split(",");
        }
        try {
            if (ids != null && ids.length > 0) {
                iConflictCheck.deleteBatchConflictCheckLandType(ids);
            }
            jsonObject.put("operResult", "成功");
            logger.info(jsonObject.toString());
            return JsonUtils.toJson(new ResultForm(true));
        }catch (Exception e) {
            jsonObject.put("operResult", "失败");
            if (e.getMessage() != null) {
                jsonObject.put("exceptionMessage", e.getMessage().substring(0, e.getMessage().length() > 500 ? 500 : e.getMessage().length()));
            }
            logger.error(jsonObject.toString());
            e.printStackTrace();
        }
        return JsonUtils.toJson(new ResultForm(false));
    }

    @RequestMapping("/addBatchConflictCheckLandType")
    public String addBatchConflictCheckLandType(String listStr,HttpServletRequest request) throws Exception {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArray = JSONArray.fromObject(listStr);
            List<AeaMapConflictCheckLandType> list = JSONArray.toList(jsonArray,AeaMapConflictCheckLandType.class);
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setId(UUID.randomUUID().toString());
            }
            iConflictCheck.addBatchConflictCheckLandType(list);
            jsonObject = this.getLogInfo(request, "插入冲突检测图层用地类型配置" );
            return JsonUtils.toJson(new ResultForm(true));
        }catch (Exception e){
            jsonObject.put("operResult", "失败");
            if (e.getMessage() != null) {
                jsonObject.put("exceptionMessage", e.getMessage().substring(0, e.getMessage().length() > 500 ? 500 : e.getMessage().length()));
            }
            logger.error(jsonObject.toString());
            e.printStackTrace();
        }
        return JsonUtils.toJson(new ResultForm(false));
    }

    @RequestMapping("importExcel")
    public String impExcel(String layerType,String construct,HttpServletRequest request) {
        ResultForm resultForm = new ResultForm(true);
        ResultForm errorReultFrom = new ResultForm(false);
        BufferedInputStream in = null;
        BufferedOutputStream outputStream = null;
        String filePath = null;
        JSONObject jsonObject = new JSONObject();
        try {
            request.setCharacterEncoding("UTF-8");
            File tempDirPath = new File(request.getSession().getServletContext().getRealPath("/") + "\\uploads\\");
            if (!tempDirPath.exists()) {
                tempDirPath.mkdirs();
            }
            StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest) request;
            Iterator<String> it = req.getFileNames();
            while (it.hasNext()) {
                MultipartFile file = req.getFile(it.next());
                String fileNames = file.getOriginalFilename();
                filePath = tempDirPath + File.separator + fileNames;
                File itemFile = new File(filePath);
                if (itemFile.exists()) {
                    itemFile.delete();
                }
                in = new BufferedInputStream(file.getInputStream());
                outputStream = new BufferedOutputStream(new FileOutputStream(itemFile));
                Streams.copy(in, outputStream, true);
            }
            outputStream.flush();
            ImportExecl poi = new ImportExecl();
            List<List<String>> list = poi.read(filePath, 0);
            List<AeaMapConflictCheckLandType> landTypeList = new ArrayList<>();
            if (list != null) {
                if(list.size() == 0) {
                    errorReultFrom.setMessage("未读取到到文档数据！");
                    return JsonUtils.toJson(errorReultFrom);
                }
                List<String> titleRow = list.get(0);
                int landCodeColumnNum = -1,landNameColumnNum = -1;
                for (int i =0; i < titleRow.size(); i++) {
                    String ColumnName = titleRow.get(i).toString().trim();
                    if("用地性质名称".equals(ColumnName)) {
                        landNameColumnNum = i;
                    }else if("用地性质编码".equals(ColumnName)) {
                        landCodeColumnNum = i;
                    }
                }
                if(landCodeColumnNum == -1 || landNameColumnNum == -1) {
                    errorReultFrom.setMessage("文档数据格式不符合要求！");
                    return JsonUtils.toJson(errorReultFrom);
                }
                for (int i = 1; i < list.size(); i++) {
                    AeaMapConflictCheckLandType landType = new AeaMapConflictCheckLandType();
                    List<String> cellList = list.get(i);
                    String landName = cellList.get(landNameColumnNum).toString();
                    String landCode = cellList.get(landCodeColumnNum).toString();
                    if(StringUtils.isEmpty(landName)
                            && StringUtils.isEmpty(landCode)) { //排除其他列数据行数多余当前列行数的情况
                        continue;
                    }
                    landType.setLandCode(cellList.get(landCodeColumnNum).toString());
                    landType.setLandName(cellList.get(landNameColumnNum).toString());
                    landType.setLayerType(layerType);
                    landType.setConstruct(construct);
                    landType.setId(UUID.randomUUID().toString());
                    landTypeList.add(landType);
                }
            }else {
                errorReultFrom.setMessage("未读取到到文档数据！");
                return JsonUtils.toJson(errorReultFrom);
            }
            jsonObject = this.getLogInfo(request, "插入冲突检测图层用地类型配置" );
            iConflictCheck.addBatchConflictCheckLandType(landTypeList);
            return JsonUtils.toJson(resultForm);

        }catch (Exception e) {
            errorReultFrom.setMessage("读取数据出错！");
            jsonObject.put("operResult", "失败");
            if (e.getMessage() != null) {
                jsonObject.put("exceptionMessage", e.getMessage().substring(0, e.getMessage().length() > 500 ? 500 : e.getMessage().length()));
            }
            logger.error(jsonObject.toString());
            e.printStackTrace();
        }
        return JsonUtils.toJson(errorReultFrom);

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
     * 冲突检测报告打印
     */

    @RequestMapping("grantReport")
    public String conflictPOST(String time,
                               String landType,
                               String dataLayers,
                                String totalArea,
                               String image,
                               String tableStr,
                                String dataStr,
                              HttpServletRequest request,
                               HttpServletResponse response) throws Exception {

        response.reset();
        Map<String, Object> content = new HashMap<String, Object>();
        String webPath = request.getSession().getServletContext()
                .getRealPath("/");
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            if (!Common.isCheckNull(time)) {
                String timeStamp = Common.checkNull(time, "");
                Calendar c = Calendar.getInstance();
                c.setTime(format.parse(timeStamp));
                // "upload/控制线检测/2015/07/201570722123两规冲突分析报告.doc"

                String uploadDirPath = "uploads";

                String filePath = uploadDirPath
                        .concat("\\冲突检测\\")
                        .concat(String.valueOf(c.get(Calendar.YEAR)))
                        .concat(File.separator)
                        .concat(String.valueOf(c.get(Calendar.MONTH) + 1))
                        .concat(File.separator).concat(timeStamp)
                        .concat("两规冲突分析报告.doc");
                File f = new File(webPath.concat(filePath));
                if (!f.exists()) {
                    content.put("landType", Common.checkNull(landType, ""));
                    SimpleDateFormat format2 = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm");
                    content.put("time", format2.format(format.parse(timeStamp)));
                    content.put("dataLayers", Common.checkNull(dataLayers, ""));
                    content.put("totalArea", Common.checkNull(totalArea, ""));
                    content.put("image", Common.checkNull(image, ""));
                    List<Map> table = new ArrayList<Map>();
                    JSONArray tableArr = JSONArray.fromObject(Common.checkNull(
                            tableStr, "[]"));
                    content.put("table", table);
                    Map<String, String> map;
                    for (int i = 0; i < tableArr.size(); i++) {
                        map = tableArr.getJSONObject(i);
                        table.add(map);
                    }
                    List<Map> dataList = new ArrayList<Map>();
                    JSONArray listArr = JSONArray.fromObject(Common.checkNull(
                            dataStr, "[]"));
                    content.put("dataList", dataList);
                    Map<String, Object> map2;
                    for (int i = 0; i < listArr.size(); i++) {
                        JSONObject object = listArr.getJSONObject(i);
                        map2 = new HashMap<String, Object>();
                        map2.put("classification",
                                object.getString("classification"));
                        List<Map> dataMap = new ArrayList<Map>();
                        JSONArray mapArr = object.getJSONArray("dataMap");
                        map2.put("dataMap", dataMap);
                        Map<String, String> map3;
                        for (int j = 0; j < mapArr.size(); j++) {
                            map3 = mapArr.getJSONObject(j);
                            dataMap.add(map3);
                        }
                        dataList.add(map2);
                    }
                    DocumentHandler handler = new DocumentHandler(webPath);
                    handler.createDoc(content, "两规冲突分析报告_template.flt",
                            filePath);
                }
                outputFile(response, webPath.concat(filePath));
            } else {
                return null;// "系统出错了，请联系系统管理员";
            }

        } catch (Exception e) {
            return null;// e.getMessage();
        }
        return null;
    }


    /**
     * 读取文件输出到浏览器
     *
     * @param outputFilePath
     *            文件路径
     * @param response
     * @throws IOException
     */
    private void outputFile(HttpServletResponse response, String outputFilePath)
            throws IOException {
        response.setContentType("application/msword");

        String saveName = new String(outputFilePath.substring(
                outputFilePath.lastIndexOf("\\") + 1).getBytes("GBK"),
                "ISO8859-1");
        response.setHeader("Content-disposition", "inline; filename=\""
                + saveName + "\";");

        BufferedOutputStream bout = new BufferedOutputStream(
                response.getOutputStream());

        InputStream fileInputStream = FileUtils.openInputStream(new File(
                outputFilePath));

        // 将 word 文件 输入到浏览器中
        IOUtils.copy(fileInputStream, bout);

        // 关闭流
        IOUtils.closeQuietly(bout);
        IOUtils.closeQuietly(fileInputStream);

    }


}
