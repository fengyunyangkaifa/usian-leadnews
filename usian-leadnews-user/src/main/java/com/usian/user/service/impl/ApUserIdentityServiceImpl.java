package com.usian.user.service.impl;

import com.usian.common.contants.user.AdminConstans;
import com.usian.common.exception.CatchCustomException;
import com.usian.common.exception.code.UserStatusCode;
import com.usian.model.article.pojos.ApAuthor;
import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.common.enums.AppHttpCodeEnum;
import com.usian.model.media.pojos.WmUser;
import com.usian.model.user.dtos.AuthDto;
import com.usian.model.user.pojos.ApUser;
import com.usian.model.user.pojos.ApUserIdentity;
import com.usian.model.user.pojos.ApUserRealname;
import com.usian.user.feign.ArticleFeign;
import com.usian.user.feign.WemediaFeign;
import com.usian.user.mapper.ApUserIdentityMapper;
import com.usian.user.mapper.ApUserMapper;
import com.usian.user.mapper.ApUserRealnameMapper;
import com.usian.user.service.ApUserIdentityService;
//import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
@Service
public class ApUserIdentityServiceImpl implements ApUserIdentityService {

    @Autowired
    private ApUserIdentityMapper apUserIdentityMapper;

    @Autowired
    private ApUserRealnameMapper apUserRealnameMapper;

    @Autowired
    private WemediaFeign wemediaFeign;

    @Autowired
    private ArticleFeign articleFeign;

    @Autowired
    private ApUserMapper apUserMapper;

    /**
     *
     * @param dto
     * @param status  认证通过还是不通过  审核
     * @return
     */

    @Override
//    @Transactional     //  本地事务
//    @GlobalTransactional   //  seata 分布式事务中 AT 解决方案
    public ResponseResult updateStatusById(AuthDto dto, Short status) {
        //参数校验
        //查询自己idEntity的对象
        ApUserIdentity apUserIdentity=  apUserIdentityMapper.findBYUserId(dto.getId());
        if (apUserIdentity==null){
            CatchCustomException.catchs(UserStatusCode.PARAM_FAIL,"没有进行实名认证/可实现联动添加");
        }
        if (apUserIdentity.getStatus()!=1){    //  待审核状态可以进行
            CatchCustomException.catchs(UserStatusCode.PARAM_FAIL,"此状态无法审和");
        }
        //查询实名认证状态 9  没有实名认证，进行实名认证
//        boolean fals=true;
        ApUserRealname apUserRealname =  apUserRealnameMapper.selectById(apUserIdentity.getUserId());
        if(apUserRealname==null){
            CatchCustomException.catchs(UserStatusCode.PARAM_FAIL,"没有进行实名认证");
//            fals=true;
        }
        if(apUserRealname.getStatus()!= 9) {
            CatchCustomException.catchs(UserStatusCode.PARAM_FAIL, "没有进行实名认证");
//            fals=true;
        }
        //   调实名认证接口
//        if (fals){
//
//        }else
        if(status.equals(AdminConstans.PASS_AUTH)){     //   审和通过  创建自媒体账号
            //实名认证过，默认审和为通过状态
          apUserIdentityMapper.updateByStatus(AdminConstans.PASS_AUTH,dto.getId(),dto.getMsg());
            //判断自媒体账号创建状态
            if(createWmUserAndAuthor(dto)!=null){
                CatchCustomException.catchs(UserStatusCode.PARAM_FAIL,"创建自媒体或者作者账号出错！");
            }
        }
        // 创建自媒体账号被驳回
        if(status.equals(AdminConstans.FAIL_AUTH)){     //   驳回
            //驳回修改状态
            apUserIdentityMapper.updateByStatus(AdminConstans.FAIL_AUTH,dto.getId(),dto.getMsg());
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
    //创建自媒体账号
    private ResponseResult createWmUserAndAuthor(AuthDto dto){
        // 先根据身份认证的Id 查询对象 获取对象中的user_id
        ApUserIdentity apUserIdentity = apUserIdentityMapper.selectById(dto.getId());
        if(apUserIdentity==null){
            CatchCustomException.catchs(UserStatusCode.PARAM_FAIL,"参数有问题");
        }
        //查询user对象
        // 因为创建自媒体账号的时候 需要把user对象中的信息 同步到WeMediaUser中
        ApUser apUser =  apUserMapper.selectById(apUserIdentity.getUserId());
        if(apUser==null){
            CatchCustomException.catchs(UserStatusCode.PARAM_FAIL,"参数有问题");
        }
        //创建自媒体账号
        //判断当前账号是否已经存在
        WmUser wmUser =  wemediaFeign.findByName(apUser.getName());
        ResponseResult save=new ResponseResult();
        if(wmUser==null){
            wmUser = new WmUser();
            wmUser.setApUserId(apUser.getId()); //关联账号
            wmUser.setCreatedTime(new Date()); //当前时间
            wmUser.setSalt("哈哈");
            wmUser.setName(apUser.getName()); //生成的登录用户名  和 普通账号一样
            wmUser.setPassword(apUser.getPassword());//生成的登录密码  和 普通账号一样
            wmUser.setStatus(9); //账号状态
            wmUser.setType(0); //账号类型   个人
            wmUser.setPhone(apUser.getPhone()); //手机号
            save= wemediaFeign.save(wmUser);  // 自媒体微服务有异常将异常给 TM
        }
        if (save.getCode()!=0){    // 自媒体微服务有异常将异常给 TM
            CatchCustomException.catchs(UserStatusCode.PARAM_FAIL,"创建自媒体账号有问题");
        }
        //创建作者账号
        createAuthor(wmUser);
        apUser.setFlag((short)1);  // 自媒体状态
        apUserMapper.updateById(apUser);
        return null;
    }

    public void createAuthor(WmUser wmUser){
        // 只能有了自媒体账号才能有作者
        Integer user_id =  wmUser.getApUserId();
        Integer wmuser_id = wmUser.getId();
        ApAuthor apAuthor =  articleFeign.findByUserId(user_id);
        ResponseResult save=new ResponseResult();
            if(apAuthor==null){
                apAuthor = new ApAuthor();
                apAuthor.setUserId(user_id);
                apAuthor.setWmUserId(wmuser_id);
                apAuthor.setName(wmUser.getName());
                apAuthor.setType(Integer.valueOf(AdminConstans.AUTHOR_TYPE)); //自媒体人类型
                save = articleFeign.save(apAuthor);
            }
            if (save.getCode()!=0){  // 作者微服务有异常将异常给 TM
                CatchCustomException.catchs(UserStatusCode.PARAM_FAIL,"创建作者账号有问题");
            }
    }
}
