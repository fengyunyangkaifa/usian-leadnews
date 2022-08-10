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
    @Select("SELECT * FROM ap_user_identity WHERE id=#{id}")
    ApUserIdentity findBYUserId(@Param("id") Integer id);
    @Update("update ap_user_identity set ststua=#{status} where id=#{id}")
    void updateBystatus(@Param("status") Integer paramFail,@Param("id") Integer id);
}
