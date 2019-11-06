package com.augurit.agsupport.map.division.service;

import com.augurit.agsupport.map.division.domain.AgSgDivisionUrl;

import java.util.List;

public interface IAgSgDivisionUrl {
    public List<AgSgDivisionUrl> findAllList() throws Exception;

    public void save(AgSgDivisionUrl agSgDivisionUrl) throws Exception;

    public void update(AgSgDivisionUrl agSgDivisionUrl) throws Exception;

    public void deleteById(String id) throws Exception;

    public AgSgDivisionUrl findById(String id) throws Exception;

    public void deleteBatch(String[] ids) throws Exception;

    public void truncateTable() throws Exception;

    public AgSgDivisionUrl getDivisionUrlByUrl(String url) throws Exception;
}
