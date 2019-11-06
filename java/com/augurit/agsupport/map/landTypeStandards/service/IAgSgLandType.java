package com.augurit.agsupport.map.landTypeStandards.service;


import com.augurit.agsupport.map.landTypeStandards.domain.AgSgLandType;

import java.util.List;

public interface IAgSgLandType {
    public List<AgSgLandType> findAllList(AgSgLandType agSgLandType) throws Exception;

    public void save(AgSgLandType agSgLandType) throws Exception;

    public void update(AgSgLandType agSgLandType) throws Exception;

    public void deleteById(String id) throws Exception;

    public List<AgSgLandType> getTargetLandTypes(String versionCode, String type, String codeSort,String isDefault) throws Exception;

    public AgSgLandType findById(String id) throws Exception;

    public void deleteBatch(String[] ids) throws Exception;

    public List<AgSgLandType> findByCode(String code, String id) throws Exception;

    public void updatePNodeByCode(String code, String type, String versionCode) throws Exception;

    public void toggleDefaultLandVersion(String versionCode,String type,String isDefault) throws Exception;

    public List<String> getAllTypes() throws Exception;

    public List<String> getVersionsByType(String type) throws Exception;
}
