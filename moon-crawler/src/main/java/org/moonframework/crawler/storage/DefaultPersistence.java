package org.moonframework.crawler.storage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

/**
 * Created by quzile on 2016/8/23.
 */
public class DefaultPersistence extends PersistenceAdapter {

    protected static Log logger = LogFactory.getLog(DefaultPersistence.class);

    private Random random = new Random(47);

    @Override
    protected boolean exists(String type, String identity) {
        return false;
    }

    @Override
    protected void visited(String name, String type, String url) {
        if (logger.isInfoEnabled()) {
            logger.info("URL : " + url);
        }
    }

}
