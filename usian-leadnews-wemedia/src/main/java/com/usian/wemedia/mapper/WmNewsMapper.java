package com.usian.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.usian.model.admin.dtos.NewsAuthDto;
import com.usian.model.media.pojos.WmNews;
import com.usian.model.media.vo.WmNewsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface WmNewsMapper extends BaseMapper<WmNews> {

    List<WmNewsVo> findListAndPage(@Param("dto") NewsAuthDto dto);
    int findListCount(@Param("dto") NewsAuthDto dto);
}
