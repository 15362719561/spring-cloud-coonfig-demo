package com.augurit.agsupport.map.landTypeStandards.mapper;

import com.augurit.agsupport.map.landTypeStandards.domain.AgSgLandType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AgSgLandTypeMapper {

    public List<AgSgLandType> findAllList(AgSgLandType agSgLandType) throws Exception;

    public void save(AgSgLandType agSgLandType) throws Exception;

    public void update(AgSgLandType agSgLandType) throws Exception;

    public void deleteById(@Param("id") String id) throws Exception;

    public List<AgSgLandType> getTargetLandTypes(@Param("versionCode") String versionCode, @Param("type") String type, @Param("codeSort") String codeSort,@Param("isDefault") String isDefault) throws Exception;

    public AgSgLandType findById(@Param("id") String id) throws Exception;

    public void deleteBatch(@Param("ids") String[] ids) throws Exception;

    public List<AgSgLandType> findByCode(@Param("code") String code, @Param("id") String id) throws Exception;

    public void updatePNodeByCode(@Param("code") String code, @Param("type") String type, @Param("versionCode") String versionCode) throws Exception;

    public void toggleDefaultLandVersion(@Param("versionCode") String versionCode,@Param("type") String type,@Param("isDefault") String isDefault) throws Exception;

    public List<String> getAllTypes() throws Exception;

    public List<String> getVersionsByType(@Param("type") String type) throws Exception;
}
