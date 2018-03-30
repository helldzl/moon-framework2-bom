package org.moonframework.web;

import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.QueryFieldOperator;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.Include;

import java.util.HashMap;
import java.util.Map;

import static org.moonframework.model.mybatis.criterion.QueryFieldOperator.fields;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/7/6
 */
public class ParameterTests {

    private static Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = new HashMap<>();
        map.put("filter[TopicsSchema.brandsImage:eq]", new String[]{"Yamaha,EV", "2box"});
        map.put("filter[Products.Brand:eq]", new String[]{"Yamaha,EV", "2box"});
        map.put("filter[price:between]", new String[]{"[20,50)", "(30,90)"});
        map.put("filter[id:in]", new String[]{"20,50,51"});
        map.put("filter[enabled]", new String[]{"1"});
        map.put("fields[topics]", new String[]{"id,UserName,topicCount,enabled"});
        map.put("fields[channels]", new String[]{"id,channelName,type"});
        map.put("include", new String[]{"products,brands,channels"});
        return map;
    }


    public static void main(String[] args) {
        Criterion criterion = QueryFieldOperator.criterion(getParameterMap());
        System.out.println(criterion);

//        System.out.println(Restrictions.and(
//                Restrictions.and(
//                        Restrictions.eq("enabled", 1),
//                        Restrictions.eq("b", 2)
//                ),
//                Restrictions.or(
//                        Restrictions.eq("c", 3),
//                        Restrictions.eq("d", 4)
//                )
//        ));

        System.out.println(fields(getParameterMap()));

        Include include = new Include(new String[]{"a", "b"});
        Restrictions.put(Include.class, include);

        System.out.println(Restrictions.get(Include.class));

    }

}
