package com.usian.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.usian.model.media.pojos.WmMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface WmMaterialMapper extends BaseMapper<WmMaterial> {
}
