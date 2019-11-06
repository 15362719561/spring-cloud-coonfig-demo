package com.augurit.agsupport.map.complianceCheck.service.impl;

import com.augurit.agcloud.framework.ui.pager.PageHelper;
import com.augurit.agsupport.map.complianceCheck.domain.AeaMapComplianceCheck;
import com.augurit.agsupport.map.complianceCheck.mapper.AeaMapComplianceCheckMapper;
import com.augurit.agsupport.map.complianceCheck.service.IAeaMapComplianceCheck;
import com.augurit.agsupport.map.util.CommonUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AeaMapCompianceCheckImpl implements IAeaMapComplianceCheck {
    @Autowired
    private AeaMapComplianceCheckMapper aeaMapComplianceCheckMapper;

    @Override
    public PageInfo<AeaMapComplianceCheck> findAeaMapComplianceCheckList(AeaMapComplianceCheck aeaMapComplianceCheck, Page page) throws Exception {
        PageHelper.startPage(page);
        List<AeaMapComplianceCheck> list = aeaMapComplianceCheckMapper.findList(aeaMapComplianceCheck);
        return new PageInfo<AeaMapComplianceCheck>(list);
    }

    @Override
    public AeaMapComplianceCheck findAeaMapComplianceCheckById(String id) throws Exception {
        return aeaMapComplianceCheckMapper.findById(id);
    }

    @Override
    public void saveAeaMapComplianceCheck(AeaMapComplianceCheck aeaMapComplianceCheck) throws Exception {
        if (aeaMapComplianceCheck == null)
            return;
        aeaMapComplianceCheckMapper.save(aeaMapComplianceCheck);
    }

    @Override
    public void updatAeaMapComplianceCheck(AeaMapComplianceCheck aeaMapComplianceCheck) throws Exception {
        if (aeaMapComplianceCheck == null)
            return;
        aeaMapComplianceCheckMapper.update(aeaMapComplianceCheck);
    }

    @Override
    public void deleteAeaMapComplianceCheck(String id) throws Exception {
        aeaMapComplianceCheckMapper.delete(id);
    }

    @Override
    public void deleteBatch(String[] ids) throws Exception {
        aeaMapComplianceCheckMapper.deleteBatch(ids);
    }

    @Override
    public List<AeaMapComplianceCheck> getAll(AeaMapComplianceCheck aeaMapComplianceCheck) throws Exception {
        return aeaMapComplianceCheckMapper.findList(aeaMapComplianceCheck);
    }

    @Override
    public void saveLayers(List<AeaMapComplianceCheck> list) throws Exception {
        aeaMapComplianceCheckMapper.saveLayers(list);
    }

    @Override
    public void saveByLayerIds(String[] layerIds) throws Exception {
        String mapType = CommonUtil.getAgmpiType();
        if(CommonUtil.LEAF_LET.equals(mapType)){
            aeaMapComplianceCheckMapper.saveLeafletByLayerIds(layerIds);
        }else {
            aeaMapComplianceCheckMapper.saveByLayerIds(layerIds);
        }
    }
}
