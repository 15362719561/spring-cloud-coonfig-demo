package com.augurit.agsupport.map.complianceCheck.mapper;

import com.augurit.agsupport.map.complianceCheck.domain.AeaMapComplianceCheck;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 合规性检查-图层配置
 */
@Mapper
public interface AeaMapComplianceCheckMapper {
    /**
     * 根据条件查询
     *
     * @param aeaMapComplianceCheck
     * @return
     * @throws Exception
     */
    public List<AeaMapComplianceCheck> findList(AeaMapComplianceCheck aeaMapComplianceCheck) throws Exception;

    /**
     * 保存
     *
     * @param aeaMapComplianceCheck
     * @throws Exception
     */
    public void save(AeaMapComplianceCheck aeaMapComplianceCheck) throws Exception;

    /**
     * 根据ID删除
     *
     * @param id
     * @throws Exception
     */
    public void delete(@Param("id") String id) throws Exception;

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     * @throws Exception
     */
    public AeaMapComplianceCheck findById(@Param("id") String id) throws Exception;

    /**
     * 修改
     *
     * @param aeaMapComplianceCheck
     * @throws Exception
     */
    public void update(AeaMapComplianceCheck aeaMapComplianceCheck) throws Exception;
    /**
     * 批量删除
     *
     * @param ids
     * @throws Exception
     */
    public void deleteBatch(@Param("ids") String[] ids) throws Exception;

    public void saveLayers(@Param("layers") List<AeaMapComplianceCheck> layers) throws Exception;

    public void saveByLayerIds(@Param("layerIds") String[] layerIds) throws Exception;

    public void saveLeafletByLayerIds(@Param("layerIds") String[] layerIds) throws Exception;
}
