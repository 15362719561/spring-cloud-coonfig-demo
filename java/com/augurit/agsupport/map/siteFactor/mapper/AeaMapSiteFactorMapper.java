package com.augurit.agsupport.map.siteFactor.mapper;

import com.augurit.agsupport.map.siteFactor.domain.AeaMapSiteFactor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 因子配置
 */
@Mapper
public interface AeaMapSiteFactorMapper {
    /**
     * 获取因子树
     *
     * @return
     */
    List getAll();

    /**
     * 新增
     *
     * @param aeaMapSiteFactor
     */
    void save(AeaMapSiteFactor aeaMapSiteFactor);

    /**
     * 修改数据
     */
    void update(AeaMapSiteFactor aeaMapSiteFactor);

    /**
     * 批量删除
     *
     * @param ids
     */
    void deleteBatch(@Param("ids") String[] ids);

    void saveLayers(@Param("layers") List<AeaMapSiteFactor> layers, @Param("parentId") String parentId);

    /**
     * 通过图层表关联新增数据
     *
     * @param layerIds
     * @param parentId
     */
    void saveByLayerIds(@Param("layerIds") String[] layerIds, @Param("parentId") String parentId);

    void saveLeafletByLayerIds(@Param("layerIds") String[] layerIds, @Param("parentId") String parentId);
}
