/**
 * 
 */
package com.augurit.agsupport.map.mapServiceInfo.arcgis;

/**
 * @author lianghuaxin
 * 2015.8.13
 */
public class ArcgisRestParam {
    public final static String GEOMETRYTYPE_esriGeometryEnvelope = "esriGeometryEnvelope";
    public final static String GEOMETRYTYPE_esriGeometryPoint = "esriGeometryPoint";
    public final static String GEOMETRYTYPE_esriGeometryMultipoint = "esriGeometryMultipoint";
    public final static String GEOMETRYTYPE_esriGeometryPolygon = "esriGeometryPolygon";
    public final static String GEOMETRYTYPE_esriGeometryPolyline = "esriGeometryPolyline";
    
    /**
     * 未定义
     */
    public final static String SPATIALREL_esriSpatialRelUndefined = "esriSpatialRelUndefined";
    /**
     * A与B空间关联。需要通过SpatialRelDescription属性对二个要素的空间关系进行定制，可以对二个要素的内部，外部，边界之间的相交的情况进行描述
     */
    public final static String SPATIALREL_esriSpatialRelRelation = "esriSpatialRelRelation";
    /**
     * 包涵关系。A包含B,所有要素类之间均具有该关系。要素A局部或全部包含在要素B中（包含、局部包含）。
     * 当查询的要素完全位于被查询的要素内部的话（即spatialRel的值是esriSpatialRelWithin），则返回被查询的要素；
     * 同时如果查询的要素完全被被查询的要素包括时（即spatialRel的值是esriSpatialRelcontains）则返回被查询的要素。
     */
    public final static String SPATIALREL_esriSpatialRelContains = "esriSpatialRelContains";
    /**
     * 包涵关系。A在B内，所有要素类之间均具有该关系。当查询的要素完全位于被查询的要素内部，则返回被查询的要素
     */
    public final static String SPATIALREL_esriSpatialRelWithin = "esriSpatialRelWithin";
    /**
     * 邻接关系。Ａ与B边界相接，除点与点之间的关系外，其它的要素之间都可以具有该关系。
     */
    public final static String SPATIALREL_esriSpatialRelTouches = "esriSpatialRelTouches";
    /**
     * 重叠关系。A与B相叠加，可应用在线与线，面与面之间，其它的不具有该关系。
     */
    public final static String SPATIALREL_esriSpatialRelOverlaps = "esriSpatialRelOverlaps";
    /**
     * 交叉关系。A与B相交，两条线相交于一点，一条线和一个面相交，即可应用在线与面，线与线等，不能用于面与面（面与面相交部分是面，不能二个要素中的最高维数低1），面与点，点与线（二个要素的维数差2）。
     */
    public final static String SPATIALREL_esriSpatialRelCrosses = "esriSpatialRelCrosses";
    /**
     * 相交。 A与B图形相交,要素A穿过要素B（线线、线面、面面相交）。相交关系是一个广义的关系，
     * 包括4种关系（Within或者Contains，Touches，Overlaps，Crosses）。
     */
    public final static String SPATIALREL_esriSpatialRelIntersects = "esriSpatialRelIntersects";
    /**
     * A的Envelope和B的Envelope相交
     */
    public final static String SPATIALREL_esriSpatialRelEnvelopeIntersects = "esriSpatialRelEnvelopeIntersects";
    /**
     * A与B索引相交
     */
    public final static String SPATIALREL_esriSpatialRelIndexIntersects = "esriSpatialRelIndexIntersects";
    
	/**
	 * 结果返回类型
	 */
	private String f;

	/**
	 * 查询条件
	 */
	private String where;
	/**
	 * 空间元素
	 */
	private String geometry;
	/**
	 * 返回字段
	 */
	private String outFields;
	/**
	 * 是否返回空间元素
	 */
	private String returnGeometry;
	/**
	 * 输入空间元素类型
	 */
	private String geometryType;
	/**
	 * 空间关系
	 */
	private String spatialRel;
	/**
	 * 排序字段
	 */
	private String orderByFields;
	/**
	 * 指定ids
	 */
	private String objectIds;
	/**
	 * 只返回ids
	 */
	private String returnIdsOnly;
	/**
	 * 只返回统计数量
	 */
	private String returnCountOnly;
	/**
	 * 分组字段
	 */
	private String groupByFieldsForStatistics;
	/**
	 * 分组字段
	 */
	private String outStatistics;
	

	public String getF() {
		return f;
	}


	public void setF(String f) {
		this.f = f;
	}


	public String getWhere() {
		return where;
	}


	public void setWhere(String where) {
		this.where = where;
	}


	public String getGeometry() {
		return geometry;
	}


	public void setGeometry(String geometry) {
		this.geometry = geometry;
	}


	public String getOutFields() {
		return outFields;
	}


	public void setOutFields(String outFields) {
		this.outFields = outFields;
	}


	public String getReturnGeometry() {
		return returnGeometry;
	}


	public void setReturnGeometry(String returnGeometry) {
		this.returnGeometry = returnGeometry;
	}


	public String getGeometryType() {
		return geometryType;
	}


	public void setGeometryType(String geometryType) {
		this.geometryType = geometryType;
	}


	public String getSpatialRel() {
		return spatialRel;
	}


	public void setSpatialRel(String spatialRel) {
		this.spatialRel = spatialRel;
	}


	public String getOrderByFields() {
		return orderByFields;
	}


	public void setOrderByFields(String orderByFields) {
		this.orderByFields = orderByFields;
	}


	public String getObjectIds() {
		return objectIds;
	}


	public void setObjectIds(String objectIds) {
		this.objectIds = objectIds;
	}


	public String getReturnIdsOnly() {
		return returnIdsOnly;
	}


	public void setReturnIdsOnly(String returnIdsOnly) {
		this.returnIdsOnly = returnIdsOnly;
	}


	public String getReturnCountOnly() {
		return returnCountOnly;
	}


	public void setReturnCountOnly(String returnCountOnly) {
		this.returnCountOnly = returnCountOnly;
	}


	public String getGroupByFieldsForStatistics() {
		return groupByFieldsForStatistics;
	}


	public void setGroupByFieldsForStatistics(String groupByFieldsForStatistics) {
		this.groupByFieldsForStatistics = groupByFieldsForStatistics;
	}


	public String getOutStatistics() {
		return outStatistics;
	}


	public void setOutStatistics(String outStatistics) {
		this.outStatistics = outStatistics;
	}
	/**
	 * 无参构造函数
	 */
	public ArcgisRestParam() {
		
	}
	/**
	 * @param returnCountOnly
	 */
	public ArcgisRestParam(String returnCountOnly) {
		super();
		this.returnCountOnly = returnCountOnly;
	}

	/**
	 * 是否只返回objectid
	 * @param f
	 * @param returnIdsOnly
	 */
	public ArcgisRestParam(String f, String returnIdsOnly) {
		super();
		this.f = f;
		this.returnIdsOnly = returnIdsOnly;
		if(Boolean.valueOf((this.returnIdsOnly))){
			this.returnCountOnly = "";
		}
	}


	/**
	 * 条件、图斑；返回所有字段;
	 * @param f
	 * @param where
	 * @param geometry
	 */
	public ArcgisRestParam(String f, String where, String geometry) {
		super();
		this.f = f;
		this.where = where;
		this.geometry = geometry;
		this.outFields = "*";
	}
	
	/**
	 * 条件、图斑、是否返回空间信息;返回所有字段
	 * @param f
	 * @param where
	 * @param geometry
	 */
	public ArcgisRestParam(String f, String where, String geometry, String returnGeometry) {
		super();
		this.f = f;
		this.where = where;
		this.geometry = geometry;
		this.returnGeometry = returnGeometry;
		this.outFields = "*";
	}
	
	/**
	 * 条件、图斑、是否返回空间信息、输出字段
	 * @param f
	 * @param where
	 * @param geometry
	 */
	public ArcgisRestParam(String f, String where, String geometry, String returnGeometry, String outFields) {
		super();
		this.f = f;
		this.where = where;
		this.geometry = geometry;
		this.outFields = outFields;
		this.returnGeometry = returnGeometry;
	}
	
	/**
	 * 条件、图斑、是否返回空间信息、输出字段
	 * @param f
	 * @param where
	 * @param geometry
	 */
	public ArcgisRestParam(String f, String where, String geometry, String returnGeometry, String outFields, String spatialRel) {
		super();
		this.f = f;
		this.where = where;
		this.geometry = geometry;
		this.outFields = outFields;
		this.returnGeometry = returnGeometry;
		this.spatialRel = spatialRel;
	}

	/**
	 * 条件、图斑、是否返回空间信息、输出字段
	 * @param f
	 * @param where
	 * @param geometry
	 */
	public ArcgisRestParam(String f, String where, String geometry, String returnGeometry, String outFields, String spatialRel, String geometryType) {
		super();
		this.f = f;
		this.where = where;
		this.geometry = geometry;
		this.outFields = outFields;
		this.returnGeometry = returnGeometry;
		this.geometryType = geometryType;
		this.spatialRel = spatialRel;
	}

	/**
	 * 有参数的构造函数（所有字段）
	 * @param f
	 * @param where
	 * @param geometry
	 * @param outFields
	 * @param returnGeometry
	 * @param geometryType
	 * @param spatialRel
	 * @param orderByFields
	 * @param objectIds
	 * @param returnIdsOnly
	 * @param returnCountOnly
	 * @param groupByFieldsForStatistics
	 * @param outStatistics
	 */
	public ArcgisRestParam(String f, String where, String geometry,
                           String outFields, String returnGeometry, String geometryType,
                           String spatialRel, String orderByFields, String objectIds,
                           String returnIdsOnly, String returnCountOnly,
                           String groupByFieldsForStatistics, String outStatistics) {
		super();
		this.f = f;
		this.where = where;
		this.geometry = geometry;
		this.outFields = outFields;
		this.returnGeometry = returnGeometry;
		this.geometryType = geometryType;
		this.spatialRel = spatialRel;
		this.orderByFields = orderByFields;
		this.objectIds = objectIds;
		this.returnIdsOnly = returnIdsOnly;
		this.returnCountOnly = returnCountOnly;
		this.groupByFieldsForStatistics = groupByFieldsForStatistics;
		this.outStatistics = outStatistics;
	}
}
