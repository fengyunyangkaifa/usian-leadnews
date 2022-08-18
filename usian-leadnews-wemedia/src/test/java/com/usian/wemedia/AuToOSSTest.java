package com.usian.wemedia;

import com.usian.common.aliyun.GreeTextScan;
import com.usian.common.aliyun.GreenImageScanForUrl;
import com.usian.model.media.dtos.WmNewsDto;
import com.usian.wemedia.service.impl.WmNewsServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AuToOSSTest {

    @Autowired
    private WmNewsServiceImpl wmNewsService;
@Autowired
private GreeTextScan greeTextScan;

@Autowired
private GreenImageScanForUrl greenImageScanForUrl;

    @Test
    public void test01(){
        WmNewsDto wmNewsDto = new WmNewsDto();
        ArrayList<String> list = new ArrayList<>();
        list.add("http://rgpbgl2cl.hb-bkt.clouddn.com/1c8716a590b0452fb06ce43047bd2bee.jpg");
        wmNewsDto.setImages(list);
        wmNewsDto.setContent("[{\"type\":\"image\",\"value\":\"http://192.168.211.132:8080/group1/M00/00/00/wKjThGL9poKAACifAABo3qgUNSA363.jpg\"},{\"type\":\"text\",\"value\":\"电饭锅电饭锅电饭锅带飞\"},{\"type\":\"text\",\"value\":\"请在这里输入正文\"}]");
        wmNewsDto.setStatus((short) 1);
        wmNewsService.auTuJC(wmNewsDto);
    }

}
