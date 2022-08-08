package com.usian.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usian.model.common.dtos.PageResponseResult;
import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.common.enums.AppHttpCodeEnum;
import com.usian.model.user.dtos.AuthDto;
import com.usian.model.user.pojos.ApUserRealname;
import com.usian.user.mapper.ApUserRealnameMapper;
import com.usian.user.service.ApUserRealnameService;
import org.springframework.stereotype.Service;

@Service
public class ApUserRealnameServiceImpl extends ServiceImpl<ApUserRealnameMapper, ApUserRealname> implements ApUserRealnameService {
    @Override
    public ResponseResult loadListByStatus(AuthDto dto) {
        //参数为空
        if (dto == null) {
            return  ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //检查参数
        dto.checkParam();
        QueryWrapper<ApUserRealname> queryWrapper = new QueryWrapper<ApUserRealname>();
        if(dto.getStatus()!=null){
            queryWrapper.lambda().eq(ApUserRealname::getStatus,dto.getStatus());
        }
        Page pageParam = new Page(dto.getPage(),dto.getSize());
        IPage page = page(pageParam, queryWrapper);
        ResponseResult responseResult = new PageResponseResult(dto.getPage(),dto.getSize(),(int)page.getTotal());
        responseResult.setCode(0);
        responseResult.setData(page.getRecords());
        return responseResult;
    }
}
