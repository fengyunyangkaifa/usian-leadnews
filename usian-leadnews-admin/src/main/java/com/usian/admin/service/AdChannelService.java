package com.usian.admin.service;


import com.usian.common.dtos.ResponseResult;
import com.usian.model.admin.dtos.ChannelDto;
import com.usian.model.admin.pojos.AdChannel;


public interface AdChannelService {
    /**
     * 根据名称分页查询频道列表
     * @param dto
     * @return
     */
    public ResponseResult findByNameAndPage(ChannelDto dto);

    /**
     * 新增
     * @param channel
     * @return
     */
    public ResponseResult save(AdChannel channel);

    /**
     * 修改
     * @param adChannel
     * @return
     */
    public ResponseResult update(AdChannel adChannel);
    /**
     * 删除
     * @param id
     * @return
     */
    public ResponseResult deleteById(Integer id);
}
