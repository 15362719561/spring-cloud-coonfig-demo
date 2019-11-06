package com.augurit.agsupport.map.division.service.impl;

import com.augurit.agsupport.map.division.domain.AgSgDivisionUrl;
import com.augurit.agsupport.map.division.mapper.AgSgDivisionUrlMapper;
import com.augurit.agsupport.map.division.service.IAgSgDivisionUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgSgDivisionUrlImpl implements IAgSgDivisionUrl {
    @Autowired
    private AgSgDivisionUrlMapper agSgDivisionUrlMapper;

    @Override
    public List<AgSgDivisionUrl> findAllList() throws Exception{
        return agSgDivisionUrlMapper.findAllList();
    }
    @Override
    public void save(AgSgDivisionUrl agSgDivisionUrl) throws Exception{
        agSgDivisionUrlMapper.save(agSgDivisionUrl);
    }
    @Override
    public void update(AgSgDivisionUrl agSgDivisionUrl) throws Exception{
        agSgDivisionUrlMapper.update(agSgDivisionUrl);
    }
    @Override
    public void deleteById(String id) throws Exception{
        agSgDivisionUrlMapper.deleteById(id);
    }
    @Override
    public AgSgDivisionUrl findById(String id) throws Exception{
        return agSgDivisionUrlMapper.findById(id);
    }
    @Override
    public void deleteBatch(String[] ids) throws Exception {
        agSgDivisionUrlMapper.deleteBatch(ids);
    }
    @Override
    public void truncateTable() throws Exception{
        agSgDivisionUrlMapper.truncateTable();
    }
    @Override
    public AgSgDivisionUrl getDivisionUrlByUrl(String url) throws Exception{
        return agSgDivisionUrlMapper.getDivisionUrlByUrl(url);
    }
}
