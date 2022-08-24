package com.usian.api.wemedia;

import com.usian.model.admin.dtos.NewsAuthDto;
import com.usian.model.common.dtos.PageResponseResult;
import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.media.dtos.WmNewsDto;
import com.usian.model.media.dtos.WmNewsPageReqDto;
import com.usian.model.media.pojos.WmNews;
import com.usian.model.media.vo.WmNewsVo;
import io.swagger.annotations.Api;

import java.util.List;

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

    /**
     * 提交文章
     * @param wmNews
     * @return
     */
    ResponseResult submitNews(WmNewsDto wmNews);
    /**
     * 根据id获取文章信息
     * @return
     */
    ResponseResult findWmNewsById(Integer id);

    /**
     * 删除文章
     * @return
     */
    ResponseResult delNews(Integer id);

    /**
     * 上下架
     * @param dto
     * @return
     */
    ResponseResult downOrUp(WmNewsDto dto);

    /**
     * 根据id查询文章
     * @param id
     * @return
     */
    WmNews findById(Integer id);

    /**
     * 修改文章
     * @param wmNews
     * @return
     */
    ResponseResult updateWmNews(WmNews wmNews);

    /**
     * 查询需要发布的文章id列表   文章状态等于8
     * @return
     */
    List<Integer> findRelease();

    /**
     * 查询文章列表
     * @param dto
     * @return
     */
    public PageResponseResult findList(NewsAuthDto dto);

    /**
     * 查询文章详情
     * @param id
     * @return
     */
    public WmNewsVo findWmNewsVo(Integer id) ;

}
