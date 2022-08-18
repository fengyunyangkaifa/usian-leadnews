package com.usian.wemedia.utils;


import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

public class KODOClientUtil {

    private final static String  ACCESSKEY = "ogD4X-No0Su0L39Cj9TC9rXduG6ehDybvqo4Rqr0";
    private final static String SECRETKEY = "FvGxSjR-X4ILAh2xMh2QegHiuT2vOz6vO2kvZiQA";
    //空间名
    private final static String NUCKETNAME = "fengyunyangkaifa";
    // 域名访问
    public final static String DOAMIN = "http://rgpbgl2cl.hb-bkt.clouddn.com/";

    /**
     * 上传文件
     * @param file
     * @return
     */
    public static String saveImage(MultipartFile file){
        try {
            // 获取文件名
            String fileName =  file.getOriginalFilename();
            //构造一个带指定 Region 对象的配置类(指定区域储存地址)
            Configuration cfg = new Configuration(Region.region1());
            UploadManager uploadManager = new UploadManager(cfg);
            //指定文件名
            // 1. 后缀名
            String fileExt =  fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
            // 2. 生成文件名
            fileName = UUID.randomUUID().toString().replaceAll("-","")+"."+fileExt;
            String upToken = Auth.create(ACCESSKEY, SECRETKEY).uploadToken(NUCKETNAME);
            Response response = uploadManager.put(file.getBytes(),fileName, upToken);
            //解析上传成功的结果
            if(response.isOK() && response.isJson()){
                //获取成功结构，获取对应存储的地址
//                System.out.println(response.toString());
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key); // 也是获取文件名
                return DOAMIN+ putRet.key;
            }else {
                System.out.println("报错");
                return null;
            }
        } catch (Exception e) {
            //ignore
            System.out.println("报错");
            return null;
        }
    }
    //删除文件
    public static void deleteFile(String key){
        //创建凭证
        Auth auth = Auth.create(ACCESSKEY, SECRETKEY);
        BucketManager bucketManager = new BucketManager(auth, new Configuration());
        try {
            bucketManager.delete(NUCKETNAME, key);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
    }


}
