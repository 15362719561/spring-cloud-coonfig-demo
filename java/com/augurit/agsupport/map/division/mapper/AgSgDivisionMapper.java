package com.augurit.agsupport.map.division.mapper;

import com.augurit.agsupport.map.division.domain.AgSgDivision;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AgSgDivisionMapper {

    public List<AgSgDivision> findList(AgSgDivision agSgDivision) throws Exception;

    public List<AgSgDivision> findAllList() throws Exception;

    public void save(AgSgDivision agSgDivision) throws Exception;

    public void update(AgSgDivision agSgDivision) throws Exception;

    public void deleteById(@Param("id") String id) throws Exception;

    public AgSgDivision findById(@Param("id") String id) throws Exception;

    public void deleteBatch(@Param("ids") String[] ids) throws Exception;

    public List<AgSgDivision> findByCode(@Param("code") String code, @Param("id") String id) throws Exception;

    public List<AgSgDivision> findByPCode(@Param("pcode") String pCode, @Param("description") String description) throws Exception;

    public void truncateTable() throws Exception;

    public AgSgDivision findByKeyword(@Param("keyword") String keyword) throws Exception;

}
