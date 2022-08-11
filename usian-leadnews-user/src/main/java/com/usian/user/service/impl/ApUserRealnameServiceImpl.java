package com.usian.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usian.common.contants.user.AdminConstans;
import com.usian.common.exception.CatchCustomException;
import com.usian.common.exception.code.UserStatusCode;
import com.usian.model.common.dtos.PageResponseResult;
import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.common.enums.AppHttpCodeEnum;
import com.usian.model.user.dtos.AuthDto;
import com.usian.model.user.pojos.ApUserRealname;
import com.usian.user.mapper.ApUserRealnameMapper;
import com.usian.user.service.ApUserRealnameService;
import com.usian.user.utils.YongyouyunAuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApUserRealnameServiceImpl extends ServiceImpl<ApUserRealnameMapper, ApUserRealname> implements ApUserRealnameService {

     @Autowired
     private ApUserRealnameMapper apUserRealnameMapper;


    //   根据状态查询全部
    @Override
    public ResponseResult loadListByStatus(AuthDto dto) {
        //参数为空
        if (dto == null) {
            return  ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //检查参数
        dto.checkParam();
        QueryWrapper<ApUserRealname> queryWrapper = new QueryWrapper<ApUserRealname>();
        if(dto.getStatus()!=null){
            queryWrapper.lambda().eq(ApUserRealname::getStatus,dto.getStatus());
        }
        Page pageParam = new Page(dto.getPage(),dto.getSize());
        IPage page = page(pageParam, queryWrapper);
        ResponseResult responseResult = new PageResponseResult(dto.getPage(),dto.getSize(),(int)page.getTotal());
        responseResult.setCode(0);
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    /**
     * 自动进行实名认证
     * @param dto
     * @return
     */
    /**
     * 响应信息      // 3 1 先身份证 2.二要素 3.活体检测
     * //    自动审核
     * //   "{\"message\":\"成功\",\"data\":{\"tradeNo\":\"22080820005558019\",\"code\":\"0\",\"riskType\":\"normal\",\"address\":\"沈阳市东陵区文化东路24-8号1-3-6\",\"birth\":\"19510322\",\"name\":\"王东镇\",\"cardNum\":\"210103195103222113\",\"sex\":\"男\",\"nation\":\"汉\",\"issuingDate\":\"\",\"issuingAuthority\":\"\",\"expiryDate\":\"\"},\"code\":\"601200000\"}"   ORC
     * //  "{\"success\":true,\"code\":400100,\"message\":\"一致\",\"data\":{\"orderNumber\":\"021659962888389491\"}}"   二要素
     * //  "{\"message\":\"成功\",\"data\":{\"checkStatus\":\"0\",\"score\":\"84\",\"tradeNo\":\"22080820581666222\",\"remark\":\"检测成功\",\"code\":\"0\"},\"code\":\"601200000\"}"     活体
     * @param dto
     * @return
     */
    @Override
    public ResponseResult AutoUpdateStatus(AuthDto dto) {
        boolean flag = true;
        if(dto ==null){
            CatchCustomException.catchs(UserStatusCode.PARAM_FAIL, "参数不合法！");
        }
        // 1. 根据Id查询用户实名认证信息
        ApUserRealname apUserRealname =  this.getById(dto.getId());
        if(apUserRealname==null){
            CatchCustomException.catchs(UserStatusCode.PARAM_FAIL, "认证用户信息有误！");
        }
        // 2. 状态为待审核状态进入审核流程  0 创建中  1 待审核  2 审核失败  8 自动审核失败，待人工审核  9 审核通过
        if(apUserRealname.getStatus()!=1){
            CatchCustomException.catchs(UserStatusCode.PARAM_FAIL, "当前数据状态有误，请检查之后重试！");
        }
        // 1. 先进行身份证OCR识别
        Map map = new HashMap();
        map.put("image",apUserRealname.getFontImage());   //身份证正面照片
        map.put("imageType","URL");
        map.put("ocrType","0");
        ResponseEntity<String> ocrEntity = YongyouyunAuthUtils.conns("https://api.yonyoucloud.com/apis/dst/IdcardOCR/IdcardOCR","ff6cee08405445e1889c5767e13ce46f",map);
        Map OcrMap =  JSON.parseObject(ocrEntity.getBody(),Map.class);
        Map data =  JSON.parseObject(OcrMap.get("data")+"",Map.class);
        String cardNum = (String) data.get("cardNum");
        if("成功".equals(OcrMap.get("message"))){ //成功
            //2.   再身份证二要素接口
            Map map2 = new HashMap();
            map2.put("idNumber",cardNum);
            map2.put("userName",apUserRealname.getName());
            ResponseEntity<String> erYaoSuEntity = YongyouyunAuthUtils.conns("https://api.yonyoucloud.com/apis/dst/matchIdentity/matchIdentity","f2648b58d851464da37b1625d35ea99c",map2);
            Map ErYaoSuEntity =  JSON.parseObject(erYaoSuEntity.getBody(),Map.class);
            if("一致".equals(ErYaoSuEntity.get("message"))){
                // 3. 最后进行活体检测
                Map map3 = new HashMap();
                map3.put("image",apUserRealname.getLiveImage());   //  活体照片
                map3.put("imageType","URL");  //  图片类型
                ResponseEntity<String> huoTiEntity = YongyouyunAuthUtils.conns("https://api.yonyoucloud.com/apis/dst/IdcardOCR/IdcardOCR","2e87b9e20228464a9b3e816ffa105ea6",map3);
                Map HuoTiEntity =  JSON.parseObject(huoTiEntity.getBody(),Map.class);
                Map data2 =  JSON.parseObject(HuoTiEntity.get("data")+"",Map.class);
                String score = data2.get("score").toString();
                Integer fen = Integer.parseInt(score);
                // score >= 80分    成功并且匹配度达到80以上
                if ("成功".equals(HuoTiEntity.get("massage")) || fen>=80){
                    return null;
                }else {
                    flag = false;
                }
            }else{
                flag = false;
            }
        }else{
            flag = false;
        }
        // 成功和失败  成功 -- 状态改正9
        if(flag){
            apUserRealname.setStatus((short)9);
            this.updateById(apUserRealname);
        }else{
            // 自动审核失败 修改状态  == 8
            // 失败： 直接修改状态  == 8
            apUserRealname.setStatus((short)8);
            this.updateById(apUserRealname);
        }
        return null;
    }
      //   人工实名认证通过/驳回
    @Override
    public ResponseResult updateStatusById(AuthDto dto, Short status) {
//        效验参数
        if(dto==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ApUserRealname byId = getById(dto.getId());
        if (status.equals(AdminConstans.PASS_AUTH)){
           apUserRealnameMapper.updateStatus(byId.getId(),dto.getMsg(),status);
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }else {
            apUserRealnameMapper.updateStatus(byId.getId(),dto.getMsg(),status);
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }
    }

}
