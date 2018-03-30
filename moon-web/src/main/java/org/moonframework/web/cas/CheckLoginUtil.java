package org.moonframework.web.cas;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckLoginUtil {

    private static final Log logger = LogFactory.getLog(CheckLoginUtil.class);

    private CheckLoginUtil() {
    }

    /**
     * @param url
     * @param resource
     * @return
     * @Function: com.budee.qr.webutil.CheckLoginUtil.checkIgnoreLogin
     * @Description: 判断是否可以忽略登录，如果返回true，则可以不登录直接访问，否则需要登录
     * @version:v0.0.1
     * @author:hongyang
     * @date:2015年7月17日 上午10:31:53
     */
    public static boolean checkIgnoreLogin(String url, String resource) {
        if (url == null) {
            return false;
        }
        String regStr = getRegStr(resource);

        Pattern p = Pattern.compile(regStr);
        Matcher matcher = p.matcher(url);
        return matcher.find();

    }

    public static String getRegStr(String resource) {
        Pattern p = Pattern.compile("\\{\\w+\\}");
        Matcher matcher = p.matcher(resource);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, ".+");
        }
        matcher.appendTail(sb);
        String regx = sb.toString();
        if (regx.endsWith("/")) {
            regx.substring(0, regx.length() - 1);
        }
        return "^" + regx + "/*$";
    }

    public static void main(String[] args) {
        logger.info(String.format("结果：%s", checkIgnoreLogin("/c/36", "/c/{id}")));
        logger.info(String.format("结果：%s", checkIgnoreLogin("/f/36", "/f/{id}")));
        logger.info(String.format("结果：%s", checkIgnoreLogin("/qrcode/33/add", "/qrcode/{static}/add")));
    }

}

    