package org.moonframework.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/1
 */
public class HtmlTests {

    public static void a(Elements elements) {
        for (Element element : elements) {
            a(element.children());
            System.out.println(element.tag() + "    " + element.text());
        }
    }

    public static void nodes(List<Node> nodes) {
        for (Node node : nodes) {
            nodes(node.childNodes());
            node(node);
        }
    }

    public static void node(Node node) {
        if (node instanceof TextNode) {
            TextNode textNode = (TextNode) node;
            System.out.println(textNode.text());
        } else if (node instanceof Element) {
            Element element = (Element) node;
            System.out.println();
        } else {
            System.out.println(node);
        }
    }


    public static void main(String[] args) {
        String html = "<div1><div2>    <div3><span1>dd</span>dd</div3>      <div4></div4></div2>  </div1>";

        Elements elements = Jsoup.parse(html).select("body>*");
        // a(elements);
        for (Element element : elements) {
            nodes(element.childNodes());
        }

        // System.out.println(select.html());
    }

}
