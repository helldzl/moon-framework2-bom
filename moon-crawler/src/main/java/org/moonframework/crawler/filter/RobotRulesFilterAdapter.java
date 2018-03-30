package org.moonframework.crawler.filter;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;
import org.moonframework.crawler.util.HttpClientUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Freeman
 * @version 1.0
 * @since 2016/6/20
 */
public class RobotRulesFilterAdapter implements RobotRulesFilter {

    private static Log logger = LogFactory.getLog(RobotRulesFilterAdapter.class);

    private static final String ROBOTS_TXT = "/robots.txt";

    /**
     * A {@link BaseRobotRules} object appropriate for use when the
     * {@code robots.txt} file is empty or missing; all requests are allowed.
     */
    private static final BaseRobotRules EMPTY_RULES = new SimpleRobotRules(SimpleRobotRules.RobotRulesMode.ALLOW_ALL);

    /**
     * A {@link BaseRobotRules} object appropriate for use when the
     * {@code robots.txt} file is not fetched due to a {@code 403/Forbidden}
     * response; all requests are disallowed.
     */
    private static final BaseRobotRules FORBID_ALL_RULES = new SimpleRobotRules(SimpleRobotRules.RobotRulesMode.ALLOW_NONE);

    private SimpleRobotRulesParser robotParser = new SimpleRobotRulesParser();

    private volatile Map<String, BaseRobotRules> cache = new ConcurrentHashMap<>();

    /**
     * check url isAllowed
     *
     * @param url   url to check
     * @param agent robotName
     * @return the url is or not allowed
     */
    @Override
    public boolean isAllowed(String url, String agent) throws IOException {
        URL urlObj = new URL(url);
        String link = urlObj.getProtocol() + "://" + urlObj.getAuthority();
        String key = link + ":" + agent;

        if (!cache.containsKey(key)) {
            synchronized (cache) {
                if (!cache.containsKey(key)) {
                    if (logger.isInfoEnabled()) {
                        logger.info("check robots rules");
                    }

                    HttpClientUtils.execute(new HttpGet(link + ROBOTS_TXT), response -> {
                        int status = response.getStatusLine().getStatusCode();
                        if (logger.isInfoEnabled()) {
                            logger.info(String.format("robots rules status : %s", status));
                        }
                        switch (status) {
                            case HttpStatus.SC_OK:
                                BufferedHttpEntity entity = new BufferedHttpEntity(response.getEntity());
                                cache.put(key, robotParser.parseContent(link, IOUtils.toByteArray(entity.getContent()), "text/plain", agent));
                                break;
                            case HttpStatus.SC_FORBIDDEN:
                                cache.put(key, FORBID_ALL_RULES);
                                EntityUtils.consumeQuietly(response.getEntity());
                                break;
                            default:
                                cache.put(key, EMPTY_RULES);
                                EntityUtils.consumeQuietly(response.getEntity());
                                break;
                        }
                        return null;
                    });
                }
            }
        }
        boolean allowed = cache.get(key).isAllowed(url);
        if (logger.isDebugEnabled()) {
            logger.debug(url + (allowed ? " allowed" : " not allowed"));
        }

        return allowed;
    }

}
