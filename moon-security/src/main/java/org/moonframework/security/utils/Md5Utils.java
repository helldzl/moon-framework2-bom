package org.moonframework.security.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.security.MessageDigest;

/**
 * <p>请使用apache项目的codec</p>
 * <p>http://commons.apache.org/proper/commons-codec/userguide.html</p>
 *
 * @see org.apache.commons.codec.digest.DigestUtils#md5Hex(String)
 * @deprecated
 */
@Deprecated
public class Md5Utils {

    private static final Log logger = LogFactory.getLog(Md5Utils.class);

    private static byte[] md5(String s) {
        MessageDigest algorithm;
        try {
            algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(s.getBytes("UTF-8"));
            return algorithm.digest();
        } catch (Exception e) {
            logger.error("MD5 Error...", e);
        }
        return null;
    }

    private static String toHex(byte hash[]) {
        if (hash == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder(hash.length * 2);
        int i;

        for (i = 0; i < hash.length; i++) {
            if ((hash[i] & 0xff) < 0x10) {
                builder.append("0");
            }
            builder.append(Long.toString(hash[i] & 0xff, 16));
        }
        return builder.toString();
    }

    public static String hash(String s) {
        try {
            return new String(toHex(md5(s)).getBytes("UTF-8"), "UTF-8");
        } catch (Exception e) {
            logger.error("not supported charset...{}", e);
            return s;
        }
    }

    public static void main(String[] args) {
        System.out.println(hash("张三"));
    }

}
