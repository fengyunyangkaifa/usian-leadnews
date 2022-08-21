package com.usian.tools.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.usian.model.article.pojos.ApAuthor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import java.util.List;



@Mapper
@Repository
public interface ApAuthorMapper extends BaseMapper<ApAuthor> {

    @Select("SELECT a.id,a.name,a.type,a.user_id userId,a.created_time createdTime,a.wm_user_id wmUserId FROM ap_author a LIMIT #{page},#{size}")
    List<ApAuthor> findPage(@Param("page") Integer page,@Param("size") Integer size);
}
