package com.augurit.agsupport.map.division.service;

import com.augurit.agsupport.map.division.domain.AgSgDivision;
import com.augurit.agsupport.map.division.domain.Division;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface IAgSgDivision {

    public PageInfo<AgSgDivision> findList(AgSgDivision agSgDivision, Page page) throws Exception;

    public List<AgSgDivision> findAllList() throws Exception;

    public void save(AgSgDivision agSgDivision) throws Exception;

    public void update(AgSgDivision agSgDivision) throws Exception;

    public void deleteById(String id) throws Exception;

    public AgSgDivision findById(String id) throws Exception;

    public void deleteBatch(String[] ids) throws Exception;

    public List<AgSgDivision> findByCode(String code, String id) throws Exception;

    public List<AgSgDivision> findByPCode(String pCode, String description) throws Exception;

    public void truncateTable() throws Exception;

    public AgSgDivision findByKeyword(String keyword) throws Exception;

    public boolean importArcGISRestLayer(Division division, String pCodeValue, Integer start,String layerType) throws Exception;

    public boolean importGeoserverLayer(Division division, String pCodeValue, Integer start,String layerType) throws Exception;


}
