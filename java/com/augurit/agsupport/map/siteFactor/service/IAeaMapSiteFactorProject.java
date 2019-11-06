package com.augurit.agsupport.map.siteFactor.service;


import com.augurit.agsupport.map.siteFactor.domain.AeaMapSiteFactorProject;

import java.util.List;

public interface IAeaMapSiteFactorProject {

    public List<AeaMapSiteFactorProject> getAllByProjectCode(String projectCode) throws Exception;

    public void save(AeaMapSiteFactorProject project) throws Exception;

    public void update(AeaMapSiteFactorProject project) throws Exception;
}
