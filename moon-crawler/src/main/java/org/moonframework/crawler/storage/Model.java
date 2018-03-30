package org.moonframework.crawler.storage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by quzile on 2016/8/30.
 */
public enum Model {

    /**
     * 缩略图
     */
    THUMB(1, "T", 80),

    /**
     * 列表图
     */
    SMALL(2, "S", 220),

    /**
     * 详情图
     */
    MEDIUM(3, "M", 400),

    /**
     * 大图
     */
    LARGE(4, "L", 800),

    /**
     * 超清图
     */
    EXTRA_LARGE(5, "XL", 1200),

    /**
     * 360度图集
     */
    IMAGE_360(36, "360", -1);

    public static Map<String, Model> nameMap = new HashMap<>(5);
    public static Map<Integer, Model> idMap = new HashMap<>(5);

    static {
        for (Model model : Model.values()) {
            nameMap.put(model.getName(), model);
            idMap.put(model.getId(), model);
        }
    }

    public static Model from(String name) {
        return nameMap.get(name);
    }

    public static Model from(Integer id) {
        return idMap.get(id);
    }

    public static List<Model> lt(Model source) {
        return filter(model -> model.getId() < source.getId());
    }

    public static List<Model> le(Model source) {
        return filter(model -> model.getId() <= source.getId());
    }

    private static List<Model> filter(Predicate<? super Model> predicate) {
        return Arrays.stream(Model.values()).filter(predicate).collect(Collectors.toList());
    }

    private final int id;
    private final String name;
    private final int pixel;

    Model(int id, String name, int pixel) {
        this.id = id;
        this.name = name;
        this.pixel = pixel;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPixel() {
        return pixel;
    }
}
