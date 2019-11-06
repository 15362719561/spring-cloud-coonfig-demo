package com.augurit.agsupport.map.siteFactor.service;

import com.augurit.agsupport.map.siteFactor.domain.AeaMapSiteFactor;

import java.util.List;

public interface IAeaMapSiteFactor {
    public void save(AeaMapSiteFactor aeaMapSiteFactor) throws Exception;

    public void delete(String[] ids) throws Exception;

    public void update(AeaMapSiteFactor aeaMapSiteFactor) throws Exception;

    public List<AeaMapSiteFactor> getAll() throws Exception;

    public void saveLayers(List<AeaMapSiteFactor> layers, String parentId) throws Exception;

    public void saveByLayerIds(String[] layerIds, String parentId) throws Exception;
}
