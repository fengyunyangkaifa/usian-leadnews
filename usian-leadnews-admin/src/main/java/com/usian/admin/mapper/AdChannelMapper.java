package com.usian.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.usian.model.admin.pojos.AdChannel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AdChannelMapper extends BaseMapper<AdChannel> {
    @Select("select * from ad_channel")
    List<AdChannel> List();
}
