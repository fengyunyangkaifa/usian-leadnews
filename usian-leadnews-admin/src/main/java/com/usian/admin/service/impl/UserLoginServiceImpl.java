package com.usian.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.usian.admin.mapper.AdUserMapper;
import com.usian.admin.service.UserLoginService;
import com.usian.common.exception.CatchCustomException;
import com.usian.model.admin.dtos.AdUserDto;
import com.usian.model.admin.pojos.AdUser;
import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.common.enums.AppHttpCodeEnum;
import com.usian.utils.common.AppJwtUtil;
import com.usian.utils.common.BCrypt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public class UserLoginServiceImpl extends ServiceImpl<AdUserMapper, AdUser> implements UserLoginService {

    @Override
    public ResponseResult login(AdUserDto dto) {
        //1.参数校验
        if (StringUtils.isEmpty(dto.getName()) || StringUtils.isEmpty(dto.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE, "用户名或密码不能为空");
//            CatchCustomException.catchs(500,"用户名或密码不能为空");
        }
        Wrapper wrapper = new QueryWrapper<AdUser>();
        ((QueryWrapper) wrapper).eq("name", dto.getName());
        List<AdUser> list = list(wrapper);
        if (list != null && list.size() == 1) {
            AdUser adUser = list.get(0);
//            String pswd = DigestUtils.md5DigestAsHex((dto.getPassword() + adUser.getSalt()).getBytes());   md5
//            if (adUser.getPassword().equals(pswd)) {
            if (adUser.getStatus()!=1){
//                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE, "此状态不能登录");
                CatchCustomException.catchs(700,"此状态不能登录");
//                throw new RuntimeException("此状态不能登录");
            }
            if (BCrypt.checkpw(dto.getPassword(),adUser.getPassword())){   //  BCrypt 加密
                Map<String, Object> map = Maps.newHashMap();
                adUser.setPassword("");
                adUser.setSalt("");
                map.put("token", AppJwtUtil.getToken(adUser.getId().longValue()));
                map.put("user", adUser);
                return ResponseResult.okResult(map);
            } else {
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
        } else {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "用户不存在");
        }
    }
}
