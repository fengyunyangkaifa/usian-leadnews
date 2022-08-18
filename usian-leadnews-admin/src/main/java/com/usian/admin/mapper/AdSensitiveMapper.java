package com.usian.admin.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.usian.model.admin.pojos.AdSensitive;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AdSensitiveMapper extends BaseMapper<AdSensitive> {

    public List<String> findAllSensitive();
}
