package com.usian.admin.controller.v1;

import com.usian.admin.service.WmNewsService;
import com.usian.api.wemedia.WmNewsControllerApi;
import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.media.dtos.WmNewsPageReqDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
