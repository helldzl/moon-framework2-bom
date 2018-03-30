package org.moonframework.crawler.storage;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/10/11
 */
public class Status {

    private Long id;
    private boolean update;

    public Status(Long id, boolean update) {
        this.id = id;
        this.update = update;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

}
