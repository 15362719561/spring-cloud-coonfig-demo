package com.augurit.agsupport.map.division.mapper;

import com.augurit.agsupport.map.division.domain.AgSgDivisionUrl;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AgSgDivisionUrlMapper {

    public List<AgSgDivisionUrl> findAllList() throws Exception;

    public void save(AgSgDivisionUrl agSgDivisionUrl) throws Exception;

    public void update(AgSgDivisionUrl agSgDivisionUrl) throws Exception;

    public void deleteById(@Param("id") String id) throws Exception;

    public AgSgDivisionUrl findById(@Param("id") String id) throws Exception;

    public void deleteBatch(@Param("ids") String[] ids) throws Exception;

    public void truncateTable() throws Exception;

    public AgSgDivisionUrl getDivisionUrlByUrl(@Param("url") String url) throws Exception;
}
