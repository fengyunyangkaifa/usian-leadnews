package com.usian.admin.controller.v1;

import com.usian.admin.service.AdChannelService;
import com.usian.api.admin.AdchannelControllerApi;

import com.usian.model.admin.dtos.ChannelDto;
import com.usian.model.admin.pojos.AdChannel;
import com.usian.model.admin.pojos.AdUser;
import com.usian.model.common.dtos.ResponseResult;
import com.usian.utils.threadlocal.AdminThreadLocalUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/channel")
@Api(value = "频道管理" ,tags = "channel", description = "频道管理API")
public class AdChannelController implements AdchannelControllerApi {
    @Autowired
    private AdChannelService adChannelService;

    @PostMapping("/list")
    @Override
    @ApiOperation(value = "查询频道信息",notes = "根据名称分页查询频道列表")
    @ApiImplicitParam(name = "dto",type ="ChannelDto",value = "查询名称")
    public ResponseResult findByNameAndPage(@RequestBody ChannelDto dto) {
//        AdUser user = AdminThreadLocalUtils.getUser();  单独的用户线程，获取用户信息
        return adChannelService.findByNameAndPage(dto);
    }
    @PostMapping("/save")
    @Override
    @ApiOperation(value = "添加频道信息",notes = "根据channel添加频道信息")
    @ApiImplicitParam(value = "添加的参数")
    public ResponseResult save(@RequestBody AdChannel channel) {
        return adChannelService.save(channel);
    }
    @PostMapping("/update")
    @Override
    @ApiOperation(value = "修改频道信息",notes = "根据channel修改频道信息")
    @ApiImplicitParam(value = "修改的参数")
    public ResponseResult update(@RequestBody AdChannel adChannel) {
        return adChannelService.update(adChannel);
    }
    @GetMapping("/del/{id}")
    @Override
    @ApiOperation(value = "删除频道信息",notes = "根据url中的id频道信息,restful 风格")
    @ApiImplicitParam(value = "删除的参数")
    public ResponseResult deleteById(@PathVariable("id") Integer id) {
        return adChannelService.deleteById(id);
    }
    @GetMapping("/delete")
    @Override
    public ResponseResult delete(Integer[] ids) {
        return adChannelService.delete(ids);
    }
}
