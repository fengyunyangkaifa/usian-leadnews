package com.usian.admin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usian.admin.mapper.AdChannelMapper;
import com.usian.admin.service.AdChannelService;
import com.usian.model.admin.dtos.ChannelDto;
import com.usian.model.admin.pojos.AdChannel;
import com.usian.model.common.dtos.PageResponseResult;
import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.common.enums.AppHttpCodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;

@Service
public class AdChannelServiceImpl  implements AdChannelService {

    @Autowired
    private AdChannelMapper adChannelMapper;

    @Override
    public ResponseResult findByNameAndPage(ChannelDto dto) {
//        验证数据
        if (dto==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //分页参数检查
        dto.checkParam();
        //名称模糊分页查询
        Page page = new Page(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<AdChannel> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(dto.getName())){
            queryWrapper.like(AdChannel::getName,dto.getName());
        }
        if (dto.getStatus()!=null){
            queryWrapper.eq(AdChannel::getStatus,dto.getStatus());
        }
        IPage result = adChannelMapper.selectPage(page,queryWrapper);
        //结果封装   多肽调用  向上级传递
        ResponseResult responseResult = new PageResponseResult(dto.getPage(),dto.getSize(),(int)result.getTotal());
        responseResult.setData(result.getRecords());
        return responseResult;
    }

    @Override
    public ResponseResult save(AdChannel channel) {
        // 验证数据
        if (channel==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        channel.setCreatedTime(new Date());
        adChannelMapper.insert(channel);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult update(AdChannel adChannel) {
        // 验证数据
        if (adChannel==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        adChannelMapper.updateById(adChannel);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult deleteById(Integer id) {
        // 验证数据
        if (id==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        adChannelMapper.deleteById(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

//  批量删除
    @Override
    public ResponseResult delete(Integer[] ids) {
        // 验证数据
        if (ids==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        adChannelMapper.deleteBatchIds(Arrays.asList(ids));
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
