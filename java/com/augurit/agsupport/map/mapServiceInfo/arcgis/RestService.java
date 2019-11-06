/**
 * 
 */
package com.augurit.agsupport.map.mapServiceInfo.arcgis;

import com.augurit.util.ConfigUtil;
import com.common.util.Common;
import com.common.util.HttpRequester;
import com.common.util.HttpRespons;
import com.common.util.ReflectBeans;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lianghuaxin
 * 
 */
public class RestService {
	public static String wkt = "{\"wkt\":\"PROJCS[\"xm92_B\",GEOGCS[\"GCS_Beijing_1954\",DATUM[\"D_Beijing_1954\",SPHEROID[\"Krasovsky_1940\",6378245.0,298.3]],PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]],PROJECTION[\"Gauss_Kruger\"],PARAMETER[\"False_Easting\",500000.0],PARAMETER[\"False_Northing\",0.0],PARAMETER[\"Central_Meridian\",118.5],PARAMETER[\"Scale_Factor\",1.0],PARAMETER[\"Latitude_Of_Origin\",0.0],UNIT[\" Meter \",1.0]]\"}";
	/**
	 * 全部默认参数查询
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String callRestQuery(String url) throws Exception {
		ArcgisRestParam restParam = new ArcgisRestParam("json", "1=1", null);
		return callRestQuery(url, restParam);
	}

	/**
	 * 只通过条件去过滤
	 * 
	 * @param url
	 * @param where
	 * @return
	 * @throws Exception
	 */
	public static String callRestQuery(String url, String where)
			throws Exception {
		ArcgisRestParam restParam = new ArcgisRestParam("json", where, null);
		return callRestQuery(url, restParam);
	}

	/**
	 * 通过条件、空间信息去过滤
	 * 
	 * @param url
	 * @param where
	 * @param geometry
	 * @return
	 * @throws Exception
	 */
	public static String callRestQuery(String url, String where, String geometry)
			throws Exception {
		ArcgisRestParam restParam = new ArcgisRestParam("json", where, geometry);
		return callRestQuery(url, restParam);
	}

	/**
	 * 通过条件、空间信息去过滤，限定返回字段
	 * 
	 * @param url
	 * @param where
	 * @param geometry
	 * @param outFields
	 * @return
	 * @throws Exception
	 */
	public static String callRestQuery(String url, String where,
			String geometry, String outFields) throws Exception {
		ArcgisRestParam restParam = new ArcgisRestParam("json", where,
				geometry, "true", outFields);
		return callRestQuery(url, restParam);
	}

	/**
	 * 通过条件、空间信息去过滤，限定返回字段、是否返回geometry
	 * 
	 * @param url
	 * @param where
	 * @param geometry
	 * @param outFields
	 * @return
	 * @throws Exception
	 */
	public static String callRestQuery(String url, String where,
			String geometry, String outFields, String returnGeometry)
			throws Exception {
		ArcgisRestParam restParam = new ArcgisRestParam("json", where,
				geometry, returnGeometry, outFields);
		return callRestQuery(url, restParam);
	}

	/**
	 * rest查询
	 * 
	 * @param url
	 *            服务图层地址
	 * @param restParam
	 *            ArcGIS Rest查询服务所需的参数
	 * @return 1、处理得到结果，返回未经处理过的json数据
	 *         2、处理过程中出错，返回{success:false,message:XXXXXXX}
	 * @throws Exception
	 */
	public static String callRestQuery(String url, ArcgisRestParam restParam)
			throws IOException {
		String r = "";
		try {
			if (Common.isCheckNull(url)) {
				throw new Exception("服务图层地址为空。");
			}
			if (Common.isCheckNull(restParam))
				restParam = new ArcgisRestParam("json", "false");
			Map param = ReflectBeans.beanToMap(restParam);
			String json = callRequestPost(url, param);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			r = String.format("{success:false,message:%s}", e.getMessage());
		}
		return r;
	}

	/**
	 * 只查询统计记录的数量
	 * 
	 * @param url
	 *            服务图层地址
	 * @param restParam
	 *            ArcGIS Rest查询服务所需的参数
	 * @return 1、处理得到结果，返回未经处理过的json数据
	 *         2、程序处理过程中出错，返回{success:false,message:XXXXXXX}
	 * @throws Exception
	 */
	public static String callRestCountOnly(String url, ArcgisRestParam restParam) {
		String r = "";
		try {
			if (Common.isCheckNull(url)) {
				throw new Exception("服务图层地址为空。");
			}
			if (Common.isCheckNull(restParam))
				restParam = new ArcgisRestParam("json", "false");
			if (Common.isCheckNull(restParam.getReturnCountOnly())
					|| restParam.getReturnCountOnly().equals("false")) {
				restParam.setReturnCountOnly("true");
			}
			if (Common.isCheckNull(restParam.getWhere())) {
				restParam.setWhere("1=1");
			}
			return callRestQuery(url, restParam);
		} catch (Exception e) {
			e.printStackTrace();
			r = String.format("{success:false,message:%s}", e.getMessage());
		}
		return r;
	}

	/**
	 * 只查询统计记录的数量
	 * 
	 * @param url
	 *            服务图层地址
	 * @param where
	 *            条件
	 * @param geometry
	 *            空间信息
	 * @return 1、处理得到相应条件记录的个数 2、-1,就是程序查询出错了
	 * @throws IOException
	 */
	public static int callRestCountOnly(String url, String where,
			String geometry) throws IOException {
		ArcgisRestParam restParam = new ArcgisRestParam("json", where, geometry);
		if (Common.isCheckNull(restParam.getWhere())) {
			restParam.setWhere("1=1");
		}
		String json = callRestCountOnly(url, restParam);
		JSONObject r = JSONObject.fromObject(json);
		if (r.containsKey("count")) {
			return r.getInt("count");
		}
		return -1;
	}

	/**
	 * 只查询统计记录的数量
	 * 
	 * @param url
	 *            服务图层地址
	 * @param where
	 *            条件
	 * @param geometry
	 *            空间信息
	 * @return 1、处理得到相应条件记录的个数 2、-1,就是程序查询出错了
	 * @throws IOException
	 */
	public static int callRestCountOnly(String url, String where,
			String geometry, String geometryType) throws IOException {
		ArcgisRestParam restParam = new ArcgisRestParam("json", where, geometry);
		if (Common.isCheckNull(restParam.getWhere())) {
			restParam.setWhere("1=1");
		}
		if (Common.isCheckNull(restParam.getGeometryType())) {
			restParam.setGeometryType(geometryType);
		}
		String json = callRestCountOnly(url, restParam);
		JSONObject r = JSONObject.fromObject(json);
		if (r.containsKey("count")) {
			return r.getInt("count");
		}
		return -1;
	}

	/**
	 * 查询只返回各记录的id
	 * 
	 * @param url
	 *            服务图层地址
	 * @param restParam
	 *            ArcGIS Rest查询服务所需的参数
	 * @return 
	 *         1、处理得到结果，返回未经处理过的json数据:{"objectIdFieldName":"OBJECTID","objectIds"
	 *         :[387,388]} 2、处理过程中出错，返回{success:false,message:XXXXXXX}
	 * @throws Exception
	 */
	public static String callRestReturnIdsOnly(String url,
			ArcgisRestParam restParam) {
		String r = "";
		try {
			if (Common.isCheckNull(url)) {
				throw new Exception("服务图层地址为空。");
			}
			if (Common.isCheckNull(restParam))
				restParam = new ArcgisRestParam("json", "false");
			if (Common.isCheckNull(restParam.getReturnIdsOnly())
					|| restParam.getReturnIdsOnly().equals("false")) {
				restParam.setReturnCountOnly("false");
				restParam.setReturnIdsOnly("true");
			}
			return callRestQuery(url, restParam);
		} catch (Exception e) {
			e.printStackTrace();
			r = String.format("{success:false,message:%s}", e.getMessage());
		}
		return r;
	}

	/**
	 * 查询只返回各记录的id,处理失败则返回空数组，不关注处理失败的错误
	 * 
	 * @param url
	 *            服务图层地址
	 * @param where
	 *            条件
	 * @param geometry
	 *            空间信息
	 * @return 1、id的数组 2、空数组
	 * @throws IOException
	 */
	public static String callRestReturnIdsOnly(String url, String where,
			String geometry) throws IOException {
		ArcgisRestParam restParam = new ArcgisRestParam("json", where, geometry);
		String json = callRestReturnIdsOnly(url, restParam);
		JSONObject r = JSONObject.fromObject(json);
		if (r.containsKey("objectIds")) {
			return r.getJSONArray("objectIds").toString();
		}
		return "[]";
	}

	/**
	 * rest服务identify查询
	 * 
	 * @param url
	 * @param geometry
	 * @return
	 * @throws Exception
	 */
	public static String callRestIdentify(String url, String geometryType,
			String geometry, String Layers, String returnGeometry)
			throws Exception {
		if (Common.isCheckNull(url)) {
			throw new Exception("服务图层地址为空。");
		}
		Map<String, String> param = new HashMap<String, String>();
		param.put("f", "json");
		param.put("geometryType", geometryType);// 面状为esriGeometryPolygon
		if (!Common.isCheckNull(geometry)) {
			param.put("geometry", geometry);
		}
//		String wkt = "{\"wkt\":\"PROJCS[\"xm92_B\",GEOGCS[\"GCS_Beijing_1954\",DATUM[\"D_Beijing_1954\",SPHEROID[\"Krasovsky_1940\",6378245.0,298.3]],PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]],PROJECTION[\"Gauss_Kruger\"],PARAMETER[\"False_Easting\",500000.0],PARAMETER[\"False_Northing\",0.0],PARAMETER[\"Central_Meridian\",118.5],PARAMETER[\"Scale_Factor\",1.0],PARAMETER[\"Latitude_Of_Origin\",0.0],UNIT[\" Meter \",1.0]]\"}";
		param.put("sr", wkt);// 传入图层坐标系
		param.put("Layers", Layers);// 查询图层ID，格式 n,n1,n2.....
		param.put("tolerance", "3");// 容差
		param.put("mapExtent", "4626.11,-30913.98,118926.34,112490.46");// 广州范围
		param.put("imageDisplay", "256,256,96");// 图片参数
		param.put("returnGeometry", returnGeometry);
		param.put("outSR", wkt);// 结果坐标系
		HttpRequester request = new HttpRequester();
		url= ConfigUtil.getLanUrl(url,null);
		HttpRespons respons = request.sendPost(url, param);
		String json = respons.getContent();
		return json;
	}

	public static String callRestQueryByField(String url, String field,
			String where, String geometry, String returnGeometry,
			String geometryType) throws IOException {
		Map<String, String> param = new HashMap<String, String>();
		param.put("f", "json");
		param.put("where", where);
		param.put("outFields", field);
		param.put("geometry", geometry);
		param.put("geometryType", "esriGeometryPolygon");
		param.put("returnGeometry", returnGeometry);
		param.put("spatialRel", "esriSpatialRelIntersects");
		HttpRequester request = new HttpRequester();
		url= ConfigUtil.getLanUrl(url,null);
		HttpRespons respons = request.sendPost(url, param);
		String json = respons.getContent();
		return json;
	}

	public static String callRestQueryByField(String url, String field,
			String where, String returnGeometry, String geometryType)
			throws IOException {
		Map<String, String> param = new HashMap<String, String>();
		param.put("f", "json");
		param.put("where", where);
		param.put("outFields", field);
		param.put("geometryType", "esriGeometryPolygon");
		param.put("returnGeometry", returnGeometry);
		HttpRequester request = new HttpRequester();
		url= ConfigUtil.getLanUrl(url,null);
		HttpRespons respons = request.sendPost(url, param);
		String json = respons.getContent();
		return json;
	}
	/**
	 * 通过服务地址获取图层字段
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static String getFieldsByurl(String url) throws Exception {
		if (Common.isCheckNull(url)) {
			throw new Exception("获取图层字段出错了：服务图层地址为空。");
		}
		HttpRequester request = new HttpRequester();
		url= ConfigUtil.getLanUrl(url,null);
		HttpRespons respons = request.sendPost(url);
		String json = respons.getContent();
		JSONObject object = JSONObject.fromObject(json);
		if (!object.containsKey("fields")) {
			throw new Exception("获取图层字段出错了：给予的图层服务没有查到相关字段。");
		}
		return object.getJSONArray("fields").toString();
	}

	public static String getRestMapServerUrl(String wmsUrl, String topicName)
			throws Exception {
		String[] urlsplit = wmsUrl.split("/WMSServer")[0].split("services");
		String url = urlsplit[0] + "rest/services" + urlsplit[1] + "?f=pjson";
		String layerTableId = null;
		HttpRequester request = new HttpRequester();
		url= ConfigUtil.getLanUrl(url,null);
		HttpRespons respons = request.sendGet(url, null);
		String json = respons.getContent();
		JSONObject ojson = JSONObject.fromObject(json);
		JSONArray layers = ojson.getJSONArray("layers");
		for (int i = 0; i < layers.size(); i++) {
			JSONObject layer = layers.getJSONObject(i);
			Long id = layer.getLong("id");
			String name = layer.getString("name");
			if (topicName.equalsIgnoreCase(name)) {
				layerTableId = String.valueOf(id);
			}
		}
		if (layerTableId == null) {
			throw new Exception("layerTableId is null");
		}
		url = urlsplit[0] + "rest/services" + urlsplit[1] + "/" + layerTableId
				+ "/query";
		return url;
	}

	/**
	 * 发起一个post请求
	 * 
	 * @param url
	 * @param param
	 * @return
	 * @throws IOException
	 */
	public static String callRequestPost(String url, Map<String, String> param)
			throws IOException {
		HttpRequester request = new HttpRequester();
		url= ConfigUtil.getLanUrl(url,null);
		HttpRespons respons = request.sendPost(url, param);
		return respons.getContent();
	}

	/**
	 * 发起一个get请求
	 * 
	 * @param url
	 * @param param
	 * @return
	 * @throws IOException
	 */
	public static String callRequestGet(String url, Map<String, String> param)
			throws IOException {
		HttpRequester request = new HttpRequester();
		url= ConfigUtil.getLanUrl(url,null);
		HttpRespons respons = request.sendGet(url, param);
		return respons.getContent();
	}

	/**
	 * 获取OBJECTIDS
	 * 
	 * @param url
	 * @param where
	 * @param geometry
	 * @param geometryType
	 * @return
	 * @throws IOException
	 */
	public static String callRestReturnIdsOnly(String url, String where,
			String geometry, String geometryType) throws IOException {
		ArcgisRestParam restParam = new ArcgisRestParam("json", where, geometry);
		restParam.setGeometryType(geometryType);
		restParam.setReturnCountOnly("");
		restParam.setReturnIdsOnly("true");
		String json = callRestQuery(url, restParam);
		JSONObject r = JSONObject.fromObject(json);
		if (r.containsKey("objectIds")) {
			return r.getJSONArray("objectIds").toString();
		}
		return "[]";
	}

	/**
	 * 根据IDS查询
	 * 
	 * @param url
	 * @param ids
	 * @param field
	 * @param returnGeometry
	 * @return
	 * @throws IOException
	 */
	public static String callRestQueryByIds(String url, String ids,
			String field, String returnGeometry) throws IOException {
		ArcgisRestParam restParam = new ArcgisRestParam();
		restParam.setF("json");
		restParam.setOutFields(field);
		restParam.setObjectIds(ids);
		restParam.setReturnGeometry(returnGeometry);
		return callRestQuery(url, restParam);
	}

	/**
	 * 检查是否有带query,若无则追加
	 * 
	 * @param url
	 */
	public static String checkAndaddQuery(String url) {
		if (StringUtils.isNotEmpty(url)) {
			String query = "/query";
			String checkValue = url.substring(url.length() - query.length());
			if (!checkValue.equals(query)) {
				url = url.concat(query);
			}
		}
		return url;
	}
}
