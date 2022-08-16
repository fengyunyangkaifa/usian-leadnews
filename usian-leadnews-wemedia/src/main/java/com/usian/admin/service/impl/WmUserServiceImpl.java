package com.usian.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usian.model.media.pojos.WmUser;
import com.usian.admin.mapper.WmUserMapper;
import com.usian.admin.service.WmUserService;
import org.springframework.stereotype.Service;

@Service
public class WmUserServiceImpl extends ServiceImpl<WmUserMapper, WmUser> implements WmUserService {
}
