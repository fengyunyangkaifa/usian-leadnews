package com.usian.wemedia.controller.v1;

import com.usian.model.admin.dtos.NewsAuthDto;
import com.usian.model.common.dtos.PageResponseResult;
import com.usian.model.common.enums.AppHttpCodeEnum;
import com.usian.model.media.vo.WmNewsVo;
import com.usian.wemedia.service.WmNewsService;
import com.usian.api.wemedia.WmNewsControllerApi;
import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.media.dtos.WmNewsDto;
import com.usian.model.media.dtos.WmNewsPageReqDto;
import com.usian.model.media.pojos.WmNews;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController implements WmNewsControllerApi {

    @Autowired
    private WmNewsService wmNewsService;

    @PostMapping("/list")
    @Override
    public ResponseResult findAll(@RequestBody WmNewsPageReqDto wmNewsPageReqDto){
        return wmNewsService.findAll(wmNewsPageReqDto);
    }

    @PostMapping("/submit")
    @Override
    public ResponseResult submitNews(@RequestBody WmNewsDto wmNews) {
        if(wmNews.getStatus()== WmNews.Status.SUBMIT.getCode()){
            //提交文章
            return wmNewsService.saveNews(wmNews, WmNews.Status.SUBMIT.getCode());
        }else{
            //保存草稿
            return wmNewsService.saveNews(wmNews, WmNews.Status.NORMAL.getCode());
        }
    }
    @GetMapping("/one/{id}")
    @Override
    public ResponseResult findWmNewsById(@PathVariable("id") Integer id) {
        return wmNewsService.findWmNewsById(id);
    }
    @GetMapping("/del_news/{id}")
    @Override
    public ResponseResult delNews(@PathVariable("id") Integer id) {
        return wmNewsService.delNews(id);
    }

    @PostMapping("/down_or_up")
    @Override
    public ResponseResult downOrUp(@RequestBody WmNewsDto dto) {
        return wmNewsService.downOrUp(dto);
    }

    @GetMapping("/findOne/{id}")
    @Override
    public WmNews findById(@PathVariable("id") Integer id) {
        return wmNewsService.getById(id);
    }

    @PostMapping("/update")
    @Override
    public ResponseResult updateWmNews(@RequestBody WmNews wmNews) {
        wmNewsService.updateById(wmNews);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @GetMapping("/findRelease")
    @Override
    public List<Integer> findRelease() {
        return wmNewsService.findRelease();
    }

    @PostMapping("/findList")
    @Override
    public PageResponseResult findList(@RequestBody NewsAuthDto dto){
        return wmNewsService.findListAndPage(dto);
    }

    @GetMapping("/find_news_vo/{id}")
    @Override
    public WmNewsVo findWmNewsVo(@PathVariable("id") Integer id) {
        return wmNewsService.findWmNewsVo(id);
    }
}
