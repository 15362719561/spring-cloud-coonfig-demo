package com.augurit.agsupport.map.complianceCheck.mapper;

import com.augurit.agsupport.map.complianceCheck.domain.AeaMapComplianceProject;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 合规性检查-图层与项目类型关联配置
 */
@Mapper
public interface AeaMapComplianceProjectMapper {
    /**
     * 根据项目类型编码查询检测图层
     *
     * @param itemCode
     * @return
     * @throws Exception
     */
    List getAllByItemCode(String itemCode) throws Exception;

    /**
     * 根据项目类型编码以及状态查询检测图层
     *
     * @param project
     * @return
     * @throws Exception
     */
    List getAllByItemCodeAndStatus(AeaMapComplianceProject project) throws Exception;

    /**
     * 保存
     *
     * @param project
     * @throws Exception
     */
    void save(AeaMapComplianceProject project) throws Exception;

    /**
     * 修改
     *
     * @param project
     * @throws Exception
     */
    void update(AeaMapComplianceProject project) throws Exception;
}