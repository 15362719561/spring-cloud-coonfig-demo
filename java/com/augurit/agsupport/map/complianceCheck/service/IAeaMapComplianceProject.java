package com.augurit.agsupport.map.complianceCheck.service;

import com.augurit.agsupport.map.complianceCheck.domain.AeaMapComplianceProject;

import java.util.List;

public interface IAeaMapComplianceProject {

    /**
     * 根据项目类型编码（字典表中取）
     *
     * @param itemCode
     * @return
     * @throws Exception
     */
    public List<AeaMapComplianceProject> getAllByItemCode(String itemCode) throws Exception;

    public List<AeaMapComplianceProject> getAllByItemCodeAndStatus(AeaMapComplianceProject project) throws Exception;

    public void save(AeaMapComplianceProject project) throws Exception;

    public void update(AeaMapComplianceProject project) throws Exception;
}
