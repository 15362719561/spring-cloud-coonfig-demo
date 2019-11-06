package com.augurit.agsupport.map.complianceCheck.service.impl;

import com.augurit.agsupport.map.complianceCheck.domain.AeaMapComplianceProject;
import com.augurit.agsupport.map.complianceCheck.mapper.AeaMapComplianceProjectMapper;
import com.augurit.agsupport.map.complianceCheck.service.IAeaMapComplianceProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AeaMapComplianceProjectImpl implements IAeaMapComplianceProject {
    @Autowired
    private AeaMapComplianceProjectMapper aeaMapComplianceProjectMapper;

    @Override
    public List<AeaMapComplianceProject> getAllByItemCode(String itemCode) throws Exception {
        return aeaMapComplianceProjectMapper.getAllByItemCode(itemCode);
    }

    @Override
    public List<AeaMapComplianceProject> getAllByItemCodeAndStatus(AeaMapComplianceProject project) throws Exception {
        return aeaMapComplianceProjectMapper.getAllByItemCodeAndStatus(project);
    }

    @Override
    public void save(AeaMapComplianceProject project) throws Exception {
        if (project == null) {
            return;
        }
        aeaMapComplianceProjectMapper.save(project);
    }

    @Override
    public void update(AeaMapComplianceProject project) throws Exception {
        if (project == null) {
            return;
        }
        aeaMapComplianceProjectMapper.update(project);
    }
}