package com.usian.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usian.wemedia.utils.KODOClientUtil;
import com.usian.wemedia.utils.OSSClientUtil;
import com.usian.common.fastdfs.FastDFSClientUtil;
import com.usian.model.common.dtos.PageResponseResult;
import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.common.enums.AppHttpCodeEnum;
import com.usian.model.media.dtos.WmMaterialDto;
import com.usian.model.media.pojos.WmMaterial;
import com.usian.model.media.pojos.WmNewsMaterial;
import com.usian.model.media.pojos.WmUser;
import com.usian.utils.threadlocal.WmThreadLocalUtils;
import com.usian.wemedia.mapper.WmMaterialMapper;
import com.usian.wemedia.mapper.WmNewsMaterialMapper;
import com.usian.wemedia.service.WmMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    @Autowired
    private FastDFSClientUtil fastDFSClient;

    @Value("${fdfs.url}")
    private String fileServerUrl;

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile,Integer id) {
        //1.检查参数
        if(multipartFile == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.上传图片到fasfdfs
        String fileId = null;
        try {
//            fasfdfs 文件上传
//        fileId = fastDFSClient.uploadFile(multipartFile);
            if (0==id){
                // DFS 上传
                fileId = fastDFSClient.uploadFile(multipartFile);
            }else if (1==id){
//            KODO 上传
                fileId = KODOClientUtil.saveImage(multipartFile);
            }else {
//              OSS  上传
                OSSClientUtil ossClientUtil = new OSSClientUtil();
                fileId = ossClientUtil.saveImage(multipartFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
        //3.保存素材数据到表中 wm_material
        WmUser user = WmThreadLocalUtils.getUser();  //   用户线程中的信息
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUrl(fileId);
        wmMaterial.setIsCollection((short)0);   //  未收藏状态
        wmMaterial.setUserId(user.getId());
        wmMaterial.setType((short)0);   //   图片
        wmMaterial.setCreatedTime(new Date());
        save(wmMaterial);
        //拼接图片路径
        if (1==id){
            wmMaterial.setUrl(fileServerUrl+fileId);  //  添加返回前端拼接服务上的url路径
        }
        return ResponseResult.okResult(wmMaterial);
    }
    @Override
    public ResponseResult findList(WmMaterialDto dto) {
        //1.检查参数
        dto.checkParam();
        //2.带条件分页查询
        LambdaQueryWrapper<WmMaterial> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //获取当前登录的用户
        Integer uid = WmThreadLocalUtils.getUser().getId();
        System.out.println(uid);
        lambdaQueryWrapper.eq(WmMaterial::getUserId,uid);  //  是否是当前用户
        //是否收藏
        if(dto.getIsCollection() != null && dto.getIsCollection().shortValue()==1){   //  列表只显示收藏状态信息
            lambdaQueryWrapper.eq(WmMaterial::getIsCollection,dto.getIsCollection());
        }
        //按照日期倒序排序
        lambdaQueryWrapper.orderByDesc(WmMaterial::getCreatedTime);
        IPage pageParam = new Page(dto.getPage(),dto.getSize());
        IPage resultPage = page(pageParam, lambdaQueryWrapper);
        //3.结果返回
        PageResponseResult responseResult = new PageResponseResult(dto.getPage(),dto.getSize(),(int)resultPage.getTotal());
        List<WmMaterial> datas = resultPage.getRecords();
        //为每个图片加上前缀
        datas = datas.stream().map(item->{
            item.setUrl(fileServerUrl+item.getUrl());
            return item;
        }).collect(Collectors.toList());
        responseResult.setData(datas);
        return responseResult;
    }
//   删除素材
    @Override
    public ResponseResult delPicture(Integer id) {
        //1.检查参数
        if(id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.判断当前图片是否问引用
        WmMaterial wmMaterial = getById(id);
        if(wmMaterial == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        LambdaQueryWrapper<WmNewsMaterial> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(WmNewsMaterial::getMaterialId,id);
        Integer count = wmNewsMaterialMapper.selectCount(lambdaQueryWrapper);
        if(count > 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"当前图片被引用");
        }
        //3.删除fastdfs中的图片
//        String fileId = wmMaterial.getUrl().replace(fileServerUrl, "");  //  替换路径 直接删除文件路径
        // KODO 删除文件
        String fileId = wmMaterial.getUrl().replace(KODOClientUtil.DOAMIN, "");  //  替换路径 直接删除文件路径
        try {
//            fastDFSClient.delFile(fileId);
            KODOClientUtil.deleteFile(fileId);  // 没删除
        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
        //4.删除数据库中的图片
        removeById(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
//     收藏或取消
    @Override
    public ResponseResult updateStatus(Integer id, Short type) {
        //1.检查参数
        if(id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.更新状态
        //获取当前用户信息
        Integer uid = WmThreadLocalUtils.getUser().getId();
        update(Wrappers.<WmMaterial>lambdaUpdate().set(WmMaterial::getIsCollection,type)
                .eq(WmMaterial::getId,id).eq(WmMaterial::getUserId,uid));

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

}
