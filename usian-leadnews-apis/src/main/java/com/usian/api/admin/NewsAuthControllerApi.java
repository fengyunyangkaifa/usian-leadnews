package com.usian.api.admin;

import com.usian.model.admin.dtos.NewsAuthDto;
import com.usian.model.common.dtos.ResponseResult;
import io.swagger.annotations.Api;

@Api(value = "文章审核管理", tags = "AdUserDto", description = "文章审核管理API")
public interface NewsAuthControllerApi {

    /**
     * 查询自媒体文章列表
     * @param dto
     * @return
     */
    public ResponseResult findNews(NewsAuthDto dto);

    /**
     * 查询文章详情
     * @param id
     * @return
     */
    public ResponseResult findOne(Integer id);

    /**
     * 文章审核成功
     * @param dto
     * @return
     */
    public ResponseResult authPass(NewsAuthDto dto);

    /**
     * 文章审核失败
     * @param dto
     * @return
     */
    public ResponseResult authFail(NewsAuthDto dto);
}
