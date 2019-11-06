package com.augurit.agsupport.map.conflictCheck.mapper;

import com.augurit.agsupport.map.conflictCheck.domain.AeaMapConflictCheck;
import com.augurit.agsupport.map.conflictCheck.domain.AeaMapConflictCheckLandType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 两规冲突检测配置
 */
@Mapper
public interface AeaMapConflictCheckMapper {

    /**
     * 获取冲突检测配置图层
     * @return
     * @throws Exception
     */
    public List<AeaMapConflictCheck> getConflictCheckLayers() throws Exception;

    /**
     * 获取冲突检测配置图层
     * @param aeaMapConflictCheck
     * @return
     * @throws Exception
     */
    public List<AeaMapConflictCheck> findConflictCheckLayerList(AeaMapConflictCheck aeaMapConflictCheck) throws Exception;


    /**
     * 保存冲突检测配置图层
     * @param aeaMapConflictCheck
     * @throws Exception
     */
    public void saveConflictCheckLayer(AeaMapConflictCheck aeaMapConflictCheck)throws Exception;

    /**
     * 更新冲突检测配置图层
     * @param aeaMapConflictCheck
     * @throws Exception
     */
    public void updateConflictCheckLayer(AeaMapConflictCheck aeaMapConflictCheck) throws  Exception;


    /**
     * 删除冲突检测指定配置图层
     * @param id
     * @throws Exception
     */
    public void deleteConflictCheckLayer(String id) throws  Exception;


    /**
     * 批量删除冲突检测指定配置图层
     * @param ids
     * @throws Exception
     */
    public void deleteBatchConflictCheckLayer(String[] ids) throws Exception;

    /**
     * 保存冲突检测图层用地类型配置
     * @param aeaMapConflictCheckLandType
     * @throws Exception
     */
    public void saveConflictCheckLandType(AeaMapConflictCheckLandType aeaMapConflictCheckLandType) throws Exception;

    /**
     * 获取指定冲突检测图层的用地类型配置
     * @param aeaMapConflictCheckLandType
     * @return
     * @throws Exception
     */
    public List<AeaMapConflictCheckLandType> findConflictCheckLandType(AeaMapConflictCheckLandType aeaMapConflictCheckLandType) throws Exception;

    /**
     * 批量删除指定冲突检测图层的用地类型配置
     * @param ids
     * @return
     * @throws Exception
     */
    public void deleteBatchConflictCheckLandType(String[] ids) throws Exception;

    /**
     * 更新用地类型配置
     * @param aeaMapConflictCheckLandType
     * @throws Exception
     */
    public void updateConflictCheckLandType(AeaMapConflictCheckLandType aeaMapConflictCheckLandType) throws Exception;

    /**
     * 批量更新用地类型配置
     * @param list
     * @throws Exception
     */
    public void updateBatchConflictCheckLandType(List<AeaMapConflictCheckLandType> list) throws Exception;

    /**
     * 批量插入用地类型配置
     * @param list
     * @throws Exception
     */
    public void addBatchConflictCheckLandType(List<AeaMapConflictCheckLandType> list) throws Exception;


}
