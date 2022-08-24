package com.usian.admin.job;

import com.usian.admin.feign.WemediaFeign;
import com.usian.admin.service.WemediaNewsAutoScanService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
public class WeMediaNewsAutoScanJob {

    @Autowired
    private WemediaNewsAutoScanService wemediaNewsAutoScanService;

    @Autowired
    private WemediaFeign wemediaFeign;

    /**
     * 每5秒点执行一次
     * @param param
     * @return
     * @throws Exception
     */
    @XxlJob("wemediaAutoScanJob")    // jobHandler 名称
    public ReturnT<String> hello(String param) throws Exception {
        log.info("自媒体文章审核调度任务开始执行....");
        List<Integer> releaseIdList = wemediaFeign.findRelease();
        if(null!=releaseIdList && !releaseIdList.isEmpty()){
            for (Integer id : releaseIdList) {
                wemediaNewsAutoScanService.autoScanByMediaNewsId(id);
            }
        }else {
            return ReturnT.FAIL;
        }
        log.info("自媒体文章审核调度任务执行结束....");
        return ReturnT.SUCCESS;
    }
}
