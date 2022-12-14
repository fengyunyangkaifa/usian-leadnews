package com.usian.wemedia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usian.model.media.pojos.WmUser;
import com.usian.wemedia.mapper.WmUserMapper;
import com.usian.wemedia.service.WmUserService;
import org.springframework.stereotype.Service;

@Service
public class WmUserServiceImpl extends ServiceImpl<WmUserMapper, WmUser> implements WmUserService {
}
