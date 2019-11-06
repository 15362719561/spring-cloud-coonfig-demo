package com.augurit.agsupport.map.conflictCheck.service;

import com.augurit.agsupport.map.conflictCheck.domain.AeaMapConflictCheck;
import com.augurit.agsupport.map.conflictCheck.domain.AeaMapConflictCheckLandType;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface IConflictCheck {

    /**
     * 获取冲突检测配置图层
     * @return
     */
    public List<AeaMapConflictCheck> getConflictCheckLayers() throws Exception;

    /**
     * 获取冲突检测配置图层
     * @param aeaMapConflictCheck
     * @param page
     * @return
     * @throws Exception
     */
    public PageInfo<AeaMapConflictCheck> findConflictCheckLayerList(AeaMapConflictCheck aeaMapConflictCheck, Page page) throws Exception;

    /**
     * 新增冲突检测配置图层
     * @param aeaMapConflictCheck
     * @return
     * @throws Exception
     */
    public void saveConflictCheckLayer(AeaMapConflictCheck aeaMapConflictCheck) throws Exception;

    /**
     * 更新冲突检测配置图层
     * @param aeaMapConflictCheck
     * @throws Exception
     */
    public void updateConflictCheckLayer(AeaMapConflictCheck aeaMapConflictCheck) throws  Exception;

    /**
     * 删除冲突检测配置图层
     * @param id
     * @throws Exception
     */
    public void deleteConflictCheckLayer(String id) throws  Exception;

    /**
     * 批量删除冲突检测配置图层
     * @param ids
     * @throws Exception
     */
    public void deleteBatchConflictCheckLayer(String[] ids) throws Exception;

    /**
     * 保存冲突检测图层用地类型配置
     * @param aeaMapConflictCheckLandType
     * @param aeaMapConflictCheckLandType
     * @throws Exception
     */
    public void saveConflictCheckLandType(AeaMapConflictCheckLandType aeaMapConflictCheckLandType) throws Exception;

    /**
     * 获取指定图层的用地类型配置
     * @param
     * @param aeaMapConflictCheckLandType
     * @return
     * @throws Exception
     */
    public PageInfo<AeaMapConflictCheckLandType> findConflictCheckLandType(AeaMapConflictCheckLandType aeaMapConflictCheckLandType, Page page) throws Exception;

    /**
     * 获取指定图层的用地类型配置
     * @param aeaMapConflictCheckLandType
     * @return
     * @throws Exception
     */
    public List<AeaMapConflictCheckLandType> getConflictCheckLandType(AeaMapConflictCheckLandType aeaMapConflictCheckLandType) throws Exception;


    /**
     * 批量删除指定图层的用地类型配置
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
