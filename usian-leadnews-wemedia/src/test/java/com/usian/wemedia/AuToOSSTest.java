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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AuToOSSTest {

    @Autowired
    private GreeTextScan greeTextScan;

    @Autowired
    private GreenImageScanForUrl greenImageScanForUrl;

    @Test
    public void test01() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("我是一个程序员");
        list.add("加班熬夜又没钱");
        Map map =  greeTextScan.greeTextScan(list);
        System.out.println(map.get("suggestion"));
        System.out.println(map.get("label"));
    }
       //    图片
    @Test
    public void test02() throws Exception {
        ArrayList<String> urlImages = new ArrayList<>();
        urlImages.add("http://rgpbgl2cl.hb-bkt.clouddn.com/1c8716a590b0452fb06ce43047bd2bee.jpg");
        Map map = greenImageScanForUrl.imageScan(urlImages);
        System.out.println(map);
    }

    @Test
    public void test03() throws Exception {
        // TODO Auto-generated method stub
//照片URL
        String imageUrl = "http://rgpbgl2cl.hb-bkt.clouddn.com/1c8716a590b0452fb06ce43047bd2bee.jpg";
        URL url = new URL(imageUrl);
//打开网络输入流
        DataInputStream dis = new DataInputStream(url.openStream());
        String newImageName="D://img/"+ UUID.randomUUID()+".jpg";
//建立一个新的文件
        FileOutputStream fos = new FileOutputStream(new File(newImageName));
        byte[] buffer = new byte[1024];
        int length;
//开始填充数据
        while((length = dis.read(buffer))>0){
            fos.write(buffer,0,length);
        }
        dis.close();
        fos.close();
        System.out.println("执行成功");
    }

}
