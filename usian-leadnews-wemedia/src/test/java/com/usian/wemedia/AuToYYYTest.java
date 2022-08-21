package com.usian.wemedia;

import com.usian.model.media.dtos.WmNewsDto;
import com.usian.wemedia.service.impl.WmNewsServiceImpl;
import com.usian.wemedia.utils.ImageBase64Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.IOException;
import java.util.ArrayList;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AuToYYYTest {

    @Autowired
     private WmNewsServiceImpl wmNewsService;

    @Test
    public void test01() throws IOException {
        WmNewsDto wmNewsDto = new WmNewsDto();
        ArrayList<String> list = new ArrayList<>();
        list.add("http://rgpbgl2cl.hb-bkt.clouddn.com/1c8716a590b0452fb06ce43047bd2bee.jpg");
        wmNewsDto.setImages(list);
        wmNewsDto.setContent("[{\"type\":\"image\",\"value\":\"\"},{\"type\":\"text\",\"value\":\"电饭锅电饭锅电饭锅带飞\"}]");
        wmNewsDto.setStatus((short) 1);
        wmNewsService.auTuJC(wmNewsDto);
    }
      @Test
    public void test02() {
          String s=ImageBase64Utils.getImageString("D:/img/微信图片_20211117171024.jpg");
          System.out.println(s);
}

//   Base64
    //    data:image/jpeg;base64,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




}
