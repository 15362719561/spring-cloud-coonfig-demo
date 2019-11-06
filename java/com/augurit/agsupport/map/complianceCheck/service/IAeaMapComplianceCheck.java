package com.augurit.agsupport.map.complianceCheck.service;

import com.augurit.agsupport.map.complianceCheck.domain.AeaMapComplianceCheck;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface IAeaMapComplianceCheck {
    /**
     * 分页查询
     *
     * @param agAttachment
     * @param page
     * @return
     */
    public PageInfo<AeaMapComplianceCheck> findAeaMapComplianceCheckList(AeaMapComplianceCheck agAttachment, Page page) throws Exception;

    /**
     * 按id查找
     *
     * @param id
     * @return
     * @throws Exception
     */
    public AeaMapComplianceCheck findAeaMapComplianceCheckById(String id) throws Exception;

    /**
     * 保存
     *
     * @param aeaMapComplianceCheck
     * @throws Exception
     */
    public void saveAeaMapComplianceCheck(AeaMapComplianceCheck aeaMapComplianceCheck) throws Exception;

    /**
     * 修改
     *
     * @param aeaMapComplianceCheck
     * @throws Exception
     */
    public void updatAeaMapComplianceCheck(AeaMapComplianceCheck aeaMapComplianceCheck) throws Exception;

    /**
     * 删除配置图层
     *
     * @param id
     * @throws Exception
     */
    public void deleteAeaMapComplianceCheck(String id) throws Exception;

    /**
     * 批量删除
     *
     * @param ids
     * @throws Exception
     */
    public void deleteBatch(String[] ids) throws Exception;

    public List<AeaMapComplianceCheck> getAll(AeaMapComplianceCheck aeaMapComplianceCheck) throws Exception;

    public void saveLayers(List<AeaMapComplianceCheck> list) throws Exception;

    public void saveByLayerIds(String[] layerIds) throws Exception;
}
