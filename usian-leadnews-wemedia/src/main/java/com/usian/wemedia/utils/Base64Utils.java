package com.usian.wemedia.utils;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
public class Base64Utils {
    public static String encode(byte[] src) {
        byte[] encodeBytes = Base64.getEncoder().encode(src);
        return new String(encodeBytes);
    }

    public static String encode(String src) {
        byte[] encodeBytes = Base64.getEncoder().encode(src.getBytes());
        return new String(encodeBytes);
    }

    public static String decode(String src) {
        byte[] decodeBytes = Base64.getDecoder().decode(src.getBytes());
        return new String(decodeBytes);
    }

    public static String encode(String src, String charset) throws UnsupportedEncodingException {
        byte[] encodeBytes = Base64.getEncoder().encode(src.getBytes(charset));
        return new String(encodeBytes);
    }

    public static String decode(String src, String charset) throws UnsupportedEncodingException {
        byte[] decodeBytes = Base64.getDecoder().decode(src.getBytes());
        return new String(decodeBytes,charset);
    }
}
