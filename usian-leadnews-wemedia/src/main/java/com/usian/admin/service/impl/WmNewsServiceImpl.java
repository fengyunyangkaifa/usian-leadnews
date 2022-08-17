package com.usian.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usian.admin.mapper.WmNewsMapper;
import com.usian.admin.service.WmNewsService;
import com.usian.model.common.dtos.PageResponseResult;
import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.common.enums.AppHttpCodeEnum;
import com.usian.model.media.dtos.WmNewsPageReqDto;
import com.usian.model.media.pojos.WmNews;
import com.usian.model.media.pojos.WmUser;
import com.usian.utils.threadlocal.WmThreadLocalUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Value("${fdfs.url}")
    private String fileServerUrl;

    @Override
    public ResponseResult findAll(WmNewsPageReqDto dto) {
        //1.参数检查
        if(dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //分页参数检查
        dto.checkParam();
        //2.分页条件查询
        IPage pageParam = new Page(dto.getPage(),dto.getSize());

        LambdaQueryWrapper<WmNews> lambdaQueryWrapper = new LambdaQueryWrapper();
        //状态精确查询
        if(dto.getStatus() != null){
            lambdaQueryWrapper.eq(WmNews::getStatus,dto.getStatus());
        }
        //频道精确查询
        if(null != dto.getChannelId()){
            lambdaQueryWrapper.eq(WmNews::getChannelId,dto.getChannelId());
        }
        //时间范围查询
        if(dto.getBeginPubDate()!=null && dto.getEndPubDate()!=null){
            lambdaQueryWrapper.between(WmNews::getPublishTime,dto.getBeginPubDate(),dto.getEndPubDate());
        }
        //关键字模糊查询
        if(null != dto.getKeyword()){
            lambdaQueryWrapper.like(WmNews::getTitle,dto.getKeyword());
        }
        //查询当前登录用户的信息
        WmUser user = WmThreadLocalUtils.getUser();
        if(user==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        lambdaQueryWrapper.eq(WmNews::getUserId,user.getId());
        //按照创建日期倒序
        lambdaQueryWrapper.orderByDesc(WmNews::getCreatedTime);
        IPage pageResult = page(pageParam, lambdaQueryWrapper);
        //3.结果封装返回
        PageResponseResult responseResult = new PageResponseResult(dto.getPage(),dto.getSize(),(int)pageResult.getTotal());
        responseResult.setData(pageResult.getRecords());
        responseResult.setHost(fileServerUrl);
        return responseResult;
    }
}
