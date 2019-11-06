package com.augurit.agsupport.map.mapServiceInfo;

import com.augurit.agsupport.map.util.ArcgisServiceUtil;
import com.augurit.agsupport.map.util.CommonUtil;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.UUID;

@RestController
@RequestMapping("/agsupport/arcgis")
public class ArcgisServiceController {
    private static Logger logger = LoggerFactory.getLogger(ArcgisServiceController.class);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    @RequestMapping("/getFields")
    public String getFieldsByUrl(String url) {
        return ArcgisServiceUtil.getFieldsByUrl(url).toString();
    }

    @RequestMapping("/getGeometryDataByFile")
    public String getGeometryDataByFile(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        object.put("success", true);
        String fileStr = fileUpload(request);
        JSONObject fileMessage = JSONObject.fromObject(fileStr);
        if (fileMessage != null && fileMessage.getBoolean("success")) {
            JSONObject message = fileMessage.getJSONObject("message");
            String module = message.getString("module");
            String fileType = message.getString("fileType");
            String fileName = message.getString("fileName");
            String resultStr = "";
            if ("shp".equals(fileType.toLowerCase())) {
                resultStr = getShpData(module, fileName, fileType, request);
            } else {
                object.put("success", false);
                object.put("message", "只能解析shp文件，请重新选择文件！");
                return object.toString();
            }
            if (StringUtils.isNotEmpty(resultStr)) {
                object = JSONObject.fromObject(resultStr);
            } else {
                object.put("success", false);
                object.put("message", "解析文件出错，请联系管理员！");
            }
        } else {
            object = fileMessage;
        }
        return object.toString();
    }

    /**
     * Input方式文件上传
     *
     * @param request
     * @return
     */
    @RequestMapping("/fileUpload")
    public String fileUpload(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        object.put("success", true);
        String webPath = request.getSession().getServletContext()
                .getRealPath("/");
        String contentType = request.getContentType();
        String module = request.getParameter("module");
        String fileType = request.getParameter("fileType");
        fileType = fileType.toLowerCase();
        module = StringUtils.isEmpty(module) ? "common" : module;
        String newFileName = "";
        String uuidDir = UUID.randomUUID().toString();
        newFileName = uuidDir + "." + fileType;
        String newFilePath = webPath.concat(CommonUtil.DEFAULT_ATTACHMENT_FILEPATH).concat("/").concat(module).concat("/").concat(newFileName);
        if (contentType != null && (contentType.toLowerCase().startsWith("multipart/"))) {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile file = multipartRequest.getFile("file");
            if (file != null && file.getSize() > 0) {
                File newFile = new File(newFilePath);
                if (!newFile.getParentFile().exists()) {
                    newFile.getParentFile().mkdirs();
                }
                try {
                    file.transferTo(newFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    object.put("message", "上传文件出错!");
                }
                JSONObject result = new JSONObject();
                result.put("module", module);
                result.put("fileType", fileType);
                result.put("fileName", newFileName);
                object.put("message", result.toString());
            } else {
                object.put("message", "上传文件失败!");
            }
        } else {

        }
        return object.toString();
    }

    /**
 * 解析shp文件
 *
 * @param module
 * @param fileName
 * @param fileType
 * @param request
 * @return
 */
@RequestMapping("/getShpData")
public String getShpData(String module, String fileName, String fileType,
                         HttpServletRequest request) {
    JSONObject object = new JSONObject();
    object.put("success", true);
    String webPath = request.getSession().getServletContext()
            .getRealPath("/");
    String filePath = webPath.concat(CommonUtil.DEFAULT_ATTACHMENT_FILEPATH).concat("/").concat(module).concat("/").concat(fileName);
    StringBuffer builder = new StringBuffer();
    builder.append("[");
    int count = 0;
    try {
        ShpFiles sf = new ShpFiles(filePath);
        ShapefileReader r = new ShapefileReader(sf, false, false, new GeometryFactory());
        while (r.hasNext()) {
            Geometry shape = (Geometry) r.nextRecord().shape();
            for (int j = 0; j < shape.getNumGeometries(); j++) {
                count++;
                if (builder.length() > 2)
                    builder.append(",");
                builder.append("{");
                builder.append("\"label\":\" 图形" + count + "\",");
                builder.append("\"mark\":[],\"geometry\":{");
                builder.append("\"paths\":[[");

                Object[] objTemp = shape.getGeometryN(j).getCoordinates();
                //Object[] objTemp =  shape.getCoordinates();
                for (int i = 0; i < objTemp.length; i++) {
                    builder.append("[");
                    String str = objTemp[i].toString();
                    str = str.substring(1, str.lastIndexOf(","));
                    builder.append(str);
                    if (i == objTemp.length - 1) {
                        builder.append("]");
                    } else {
                        builder.append("]");
                        builder.append(",");
                    }
                }
                builder.append("]]");
                builder.append("}");
                builder.append("}");
            }
        }
        builder.append("]");
        r.close();
        object.put("geometryStrs", builder.toString());
    } catch (Exception e) {
        e.printStackTrace();
        object.put("success", false);
    }
    return object.toString();
}
    /**
     * 解析shp文件
     *
     * @param fileName
     * @param request
     * @return
     */
    @RequestMapping("/getShpDatanew")
    public String getShpDatanew(String module, String fileName, String fileType,
                             HttpServletRequest request) {
        JSONObject object = new JSONObject();
        object.put("success", true);
        String webPath = request.getSession().getServletContext()
                .getRealPath("/");
        String filePath = webPath.concat("uploads").concat("/").concat(fileName);
        StringBuffer builder = new StringBuffer();
        builder.append("[");
        int count = 0;
        try {
            ShpFiles sf = new ShpFiles(filePath);
            ShapefileReader r = new ShapefileReader(sf, false, false, new GeometryFactory());
            while (r.hasNext()) {
                    Geometry shape = (Geometry) r.nextRecord().shape();
                for (int j = 0; j < shape.getNumGeometries(); j++) {
                    count++;
                    if (builder.length() > 2)
                        builder.append(",");
                    builder.append("{");
                    builder.append("\"label\":\" 图形" + count + "\",");
                    if(shape.toString().toLowerCase().contains("polygon")){
                        builder.append("\"type\":\" polygon\",");
                    }else if(shape.toString().toLowerCase().contains("line")){
                        builder.append("\"type\":\" line\",");
                    }else if(shape.toString().toLowerCase().contains("point")){
                        builder.append("\"type\":\" point\",");
                    }
                    builder.append("\"mark\":[],\"geometry\":{");
                    builder.append("\"paths\":[[");

                    Object[] objTemp = shape.getGeometryN(j).getCoordinates();
                    //Object[] objTemp =  shape.getCoordinates();
                    for (int i = 0; i < objTemp.length; i++) {
                        builder.append("[");
                        String str = objTemp[i].toString();
                        str = str.substring(1, str.lastIndexOf(","));
                        builder.append(str);
                        if (i == objTemp.length - 1) {
                            builder.append("]");
                        } else {
                            builder.append("]");
                            builder.append(",");
                        }
                    }
                    builder.append("]]");
                    builder.append("}");
                    builder.append("}");
                }
            }
            builder.append("]");
            r.close();
            object.put("geometryStrs", builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            object.put("success", false);
        }
        return object.toString();
    }
}
