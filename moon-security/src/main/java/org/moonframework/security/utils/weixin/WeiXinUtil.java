/**
 * 获取用户信息
 */
package org.moonframework.security.utils.weixin;

import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZYW
 */
public class WeiXinUtil {
    private static Log logger = LogFactory.getLog(WeiXinUtil.class);

    public final static String WX_USERNAME_SUFFIX = "@budeeweixin.com";

    /**
     * 获取网页授权凭证
     *
     * @param appId     公众账号的唯一标识
     * @param appSecret 公众账号的密钥
     * @param code
     * @return WeixinAouth2Token
     */
    public static Map<String, String> getOauth2AccessToken(String appId, String appSecret, String code) {
        Map<String, String> wat = null;
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        requestUrl = requestUrl.replace("APPID", appId);
        requestUrl = requestUrl.replace("SECRET", appSecret);
        requestUrl = requestUrl.replace("CODE", code);
        // 获取网页授权凭证
        JSONObject jsonObject = WXHttpClient.httpsRequest(requestUrl, "GET", null);
        if (null != jsonObject) {
            try {
                wat = new HashMap<String, String>();
                wat.put("access_token", jsonObject.getString("access_token"));
                wat.put("openId", jsonObject.getString("openid"));
            } catch (Exception e) {
                wat = null;
                int errorCode = jsonObject.getInt("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("获取网页授权凭证失败 errcode:%s errmsg:%s", errorCode, errorMsg));
                }
            }
        }
        return wat;
    }

    /**
     * 通过网页授权获取用户信息
     *
     * @param accessToken 网页授权接口调用凭证
     * @param openId      用户标识
     * @return SNSUserInfo
     */
    public static Map<String, Object> getSNSUserInfo(String accessToken, String openId) {
        Map<String, Object> wxUser = null;
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
        // 通过网页授权获取用户信息
        JSONObject jsonObject = WXHttpClient.httpsRequest(requestUrl, "GET", null);

        if (null != jsonObject) {
            try {
                String nickName = jsonObject.getString("nickname");
                int gender = jsonObject.getInt("sex");
                String province = jsonObject.getString("province");
                String city = jsonObject.getString("city");
                String country = jsonObject.getString("country");
                String userAvatar = jsonObject.getString("headimgurl");
                String unionid = jsonObject.getString("unionid");

                wxUser = new HashMap<String, Object>();
                wxUser.put("nickName", nickName);
                wxUser.put("gender", gender);
                wxUser.put("province", province);
                wxUser.put("city", city);
                wxUser.put("country", country);
                wxUser.put("userAvatar", userAvatar);
                wxUser.put("unionid", unionid);
                wxUser.put("openId", openId);
                wxUser.put("accessToken", accessToken);

            } catch (Exception e) {
                wxUser = null;
                int errorCode = jsonObject.getInt("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("获取用户信息失败 errcode:%s errmsg:%s", errorCode, errorMsg));
                }
            }
        }
        return wxUser;
    }
}
