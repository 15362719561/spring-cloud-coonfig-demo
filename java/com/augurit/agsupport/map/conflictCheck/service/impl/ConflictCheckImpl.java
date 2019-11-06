package com.augurit.agsupport.map.conflictCheck.service.impl;

import com.augurit.agsupport.map.conflictCheck.domain.AeaMapConflictCheck;
import com.augurit.agsupport.map.conflictCheck.domain.AeaMapConflictCheckLandType;
import com.augurit.agsupport.map.conflictCheck.mapper.AeaMapConflictCheckMapper;
import com.augurit.agsupport.map.conflictCheck.service.IConflictCheck;
import com.augurit.agcloud.framework.ui.pager.PageHelper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConflictCheckImpl implements IConflictCheck {

    @Autowired
    private AeaMapConflictCheckMapper aeaMapConflictCheckMapper;

    @Override
    public List<AeaMapConflictCheck> getConflictCheckLayers() throws Exception{
        return aeaMapConflictCheckMapper.getConflictCheckLayers();
    }

    @Override
    public PageInfo findConflictCheckLayerList(AeaMapConflictCheck aeaMapConflictCheck, Page page) throws Exception {
        PageHelper.startPage(page);
        List<AeaMapConflictCheck> list = aeaMapConflictCheckMapper.findConflictCheckLayerList(aeaMapConflictCheck);
        return new PageInfo<AeaMapConflictCheck>(list);
    }

    @Override
    public void saveConflictCheckLayer(AeaMapConflictCheck aeaMapConflictCheck) throws Exception {
        if(aeaMapConflictCheck == null) return;
        aeaMapConflictCheckMapper.saveConflictCheckLayer(aeaMapConflictCheck);
    }

    @Override
    public void updateConflictCheckLayer(AeaMapConflictCheck aeaMapConflictCheck) throws  Exception {
        if(aeaMapConflictCheck == null) return;
        aeaMapConflictCheckMapper.updateConflictCheckLayer(aeaMapConflictCheck);
    }

    @Override
    public void deleteConflictCheckLayer(String id) throws  Exception {
        aeaMapConflictCheckMapper.deleteConflictCheckLayer(id);
    }

    @Override
    public void deleteBatchConflictCheckLayer(String[] ids) throws Exception {
        aeaMapConflictCheckMapper.deleteBatchConflictCheckLayer(ids);
    }

    @Override
    public void saveConflictCheckLandType(AeaMapConflictCheckLandType aeaMapConflictCheckLandType) throws Exception {
        aeaMapConflictCheckMapper.saveConflictCheckLandType(aeaMapConflictCheckLandType);
    }

    @Override
    public PageInfo<AeaMapConflictCheckLandType> findConflictCheckLandType(AeaMapConflictCheckLandType aeaMapConflictCheckLandType,Page page) throws Exception {
        PageHelper.startPage(page);
        List<AeaMapConflictCheckLandType> list = aeaMapConflictCheckMapper.findConflictCheckLandType(aeaMapConflictCheckLandType);
        return new PageInfo<AeaMapConflictCheckLandType>(list);
    }

    @Override
    public List<AeaMapConflictCheckLandType> getConflictCheckLandType(AeaMapConflictCheckLandType aeaMapConflictCheckLandType) throws Exception {
        List<AeaMapConflictCheckLandType> list = aeaMapConflictCheckMapper.findConflictCheckLandType(aeaMapConflictCheckLandType);
        return list;
    }

    @Override
    public void deleteBatchConflictCheckLandType(String[] ids) throws Exception {
        aeaMapConflictCheckMapper.deleteBatchConflictCheckLandType(ids);
    }

    @Override
    public void updateConflictCheckLandType(AeaMapConflictCheckLandType aeaMapConflictCheckLandType) throws Exception {
        if(aeaMapConflictCheckLandType == null) return;
        aeaMapConflictCheckMapper.updateConflictCheckLandType(aeaMapConflictCheckLandType);
    }

    @Override
    public void updateBatchConflictCheckLandType(List<AeaMapConflictCheckLandType> list) throws Exception {
        aeaMapConflictCheckMapper.updateBatchConflictCheckLandType(list);
    }

    @Override
    public void addBatchConflictCheckLandType(List<AeaMapConflictCheckLandType> list) throws Exception {
        aeaMapConflictCheckMapper.addBatchConflictCheckLandType(list);
    }

}
