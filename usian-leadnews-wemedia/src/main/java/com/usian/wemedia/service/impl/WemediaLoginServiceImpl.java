package com.usian.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.usian.common.exception.CatchCustomException;
import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.common.enums.AppHttpCodeEnum;
import com.usian.model.media.dtos.WmUserDto;
import com.usian.model.media.pojos.WmUser;
import com.usian.utils.common.AppJwtUtil;
import com.usian.utils.common.BCrypt;
import com.usian.wemedia.mapper.WmUserMapper;
import com.usian.wemedia.service.WemediaLoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WemediaLoginServiceImpl extends ServiceImpl<WmUserMapper, WmUser> implements WemediaLoginService {
    
    @Override
    public ResponseResult login(WmUserDto dto) {
        //1.参数校验
        if (StringUtils.isEmpty(dto.getName()) || StringUtils.isEmpty(dto.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE, "用户名或密码不能为空");
//            CatchCustomException.catchs(500,"用户名或密码不能为空");
        }
        Wrapper wrapper = new QueryWrapper<WmUser>();
        ((QueryWrapper) wrapper).eq("name", dto.getName());
        List<WmUser> list = list(wrapper);
        if (list != null && list.size() == 1) {
            WmUser wmUser = list.get(0);
//            String pswd = DigestUtils.md5DigestAsHex((dto.getPassword() + wmUser.getSalt()).getBytes());   md5
//            if (wmUser.getPassword().equals(pswd)) {
            if (wmUser.getStatus()!=1){
//                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE, "此状态不能登录");
                CatchCustomException.catchs(700,"此状态不能登录");
//                throw new RuntimeException("此状态不能登录");
            }
            if (BCrypt.checkpw(dto.getPassword(),wmUser.getPassword())){   //  BCrypt 加密
                Map<String, Object> map = Maps.newHashMap();
                wmUser.setPassword("");
                wmUser.setSalt("");
                map.put("token", AppJwtUtil.getToken(wmUser.getId().longValue()));
                map.put("user", wmUser);
                return ResponseResult.okResult(map);
            } else {
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
        } else {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "用户不存在");
        }
    }
}
