package org.moonframework.core.amqp;

import java.util.Map;
import java.util.Set;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/14
 */
public interface Message {

    /**
     * <p>Send And Receive</p>
     *
     * @param meta          meta
     * @param data          data
     * @param relationships relationships
     * @return map
     */
    Map<String, Object> sendAndReceive(Map<String, String> meta, Map<String, Object> data, Map<String, Set<Long>> relationships);

}
