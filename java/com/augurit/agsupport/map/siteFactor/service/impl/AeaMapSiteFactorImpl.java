package com.augurit.agsupport.map.siteFactor.service.impl;

import com.augurit.agsupport.map.siteFactor.domain.AeaMapSiteFactor;
import com.augurit.agsupport.map.siteFactor.mapper.AeaMapSiteFactorMapper;
import com.augurit.agsupport.map.siteFactor.service.IAeaMapSiteFactor;
import com.augurit.agsupport.map.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AeaMapSiteFactorImpl implements IAeaMapSiteFactor {
    @Autowired
    private AeaMapSiteFactorMapper aeaMapSiteFactorMapper;

    @Override
    public void save(AeaMapSiteFactor aeaMapSiteFactor) throws Exception {
        if (aeaMapSiteFactor != null)
            aeaMapSiteFactorMapper.save(aeaMapSiteFactor);
    }

    @Override
    public void delete(String[] ids) throws Exception {
        aeaMapSiteFactorMapper.deleteBatch(ids);
    }

    @Override
    public void update(AeaMapSiteFactor aeaMapSiteFactor) throws Exception {
        if (aeaMapSiteFactor != null)
            aeaMapSiteFactorMapper.update(aeaMapSiteFactor);
    }

    @Override
    public List<AeaMapSiteFactor> getAll() throws Exception {
        return aeaMapSiteFactorMapper.getAll();
    }

    @Override
    public void saveLayers(List<AeaMapSiteFactor> layers, String parentId) throws Exception {
        aeaMapSiteFactorMapper.saveLayers(layers,parentId);
    }

    @Override
    public void saveByLayerIds(String[] layerIds, String parentId) throws Exception {
        String mapType = CommonUtil.getAgmpiType();
        if (CommonUtil.LEAF_LET.equals(mapType)) {
            aeaMapSiteFactorMapper.saveLeafletByLayerIds(layerIds, parentId);
        } else {
            aeaMapSiteFactorMapper.saveByLayerIds(layerIds, parentId);
        }
    }
}
