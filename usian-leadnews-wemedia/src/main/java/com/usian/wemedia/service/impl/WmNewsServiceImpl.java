package com.usian.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usian.wemedia.mapper.WmMaterialMapper;
import com.usian.wemedia.mapper.WmNewsMapper;
import com.usian.wemedia.mapper.WmNewsMaterialMapper;
import com.usian.wemedia.service.WmNewsService;
import com.usian.wemedia.utils.YongyouyunAuthUtils;
import com.usian.common.contants.wemedia.WemediaContans;
import com.usian.common.exception.CatchCustomException;
import com.usian.common.exception.code.UserStatusCode;
import com.usian.model.common.dtos.PageResponseResult;
import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.common.enums.AppHttpCodeEnum;
import com.usian.model.media.dtos.WmNewsDto;
import com.usian.model.media.dtos.WmNewsPageReqDto;
import com.usian.model.media.pojos.WmMaterial;
import com.usian.model.media.pojos.WmNews;
import com.usian.model.media.pojos.WmNewsMaterial;
import com.usian.model.media.pojos.WmUser;
import com.usian.utils.common.Base64Utils;
import com.usian.utils.threadlocal.WmThreadLocalUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Value("${fdfs.url}")
    private String fileServerUrl;
    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;
    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    @Override
    public ResponseResult findAll(WmNewsPageReqDto dto) {
        //1.参数检查
        if(dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //分页参数检查
        dto.checkParam();
        //2.分页条件查询
        IPage pageParam = new Page(dto.getPage(),dto.getSize());

        LambdaQueryWrapper<WmNews> lambdaQueryWrapper = new LambdaQueryWrapper();
        //状态精确查询
        if(dto.getStatus() != null){
            lambdaQueryWrapper.eq(WmNews::getStatus,dto.getStatus());
        }
        //频道精确查询
        if(null != dto.getChannelId()){
            lambdaQueryWrapper.eq(WmNews::getChannelId,dto.getChannelId());
        }
        //时间范围查询
        if(dto.getBeginPubDate()!=null && dto.getEndPubDate()!=null){
            lambdaQueryWrapper.between(WmNews::getPublishTime,dto.getBeginPubDate(),dto.getEndPubDate());
        }
        //关键字模糊查询
        if(null != dto.getKeyword()){
            lambdaQueryWrapper.like(WmNews::getTitle,dto.getKeyword());
        }
        //查询当前登录用户的信息
        WmUser user = WmThreadLocalUtils.getUser();
        if(user==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        lambdaQueryWrapper.eq(WmNews::getUserId,user.getId());
        //按照创建日期倒序
        lambdaQueryWrapper.orderByDesc(WmNews::getCreatedTime);
        IPage pageResult = page(pageParam, lambdaQueryWrapper);
        //3.结果封装返回
        PageResponseResult responseResult = new PageResponseResult(dto.getPage(),dto.getSize(),(int)pageResult.getTotal());
        List<WmNews> datas = pageResult.getRecords();
        //为每个图片加上前缀
        datas = datas.stream().map(item->{
            item.setImages(fileServerUrl+item.getImages());
            return item;
        }).collect(Collectors.toList());
        responseResult.setData(datas);
        return responseResult;
    }
//     保存,草稿
    @Override
    public ResponseResult saveNews(WmNewsDto dto, Short isSubmit) {
        if (dto==null || StringUtils.isBlank(dto.getContent())){
            CatchCustomException.catchs(UserStatusCode.LOGIN_STATUS,"文章信息不能为空");
        }
        //  保存或修改文章
        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(dto,wmNews);   //  全量复制进实体类进行添加
//         对特殊字段处理
        if (WemediaContans.WM_NEWS_TYPE_AUTO.equals(wmNews.getType())){   //  若为自动类型，需要先做默认为空
            wmNews.setType(null);
        }
//       对图片进行解析 前台是list集合  数据库存储是 字符串   先遍历为一个个的String串
        if (dto.getImages()!=null && dto.getImages().size()>0 ) {
            wmNews.setImages(dto.getImages().toString().replace("[", "")
                    .replace("]", "").replace(fileServerUrl, "")
                    .replace(" ", ""));
        }
//           保存或修改文章 内容
            saveOfSave(wmNews,isSubmit);
//             关联素材和文章
        String content = dto.getContent();
        List<Map> maps = JSON.parseArray(content, Map.class);
//             提取文章中的素材
        List<String> imageList=suCaiList(maps);
//          是提交状态   status==1
        if (WmNews.Status.SUBMIT.getCode()==isSubmit && imageList.size()!=0){
//            文章中图片与素材关联
              guAnLianSUCaiRN(imageList,wmNews.getId());
        }
//          关联封页和文章
          if (WmNews.Status.SUBMIT.getCode()==isSubmit){
            guAnLianSUCaiFM(imageList,dto,wmNews);
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
    private void  saveOfSave(WmNews wmNews,Short isSubmit){
        //  保存、草稿
        wmNews.setStatus(isSubmit);
        wmNews.setUserId(WmThreadLocalUtils.getUser().getId());  // 自媒体用户ID
        wmNews.setCreatedTime(new Date());
        wmNews.setPublishTime(null);
        wmNews.setSubmitedTime(new Date());
        wmNews.setEnable(WemediaContans.WM_NEWS_ENABLE_UP);  //  默认为下架状态  1
        if (wmNews.getId()==null){  //  不存在就添加
            this.save(wmNews);
        }else {   //  存在则修改文章内容 先删除关联表的信息
            LambdaQueryWrapper<WmNewsMaterial> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WmNewsMaterial::getNewsId,wmNews.getId());
            wmNewsMaterialMapper.delete(queryWrapper);
      //          修改的内容
           this.updateById(wmNews);
        }
    }
//    提取素材、照片信息
    private List<String> suCaiList( List<Map> maps){
        ArrayList<String> urlImages = new ArrayList<>();
        if (maps==null && maps.size()>0){
            CatchCustomException.catchs(UserStatusCode.LOGIN_STATUS,"文章信息不能为空");
        }
        for (Map map : maps) {
            if (map.get("type").equals(WemediaContans.WM_NEWS_TYPE_IMAGE)){
                urlImages.add(map.get("value").toString().replace(fileServerUrl,""));
            }
        }
        return urlImages;
    }

    //  提取 文本信息
    private String wenBen( List<Map> maps){
      String  wenBenNeiRong="";
        if (maps==null && maps.size()>0){
            CatchCustomException.catchs(UserStatusCode.LOGIN_STATUS,"文章信息不能为空");
        }
        for (Map map : maps) {
            if (map.get("type").equals(WemediaContans.WM_NEWS_TYPE_WENBEN)){
                wenBenNeiRong=(map.get("value").toString());
            }
        }
        return wenBenNeiRong;
    }
//    内容关联素材
    private void guAnLianSUCaiRN(List<String> imageList,Integer newID){
        guAnLianSUCai(imageList,newID,WemediaContans.WM_CONTENT_REFERENCE);
    }

    //    封面关联内容
    private void guAnLianSUCaiFM(List<String>  imageList, WmNewsDto dto,WmNews wmNews){
        List<String> images = dto.getImages();  //  封面属性
      if (WemediaContans.WM_NEWS_TYPE_AUTO.equals(dto.getType())){
          //自动获取封面
          if (imageList!=null && imageList.size()>0){
              // 单图
              wmNews.setType(WemediaContans.WM_NEWS_SINGLE_IMAGE);
              images = imageList.stream().limit(1).collect(Collectors.toList());
          }else if (imageList!=null && imageList.size()>3){
              //  三图
              wmNews.setType(WemediaContans.WM_NEWS_MANY_IMAGE);
              images= imageList.stream().limit(3).collect(Collectors.toList());
          }else {
              //无图
              wmNews.setType(WemediaContans.WM_NEWS_NONE_IMAGE);
          }
     //     对图片进行解析 前台是list集合  数据库存储是 字符串   先遍历为一个个的String串
          if (images != null && images.size() > 0) {
//              String image = "";
//              for (String item : images) {
//                  image += item + ",";
//              }
//              wmNews.setImages(image.substring(0, image.length() - 1));
              wmNews.setImages(images.toString().replace("[", "")
                      .replace("]", "").replace(fileServerUrl, "")
                      .replace(" ", ""));
          }
          //修改WmNews 状态  因为 最开始的时候 保存了数据库了 在这个地方进行修改状态
          this.updateById(wmNews);
          // 主图引用 保存关系
          if (images != null && images.size() > 0) {
              guAnLianSUCai(images,wmNews.getId(),WemediaContans.WM_COVER_REFERENCE);
          }
      }
    }
//    进行素材与内容添加
    private void guAnLianSUCai(List<String> imageList,Integer newID,Short type){
//          先获取数据库中的图片信息
        LambdaQueryWrapper<WmMaterial> WmMaterialQueryWrapper = new LambdaQueryWrapper<>();
        WmMaterialQueryWrapper.in(WmMaterial::getUrl,imageList); //  根据内容图片信息查数据库 图片信息
        WmMaterialQueryWrapper.eq(WmMaterial::getUserId,WmThreadLocalUtils.getUser().getId());//  是该用户的 素材子信息
        List<WmMaterial> wmMaterials = wmMaterialMapper.selectList(WmMaterialQueryWrapper);  //  查询到所有对应的信息
        Map<String, Integer> collect = wmMaterials.stream().collect(Collectors.toMap(WmMaterial::getUrl, WmMaterial::getId));
        ArrayList<String> addURl = new ArrayList<>();
        if (wmMaterials !=null && wmMaterials.size() >0 ){
            for (String url : imageList) {  //  遍历 文章中的 url
                Integer urlID = collect.get(url);  // 图片 ID
                if (urlID==null) {
                    CatchCustomException.catchs(UserStatusCode.PARAM_FAIL,"图片有问题");
                }
                addURl.add(String.valueOf(urlID));
            }
        }
        //   批量添加关联表信息
        wmNewsMaterialMapper.saveRelationsByContent(addURl,newID,type);
    }
    @Override
    public ResponseResult findWmNewsById(Integer id) {
        //1.参数检查
        if(id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"文章Id不可缺少");
        }
        //2.查询数据
        WmNews wmNews = getById(id);
        if(wmNews == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"文章不存在");
        }
        //3.结果返回
        ResponseResult responseResult = ResponseResult.okResult(wmNews);
        return responseResult;
    }

    @Override
    public ResponseResult delNews(Integer id) {
        //1.检查参数
        if(id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"文章Id不可缺少");
        }
        //2.获取数据
        WmNews wmNews = getById(id);
        if(wmNews == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"文章不存在");
        }

        //3.判断当前文章的状态  status==9  enable == 1
        if(wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode()) && wmNews.getEnable().equals(WemediaContans.WM_NEWS_ENABLE_UP)){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"文章已发布，不能删除");
        }
        //4.去除素材与文章的关系
        wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId,wmNews.getId()));

        //5.删除文章
        removeById(wmNews.getId());
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult downOrUp(WmNewsDto dto) {
        //1.检查参数
        if(dto == null || dto.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.查询文章
        WmNews wmNews = getById(dto.getId());
        if(wmNews == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"文章不存在");
        }
        //3.判断文章是否发布
        if(!wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode())){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"当前文章不是发布状态，不能上下架");
        }
        //4.修改文章状态，同步到app端（后期做）TODO
        if(dto.getEnable() != null && dto.getEnable() > -1 && dto.getEnable() < 2){
            update(Wrappers.<WmNews>lambdaUpdate().eq(WmNews::getId,dto.getId()).set(WmNews::getEnable,dto.getEnable()));
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }


    /**
     /*  自动 图片审核
     * @param dto
     * @return
     */

    @Override
    public ResponseResult auTuJC(WmNewsDto dto) {
        boolean flag = true;
        if(dto ==null){
            CatchCustomException.catchs(UserStatusCode.PARAM_FAIL, "参数不合法！");
        }
        // 2. 状态为待审核状态进入审核流程  0 创建中  1 待审核  2 审核失败  8 自动审核失败，待人工审核  9 审核通过
        if(!dto.getStatus().equals((short)1)){
            CatchCustomException.catchs(UserStatusCode.PARAM_FAIL, "当前数据状态有误，请检查之后重试");
        }
        String content = dto.getContent();
        List<Map> maps = JSON.parseArray(content, Map.class);
//             提取文章中的素材   公网图片
        String urlImages="";
        if (StringUtils.isNotBlank(content)){
            List<String> imageList=suCaiList(maps);
            if (imageList!=null && imageList.size()>0){
                for (String s : imageList) {
                    urlImages = Base64Utils.encode(s.getBytes());
                }
            }
        }
        if (dto.getImages()!=null && dto.getImages().size()>0){
            urlImages+=Base64Utils.decode(dto.getImages().toString().replace("[","").replace("]",""));
        }
//         1. 先进行素材检测    0 不检测  1  检测
        Map<String,String> map = new HashMap<>();
        map.put("Politician", "1");  //  政治敏感识别
        map.put("image", urlImages);  // Base64编码字符串  图片 二进制 转换
        map.put("Disgust", "1");  //  恶心图像识别
        map.put("Antiporn", "1");  //  色情识别
        map.put("Quality", "1");   //  图像质量识别
        map.put("Anti_spam", "1");   //  图文审核
        map.put("Watermark", "1");   // 广告审核
        map.put("Terror", "1");     //  暴恐审核
        ResponseEntity<String> ocrEntity = YongyouyunAuthUtils.conns(YongyouyunAuthUtils.SCJC, "f113ffd4ec7947eb9f7ee45f577c6bdb",map);
        Map  OcrMap=  JSON.parseObject(ocrEntity.getBody(),Map.class);
        Map data =  JSON.parseObject(OcrMap.get("data")+"",Map.class);
        if("成功".equals(OcrMap.get("message"))){ //成功
//          文本检测
        if (StringUtils.isNotBlank(dto.getContent())){
            String s = wenBen(maps);
             Map wenBen = new HashMap<>();
             wenBen.put("text",s);
         ResponseEntity<String> ocrEntity2 = YongyouyunAuthUtils.conns(YongyouyunAuthUtils.WENZIJC, "9180764ea4154729aad1a422670dca19",wenBen);
        Map  OcrMap2=  JSON.parseObject(ocrEntity2.getBody(),Map.class);
                  // {vec_fragment=[], score=0, correct_query=啊哈哈哈哈哈哈哈}
         Map data1 =  JSON.parseObject(OcrMap2.get("data")+"",Map.class);
            Integer score = Integer.parseInt(data1.get("score").toString());
            if (score==0){
               flag=true;
               }else {
                flag=false;
               }
            }
        }else {
            flag=false;
        }
//         成功和失败  成功 -- 状态改正9
        if(flag){
//            apUserRealname.setStatus((short)9);
//            this.updateById(apUserRealname);
        }else{
//             自动审核失败 修改状态  == 8
//             失败： 直接修改状态  == 8
//            apUserRealname.setStatus((short)8);
//            this.updateById(apUserRealname);
        }
        return null;
    }



}
