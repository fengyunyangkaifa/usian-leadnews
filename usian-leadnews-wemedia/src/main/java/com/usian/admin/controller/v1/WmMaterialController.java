package com.usian.admin.controller.v1;

import com.usian.api.wemedia.WmMaterialControllerApi;
import com.usian.common.contants.wemedia.WemediaContans;
import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.media.dtos.WmMaterialDto;
import com.usian.admin.service.WmMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/material")
public class WmMaterialController implements WmMaterialControllerApi {

    @Autowired
    private WmMaterialService wmMaterialService;
//  查看素材
    @RequestMapping("/list")
    @Override
    public ResponseResult findList(@RequestBody WmMaterialDto dto) {
        return wmMaterialService.findList(dto);
    }
//    删除素材
   @GetMapping("/del_picture/{id}")
   @Override
   public ResponseResult delPicture(@PathVariable("id") Integer id) {
       return wmMaterialService.delPicture(id);
   }
    //  上传图片
    @PostMapping("/upload_picture")
    @Override
    public ResponseResult uploadPicture(@RequestPart("multipartFile") MultipartFile multipartFile,Integer id) {
        return wmMaterialService.uploadPicture(multipartFile,id);
    }
//   收藏
    @GetMapping("/cancel_collect/{id}")
    @Override
    public ResponseResult cancleCollectionMaterial(@PathVariable("id") Integer id) {
        return wmMaterialService.updateStatus(id, WemediaContans.CANCEL_COLLECT_MATERIAL);
    }
//  取消
    @GetMapping("/collect/{id}")
    @Override
    public ResponseResult collectionMaterial(@PathVariable("id") Integer id) {
        return wmMaterialService.updateStatus(id, WemediaContans.COLLECT_MATERIAL);
    }
}
