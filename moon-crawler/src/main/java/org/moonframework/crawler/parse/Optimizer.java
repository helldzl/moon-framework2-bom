package org.moonframework.crawler.parse;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.moonframework.crawler.storage.WebPage;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * <p>文档优化器</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2016/9/9
 */
public class Optimizer {

    private static final HtmlCompressor compressor = new HtmlCompressor();

    static {
        // default replace all multiple whitespace characters with single spaces.
        // Then compress remove all inter-tag whitespace characters <code>&amp;nbsp;</code>
        compressor.setRemoveIntertagSpaces(true);
    }

    public static String removeHtml(String str) {
        if (str == null)
            return null;
        return compressor.compress(Jsoup.clean(str, Whitelist.none()));
    }

    /**
     * <p>优化文档结构, 将相对链接补全为绝对链接</p>
     *
     * @param document document
     * @param page     page
     * @return Document
     */
    public Document optimize(Document document, WebPage page) {
        String host = page.getHost();
        String uri = page.getUri().toString();

        // 链接补全:优化a标签
        optimize(page, document.select("a[href]"), "href");

        // 延迟加载图片文档优化
        Elements elements = document.select("img[src]");
        elements.stream()
                .filter(element -> !element.hasAttr("src")&&element.hasAttr("data-original") && !"".equals(element.attr("data-original")))
                .forEach(element -> element.attr("src", element.attr("data-original")));

        // 链接补全:优化img标签
        optimize(page, elements, "src");
        return document;
    }

    /**
     * <p>优化文档结构, 将相对链接补全为绝对链接</p>
     *
     * @param page     page
     * @param elements elements
     * @param attr     attribute
     */
    public void optimize(WebPage page, Elements elements, String attr) {
        String host = page.getHost();
        URI uri = page.getUri();
        String link = page.getUri().toString();
        elements.forEach(element -> {
            String s = element.attr(attr).replaceAll("(\\.\\./|\\./)", "/");
            boolean hasQuery = s.startsWith("?");
            boolean hasFragment = s.startsWith("#");
            if (hasQuery || hasFragment) {
                try {
                    int queryIndex = s.indexOf("?");
                    int fragmentIndex = s.indexOf("#");
                    String query=null;
                    //新增一种特殊情况“#？”相互连接在一起。
                    if (queryIndex!=-1&&fragmentIndex!=-1&&queryIndex==fragmentIndex+1){
                        query=null;
                    }else {
                        query= queryIndex != -1 ? s.substring(queryIndex + 1, fragmentIndex == -1 ? s.length() : fragmentIndex) : uri.getQuery();
                    }
                    String fragment = fragmentIndex != -1 ? s.substring(fragmentIndex + 1) : uri.getFragment();
                    element.attr(attr, new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), query, fragment).toString());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else if ("/".equals(s)) {
                element.attr(attr, host);
            } else if (s.matches("/[^/].*") && host != null) {
                element.attr(attr, host + s);
            } else if (s.matches("//.*")) {
                element.attr(attr, URI.create(host).getScheme() + ":" + s);
            } else if (!s.matches("(?i)http.*")) {
                element.attr(attr, link.substring(0, link.lastIndexOf("/") + 1) + s);
            }
        });
    }

}
