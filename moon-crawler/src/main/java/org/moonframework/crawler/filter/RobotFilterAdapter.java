package org.moonframework.crawler.filter;

import org.moonframework.crawler.util.HttpClientUtils;

import java.io.IOException;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/6/27
 */
public class RobotFilterAdapter implements LinkFilter {

    private RobotRulesFilterAdapter robotRulesFilterAdapter = new RobotRulesFilterAdapter();

    @Override
    public boolean filter(String type, String url) {
        try {
            return robotRulesFilterAdapter.isAllowed(url, HttpClientUtils.USER_AGENT);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean exist(String type, String url) {
        return false;
    }

}
