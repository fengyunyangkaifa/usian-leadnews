package com.usian.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.usian.model.user.pojos.ApUserRealname;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ApUserRealnameMapper extends BaseMapper<ApUserRealname> {
    @Update("update ap_user_realname set reason=#{reason},status=#{status} where id=#{id}")
    void updateStatus(@Param("id") Integer id,@Param("reason") String msg,@Param("status") Short status);
}
