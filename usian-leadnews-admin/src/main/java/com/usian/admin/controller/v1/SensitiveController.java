package com.usian.admin.controller.v1;

import com.usian.admin.service.AdSensitiveService;
import com.usian.api.admin.SensitiveControllerApi;
import com.usian.model.admin.dtos.SensitiveDto;
import com.usian.model.admin.pojos.AdSensitive;
import com.usian.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/sensitive")
public class SensitiveController implements SensitiveControllerApi {

    @Autowired
    private AdSensitiveService adSensitiveService;
    @PostMapping("/list")
    @Override
    public ResponseResult list(@RequestBody SensitiveDto dto) {
        return adSensitiveService.list(dto);
    }

    @PostMapping("/save")
    @Override
    public ResponseResult save(@RequestBody AdSensitive adSensitive) {
        return adSensitiveService.insert(adSensitive);
    }

    @PostMapping("/update")
    @Override
    public ResponseResult update(@RequestBody AdSensitive adSensitive) {
        return adSensitiveService.update(adSensitive);
    }

    @DeleteMapping("/del/{id}")
    @Override
    public ResponseResult deleteById(@PathVariable("id") Integer id) {
        return adSensitiveService.deleteById(id);
    }
}
