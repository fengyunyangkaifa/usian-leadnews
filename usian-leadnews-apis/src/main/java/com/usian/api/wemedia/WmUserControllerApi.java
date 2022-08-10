package com.usian.api.wemedia;

import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.media.pojos.WmUser;

public interface WmUserControllerApi {

    /**
     * 保存自媒体用户
     * @param wmUser
     * @return
     */
    public ResponseResult save(WmUser wmUser);

    /**
     * 按照名称查询用户
     * @param name
     * @return
     */
    public WmUser findByName(String name);
}
