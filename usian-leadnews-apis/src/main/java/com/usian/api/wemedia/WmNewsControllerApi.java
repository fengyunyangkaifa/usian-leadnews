package com.usian.api.wemedia;

import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.media.dtos.WmNewsPageReqDto;
import io.swagger.annotations.Api;

/**
 * 自媒体文章接口
 */

@Api(value = "自媒体文章管理", tags = "WmNews", description = "自媒体文章管理API")
public interface WmNewsControllerApi {
    /**
     * 分页带条件查询自媒体文章列表
     * @param wmNewsPageReqDto
     * @return
     */
    public ResponseResult findAll(WmNewsPageReqDto wmNewsPageReqDto);
}
