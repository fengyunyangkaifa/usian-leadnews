package com.usian.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.usian.model.user.pojos.ApUserIdentity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ApUserIdentityMapper extends BaseMapper<ApUserIdentity> {
    @Select("select * from ap_user_identity where id=#{id}")
    ApUserIdentity findBYUserId(@Param("id") Integer id);
    @Update("update ap_user_identity set status=#{status},reason=#{reason} where id=#{id}")
    void updateByStatus(@Param("status") short paramFail,@Param("id") Integer id,@Param("reason") String reason);
}
