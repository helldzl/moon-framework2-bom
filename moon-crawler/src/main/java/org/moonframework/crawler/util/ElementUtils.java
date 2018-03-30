package org.moonframework.crawler.util;

import org.jsoup.nodes.Element;

import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by quzile on 2016/8/30.
 */
public class ElementUtils {

    public static void iterate(Iterator<? extends Element> iterator, Consumer<? super Element> consumer) {
        while (iterator.hasNext()) {
            Element element = iterator.next();
            consumer.accept(element);
            if (!element.children().isEmpty())
                iterate(element.children().iterator(), consumer);
        }
    }

    public static void iterate(Iterator<? extends Element> iterator, Element parent, BiConsumer<? super Element, ? super Element> consumer) {
        while (iterator.hasNext()) {
            Element element = iterator.next();
            consumer.accept(parent, element);
            if (!element.children().isEmpty())
                iterate(element.children().iterator(), element, consumer);
        }
    }

}
