package org.moonframework.crawler.storage;

import java.io.Serializable;
import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/9/20
 */
public class Remove implements Serializable {

    private static final long serialVersionUID = -6132567025712336320L;

    private boolean clone = true;

    private List<String> selectors;

    public boolean isClone() {
        return clone;
    }

    public void setClone(boolean clone) {
        this.clone = clone;
    }

    public List<String> getSelectors() {
        return selectors;
    }

    public void setSelectors(List<String> selectors) {
        this.selectors = selectors;
    }

}
