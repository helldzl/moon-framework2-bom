package org.moonframework.crawler.storage;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/6/28
 */
public enum Media {

    /**
     * img[src], a[href]
     */
    IMAGE("images"),

    VIDEO("videos"),

    AUDIO("audios"),

    ATTACHMENT("attachments");

    private final String name;

    Media(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
