package com.usian.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.usian.model.user.pojos.ApUserRealname;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ApUserRealnameMapper extends BaseMapper<ApUserRealname> {
}
