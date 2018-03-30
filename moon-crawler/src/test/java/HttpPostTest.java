import org.moonframework.crawler.fetcher.Fetcher;
import org.moonframework.crawler.fetcher.HttpClientConnection;
import org.moonframework.crawler.parse.Parser;
import org.moonframework.crawler.storage.WebPage;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LiuKai on 2017/9/6.
 */
public class HttpPostTest {
    public static void main(String[] args) {
        WebPage page = new WebPage();
        String postParamsText = "action=extra_blog_feed_get_content&et_load_builder_modules=1&blog_feed_nonce=8939c125b8&to_page=2&posts_per_page=12&order=desc&orderby=date&categories=1&show_featured_image=1&blog_feed_module_type=standard&et_column_type=&show_author=&show_categories=1&show_date=1&show_rating=&show_more=&show_comments=&date_format=Y%2Fm%2Fd&content_length=excerpt&hover_overlay_icon=&use_tax_query=1&tax_query%5B0%5D%5Btaxonomy%5D=category&tax_query%5B0%5D%5Bterms%5D%5B%5D=1&tax_query%5B0%5D%5Bfield%5D=term_id&tax_query%5B0%5D%5Boperator%5D=IN&tax_query%5B0%5D%5Binclude_children%5D=true";
        page.setPostParamsText(postParamsText);
        Map<String, String> postHeaderGroup = new HashMap<>();
        postHeaderGroup.put("origin", "https://news.guitarchina.com");
        postHeaderGroup.put("accept-encoding", "gzip, deflate");
        postHeaderGroup.put("accept-language", "zh-CN,zh;q=0.8");
        postHeaderGroup.put("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
        postHeaderGroup.put("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        postHeaderGroup.put("accept", "*/*");
        postHeaderGroup.put("referer", "https://news.guitarchina.com/?cat=1");
        postHeaderGroup.put("authority", "news.guitarchina.com");
        postHeaderGroup.put("x-requested-with", "XMLHttpRequest");
        page.setPostHeaderGroup(postHeaderGroup);
        try {
            page.setPostUri(new URI("https://news.guitarchina.com/wp-admin/admin-ajax.php"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        page.setHttpMethod("post");
        HttpClientConnection httpClientConnection = new HttpClientConnection();
        httpClientConnection.request(new Fetcher(), new Parser(), page);
    }
}
