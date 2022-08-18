package com.usian.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.usian.model.media.pojos.WmNewsMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface WmNewsMaterialMapper extends BaseMapper<WmNewsMaterial> {
    //  添加文章
    void saveRelationsByContent(@Param("materials") List<String> materials, @Param("newsId") Integer newId, @Param("type") int type);
}
