package com.usian.wemedia.utils;

import com.alibaba.nacos.client.identify.Base64;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * ImageBase64转换类
 * @author admin
 */

public class ImageBase64Utils {
    /**
     * 将网络图片进行Base64位编码
     * @param imageUrl 图片的url路径，如http://.....xx.jpg
     * @return
     */
    public static String encodeImgageToBase64(String imageUrl) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        ByteArrayOutputStream outputStream = null;
        try {
            BufferedImage bufferedImage = ImageIO.read(new URL(imageUrl));
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", outputStream);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        // 返回Base64编码过的字节数组字符串
        return encoder.encode(outputStream.toByteArray());
    }
    /**
     * 将本地图片进行Base64位编码
     */
    public static String encodeImgageToBase64(File imageFile) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        ByteArrayOutputStream outputStream = null;
        try {
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", outputStream);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        // 返回Base64编码过的字节数组字符串
        return encoder.encode(outputStream.toByteArray());
    }

    /**
     * 将Base64位编码进行转本地图片
     */
    public static void decodeBase64ToImage(String base64, String path,String imgName) {
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            FileOutputStream write = new FileOutputStream(new File(path
                    + imgName));
            byte[] decoderBytes = decoder.decodeBuffer(base64);
            write.write(decoderBytes);
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getImageString(String imageFile){
        InputStream is = null;
        try {
            byte[] data = null;
            is = new FileInputStream(new File(imageFile));
            data = new byte[is.available()];
            is.read(data);
            return new String(Base64.encodeBase64(data));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                    is = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }
}
