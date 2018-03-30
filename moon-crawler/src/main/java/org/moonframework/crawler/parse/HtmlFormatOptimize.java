package org.moonframework.crawler.parse;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LiuKai on 2017/10/31.
 * HTML格式优化
 */
public class HtmlFormatOptimize {
    private Map<String, String> replaces = new HashMap<>();
    private Whitelist whitelist;


    //添加你要替换的字符，和被替换的字符
    private HtmlFormatOptimize addReplaces(String key, String value) {
        Validate.notNull(key);
        Validate.notNull(value);
        replaces.put(key, value);
        return this;
    }

    //删除你要替换的字符，和被替换的字符
    private HtmlFormatOptimize deleteReplaces(String key) {
        Validate.notNull(key);
        replaces.remove(key);
        return this;
    }

    private static HtmlFormatOptimize basicFormat() {
        return new HtmlFormatOptimize(Whitelist.relaxed().removeTags("a").addAttributes("img", "src").addAttributes("span", "style").addAttributes("p", "style").addAttributes("img", "style")).addReplaces("\n", "").addReplaces("&nbsp;", "").addReplaces("&lt;", "").addReplaces("&gt;", "");
    }

    public static HtmlFormatOptimize basicFormat(Whitelist whitelist) {
        return new HtmlFormatOptimize(whitelist).addReplaces("\n", "").addReplaces("&nbsp;", "").addReplaces("&lt;", "").addReplaces("&gt;", "");
    }

    public static HtmlFormatOptimize basicFormat(Map<String, String> replaces, Whitelist whitelist) {
        return new HtmlFormatOptimize(replaces, whitelist).addReplaces("\n", "").addReplaces("&nbsp;", "").addReplaces("&lt;", "").addReplaces("&gt;", "");
    }

    public HtmlFormatOptimize() {
    }

    private HtmlFormatOptimize(Map<String, String> replaces, Whitelist whitelist) {
        this.replaces = replaces;
        this.whitelist = whitelist;
    }

    private HtmlFormatOptimize(Whitelist whitelist) {
        this.whitelist = whitelist;
    }

    private Elements formatOptimize(String html1) {
        if (!replaces.isEmpty()) {
            for (String key : replaces.keySet()) {
                html1 = html1.replace(key, replaces.get(key));
            }
        }
        String html = Jsoup.clean(html1, whitelist);
        Document document = Jsoup.parse(html);
        Elements elements = document.select("body>*");
        String newHtml = "";
        for (Element element : elements) {
            if (element.text().length() == 0 && element.childNodes().size() == 0 && element.tag().getName().equals("img")) {
                newHtml = newHtml + element.outerHtml();
            } else {
                newHtml = newHtml + element.outerHtml();
            }
        }
        return Jsoup.parse(newHtml).select("body>*");
    }

    private String removeMultiStage(String html) {
        Elements elements = formatOptimize(html);
        StringBuilder html001 = new StringBuilder();
        String html001Str="";
        for (Element element : elements) {
            StringBuilder elementHtml = new StringBuilder();
            //img处理
            if (element.tag().getName().equals("img")) {
                elementHtml.append("<").append(element.tag()).append(" ").append(element.attributes()).append(">");
            }
            //1、text为空 text是元素及元素下级都没有text
            else if (element.text().length() == 0 && element.childNodes().size() == 0) {
            } else if (element.childNodes().size() != 0) {
                StringBuilder elementHtml001 = new StringBuilder();
                // for (Element element1 : element.children()) {
                for (Object element1 : element.childNodes()) {
                    if (element1 instanceof TextNode && !((TextNode) element1).text().equals(" ")) {
                        elementHtml001.append("<").append(element.tag()).append(" ").append(element.attributes()).append(">");
                        break;
                    }
                    if (element1 instanceof Element) {
                        if (element.tag() != ((Element) element1).tag()) {
                            elementHtml001.append("<").append(element.tag()).append(" ").append(element.attributes()).append(">");
                            break;
                        }
                    }
                }
                for (Object element1 : element.childNodes()) {
                    if (element1 instanceof TextNode && !((TextNode) element1).text().equals(" ")) {
                        elementHtml001.append(((TextNode) element1).text());
                    } else {
                        elementHtml001.append(removeMultiStage(((Element) element1).outerHtml()));
                    }
                }
                String elementHtml002;
                elementHtml002 = Jsoup.parse(elementHtml001.toString()).select("body>*").outerHtml();
                elementHtml.append(elementHtml002);
            } else if (element.text().length() != 0 && element.childNodes().size() == 0) {
                elementHtml.append(element.outerHtml());
            }
            html001.append(elementHtml.toString());
            html001Str = Jsoup.parse(html001.toString()).select("body>*").outerHtml();
        }
        html001Str = html001Str.replace("\n", "");
        return html001Str;
    }

    public static void main(String[] args) {
        String html = "<div>&nbsp;&nbsp;</div><div>11</div><span style=\"11\">222333</span>" +
                "<div><div><div><div>11</div>22</div></div><p>pp</p></div><p>pp</p>" +
                "<img src=\"http://static.ingping.com/zx/images/ingping_logo/sc_logo-1.0.0.png\" height=\"45px\">";
        System.out.println(HtmlFormatOptimize.basicFormat().addReplaces("1", "").removeMultiStage(html));
        System.out.println(HtmlFormatOptimize.basicFormat().removeMultiStage(html));
    }
}
