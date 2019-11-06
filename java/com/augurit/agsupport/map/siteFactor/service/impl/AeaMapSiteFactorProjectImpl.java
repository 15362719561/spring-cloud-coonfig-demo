package com.augurit.agsupport.map.siteFactor.service.impl;

import com.augurit.agsupport.map.siteFactor.domain.AeaMapSiteFactorProject;
import com.augurit.agsupport.map.siteFactor.mapper.AeaMapSiteFactorProjectMapper;
import com.augurit.agsupport.map.siteFactor.service.IAeaMapSiteFactorProject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AeaMapSiteFactorProjectImpl implements IAeaMapSiteFactorProject {
    @Autowired
    private AeaMapSiteFactorProjectMapper aeaMapSiteFactorProjectMapper;

    @Override
    public List<AeaMapSiteFactorProject> getAllByProjectCode(String projectCode) throws Exception {
        List<AeaMapSiteFactorProject> list = aeaMapSiteFactorProjectMapper.getAllByProjectCode(projectCode);
        List<AeaMapSiteFactorProject> result = new ArrayList<AeaMapSiteFactorProject>();
        for (AeaMapSiteFactorProject project : list) {
            if (StringUtils.isNotEmpty(project.getParentId()) && StringUtils.isNotEmpty(project.getUrl())) {//子节点
                result.add(project);
            } else if (StringUtils.isEmpty(project.getParentId())) {
                for (int i = 0; i < list.size(); i++) {
                    AeaMapSiteFactorProject project1 = list.get(i);
                    if (StringUtils.isNotEmpty(project1.getUrl()) && project1.getParentId().equals(project.getFactorId())) {
                        if (!result.contains(project)) {
                            result.add(project);
                        }
                        break;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void save(AeaMapSiteFactorProject project) throws Exception {
        if (project != null) {
            aeaMapSiteFactorProjectMapper.save(project);
        }
    }

    @Override
    public void update(AeaMapSiteFactorProject project) throws Exception {
        if (project != null) {
            aeaMapSiteFactorProjectMapper.update(project);
        }
    }
}
