package com.usian.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.usian.model.admin.pojos.AdUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AdUserMapper extends BaseMapper<AdUser> {
}
