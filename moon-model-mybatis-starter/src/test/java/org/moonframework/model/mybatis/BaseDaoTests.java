package org.moonframework.model.mybatis;

import org.moonframework.model.mybatis.support.Association;

import java.util.HashMap;
import java.util.Map;

public class BaseDaoTests {

    public static void main(String[] args) {
        Map<String, Object> o = new HashMap<>();
        o.put("a", Association.DELAYED);
    }

}
