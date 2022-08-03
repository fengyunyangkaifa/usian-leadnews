package com.usian.api.admin;


import com.usian.common.dtos.ResponseResult;
import com.usian.model.admin.dtos.ChannelDto;
import com.usian.model.admin.pojos.AdChannel;
import io.swagger.annotations.Api;

@Api(value = "频道管理", tags = "channel", description = "频道管理API")
public interface AdchannelControllerApi {
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
