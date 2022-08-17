package com.usian.admin.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class OSSClientUtil {
    // endpoint以杭州为例，其它region请按实际情况填写
    // oss-cn-beijing.aliyuncs.com
    private String endpoint = "http://oss-cn-beijing.aliyuncs.com";
    // accessKey
    private String accessKeyId = "LTAI5tPGtNbahUbK9pRF2CA6";
    private String accessKeySecret = "Lg2DYLT0m5I5cqtFs53wjHG0RSuSOx";
    //空间
    private String bucketName = "fengyunyangkaifa";
    //文件存储目录 data/
    private String filedir = "media/";

    private String domain = "https://fengyunyangkaifa.oss-cn-beijing.aliyuncs.com/";

    private OSS ossClient;

    /**
     * 初始化 创建OSSClient实例。 方式一  使用构造方式
     */
    public OSSClientUtil() {
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
    /**
     * 初始化 创建OSSClient实例。 方式二  使用init方法
     */
    public void init(){
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    public void destory(){
        if(ossClient!=null){
            //销毁方法
            ossClient.shutdown();
        }
    }


    /**
     * 上传文件
     * @param file
     * @return
     */
    public String saveImage(MultipartFile file){
        try {
            // 获取文件名
            String fileName =  file.getOriginalFilename();
            // 1. 后缀名
            String fileExt =  fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
            // 2. 生成文件名
            fileName = UUID.randomUUID().toString().replaceAll("-","")+"."+fileExt;
            //  filedir+fileName   =  media/jkljkljlkjkl.png
            PutObjectResult putObjectResult = ossClient.putObject(bucketName, filedir+fileName, file.getInputStream());
            // 如果是共有空间，访问需要自己封装请求URL
            return domain+filedir+fileName;
        }catch (Exception e){
            System.out.println("失败");
            return null;
        }
    }
    /**
     * 获取访问地址 （私有空间访问方式 自动生成 带访问秘钥的）
     * http://unsiannews.oss-cn-beijing.aliyuncs.com/60DB483EDCBDBD7EEFAC4A78DDB5C46E?Expires=1654510152&OSSAccessKeyId=LTAI5tKepz6hcxys4P1aBHa7&Signature=71MvvflAqJmW8qCVT6waxcnOtdU%3D
     */
    public String getUrl(String key){
        // 开发中写算法  不写最终的乘积  为了方便知道多久
        // new Date().getTime() 毫秒值 + 60*60*1000*24*365*10  当前生成的url保质期10年
        Date urlTimeOut = new Date(new Date().getTime() + 60*60*1000*24*365*10 );
        /**
         * 1. 存储空间  2. 文件名称  3. 当前生成的URL的过期时间
         */
        URL url = ossClient.generatePresignedUrl(bucketName,key,urlTimeOut);
        return url.toString();
    }

    public void getDeletedObjects(String fileName){
    // 创建OSSClient实例。
    OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
        // 列举所有包含指定前缀的文件并删除。
        String nextMarker = null;
        ObjectListing objectListing = null;
        do {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName)
                    .withPrefix(fileName)   // 指定目录删除
                    .withMarker(nextMarker);
            objectListing = ossClient.listObjects(listObjectsRequest);
            if (objectListing.getObjectSummaries().size() > 0) {
                List<String> keys = new ArrayList<String>();
                for (OSSObjectSummary s : objectListing.getObjectSummaries()) {
                    System.out.println("key name: " + s.getKey());
                    keys.add(s.getKey());
                }
                DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName).withKeys(keys).withEncodingType("url");
                DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(deleteObjectsRequest);
                List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
                try {
                    for(String obj : deletedObjects) {
                        String deleteObj =  URLDecoder.decode(obj, "UTF-8");
                        System.out.println(deleteObj);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            nextMarker = objectListing.getNextMarker();
        } while (objectListing.isTruncated());
    } catch (OSSException oe) {
        System.out.println("Caught an OSSException, which means your request made it to OSS, "
                + "but was rejected with an error response for some reason.");
        System.out.println("Error Message:" + oe.getErrorMessage());
        System.out.println("Error Code:" + oe.getErrorCode());
        System.out.println("Request ID:" + oe.getRequestId());
        System.out.println("Host ID:" + oe.getHostId());
    } catch (ClientException ce) {
        System.out.println("Caught an ClientException, which means the client encountered "
                + "a serious internal problem while trying to communicate with OSS, "
                + "such as not being able to access the network.");
        System.out.println("Error Message:" + ce.getMessage());
    } finally {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
    }
}

