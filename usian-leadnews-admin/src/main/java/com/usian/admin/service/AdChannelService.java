package com.usian.admin.service;



import com.usian.model.admin.dtos.ChannelDto;
import com.usian.model.admin.pojos.AdChannel;
import com.usian.model.common.dtos.ResponseResult;


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

    /**
     * 批量删除
     * @param ids
     * @return
     */
    public ResponseResult delete(Integer[] ids);
}
