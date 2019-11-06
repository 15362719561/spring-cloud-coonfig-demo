package com.augurit.agsupport.map.landTypeStandards.service.impl;

import com.augurit.agsupport.map.landTypeStandards.domain.AgSgLandType;
import com.augurit.agsupport.map.landTypeStandards.mapper.AgSgLandTypeMapper;
import com.augurit.agsupport.map.landTypeStandards.service.IAgSgLandType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgSgLandTypeImpl implements IAgSgLandType {
    @Autowired
    private AgSgLandTypeMapper agSgLandTypeMapper;

    @Override
    public List<AgSgLandType> findAllList(AgSgLandType agSgLandType) throws Exception {
        return agSgLandTypeMapper.findAllList(agSgLandType);
    }
    @Override
    public void save(AgSgLandType agSgLandType) throws Exception {
        agSgLandTypeMapper.save(agSgLandType);
    }
    @Override
    public void update(AgSgLandType agSgLandType) throws Exception {
        agSgLandTypeMapper.update(agSgLandType);
    }
    @Override
    public void deleteById(String id) throws Exception {
        agSgLandTypeMapper.deleteById(id);
    }
    @Override
    public List<AgSgLandType> getTargetLandTypes(String versionCode, String type, String codeSort,String isDefault)throws Exception {
        return agSgLandTypeMapper.getTargetLandTypes(versionCode,type,codeSort,isDefault);
    }
    @Override
    public AgSgLandType findById(String id) throws Exception {
        return agSgLandTypeMapper.findById(id);
    }
    @Override
    public void deleteBatch(String[] ids) throws Exception {
        agSgLandTypeMapper.deleteBatch(ids);
    }
    @Override
    public List<AgSgLandType> findByCode(String code, String id) throws Exception {
        return agSgLandTypeMapper.findByCode(code,id);
    }

    @Override
    public void updatePNodeByCode(String code, String type, String versionCode) throws Exception {
        agSgLandTypeMapper.updatePNodeByCode(code,type,versionCode);
    }

    @Override
    public void toggleDefaultLandVersion(String versionCode,String type,String isDefault) throws Exception {
        agSgLandTypeMapper.toggleDefaultLandVersion(versionCode,type,isDefault);
    }
    @Override
    public List<String> getAllTypes() throws Exception {
       return agSgLandTypeMapper.getAllTypes();
    }
    @Override
    public List<String> getVersionsByType(String type) throws Exception {
        return agSgLandTypeMapper.getVersionsByType(type);
    }
}
