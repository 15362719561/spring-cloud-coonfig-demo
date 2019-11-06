package com.augurit.agsupport.map.siteFactor.mapper;

import com.augurit.agsupport.map.siteFactor.domain.AeaMapSiteFactorProject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目与因子关联配置
 */
@Mapper
public interface AeaMapSiteFactorProjectMapper {
    /**
     * 根据项目性质编码查询
     *
     * @param projectCode
     * @return
     * @throws Exception
     */
    List getAllByProjectCode(@Param("projectCode") String projectCode) throws Exception;

    /**
     * 保存
     *
     * @param project
     * @throws Exception
     */
    void save(AeaMapSiteFactorProject project) throws Exception;

    /**
     * 修改
     *
     * @param project
     * @throws Exception
     */
    void update(AeaMapSiteFactorProject project) throws Exception;
}
