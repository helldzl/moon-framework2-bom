package org.moonframework.crawler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/9/22
 */
public class EncodeTests {

    public static void main(String[] args) {
        String str2 = "灯光+舞台";
        System.out.println(encode(str2));
        System.out.println(encode("贝斯"));
        System.out.println(decode(encode(str2)));
    }

    public static String encode(String s) {
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decode(String s) {
        if (s == null)
            return null;
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
