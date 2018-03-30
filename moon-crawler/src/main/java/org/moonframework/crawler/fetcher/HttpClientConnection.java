package org.moonframework.crawler.fetcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.moonframework.crawler.parse.Parser;
import org.moonframework.crawler.storage.WebPage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/7/11
 */
public class HttpClientConnection implements HttpConnection {

    protected static final Log logger = LogFactory.getLog(HttpClientConnection.class);

    private CloseableHttpAsyncClient httpClient;

    public HttpClientConnection(CloseableHttpAsyncClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpClientConnection() {
    }

    @Override
    public <T extends WebPage> void request(Fetcher fetcher, Parser parser, T page) {
        if (page.getHttpMethod() == null || page.getHttpMethod().toLowerCase().equals("get")) {
            doGet(fetcher, parser, page);
        } else {
            doPost(fetcher, parser, page);
        }
    }

    //Post执行的方法
    private void doPost(Fetcher fetcher, Parser parser, WebPage page) {
        long start = System.currentTimeMillis();
        //httpClient=getHttpClient();
        /*++++++++++++++++++++*/
        HttpPost request = new HttpPost(page.getPostUri());
        if (page.getPostHeaderGroup().size() != 0) {
            Map<String, String> postHeaderGroup = page.getPostHeaderGroup();
            postHeaderGroup.forEach(request::setHeader);
        }

        //设置参数 传入的是字符串类型的
        if (page.getPostParamsText() != null) {
            request.setConfig(RequestConfig.DEFAULT);
            EntityBuilder entityBuilder = EntityBuilder.create().setText(page.getPostParamsText());
            HttpEntity Entity = entityBuilder.build();
            request.setEntity(Entity);
        }
        //设置参数 传入的是键值对的参数类型
        if (page.getPostParamsMap() != null && page.getPostParamsMap().size() != 0) {
            List<NameValuePair> list = new ArrayList<>();
            Iterator iterator = page.getPostParamsMap().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> elem = (Map.Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
            }
            if (list.size() > 0) {
                UrlEncodedFormEntity entity = null;
                try {
                    entity = new UrlEncodedFormEntity(list, page.getCharset());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                request.setEntity(entity);
            }
        }
        execute(httpClient, fetcher, parser, page, request, start);

    }

    //Get执行的方法
    private void doGet(Fetcher fetcher, Parser parser, WebPage page) {
        long start = System.currentTimeMillis();
        //httpClient=getHttpClient();
        HttpGet request = new HttpGet(page.getUri());
        if (page.getAgencyIp() != null && page.getAgencyIpPort() != null) {
            HttpHost proxy = new HttpHost(page.getAgencyIp(), page.getAgencyIpPort());
            RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
            request.setConfig(config);
        }
        execute(httpClient, fetcher, parser, page, request, start);
    }


    private void execute(CloseableHttpAsyncClient httpClient, Fetcher fetcher, Parser parser, WebPage page, HttpUriRequest request, long start) {
        httpClient.execute(request, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse response) {
                try {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        String result = entity != null ? EntityUtils.toString(entity, page.getCharset()) : null;
                        //在抓取公众号的时候，需要处理视频的URL，进行替换
                        result=result.replace("v.qq.com/iframe/preview.html","v.qq.com/iframe/player.html");
                        result=result.replaceAll("&amp;width=\\d{0,}&amp;height=\\d{0,}&amp;auto=0","&auto=0");
                        long time = System.currentTimeMillis() - start;
                        page.setStatusCode(status);
                        page.setHtml(result);
                        page.setFetchTime(time);
                        parser.execute(page);
                    }
                    if (logger.isInfoEnabled()) {
                        logger.info(String.format("Time: (%s)ms -> %s -> %s", page.getFetchTime(), request.getRequestLine(), response.getStatusLine()));
                    }
                } catch (IOException e) {
                    if (logger.isErrorEnabled()) {
                        logger.error("error", e);
                    }
                } finally {
                    fetcher.release();
                }
            }

            @Override
            public void failed(Exception e) {
                fetcher.release();
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Retry: (%s), %s failed", page.getRetry(), request.getRequestLine()), e);
                }

                if (!(e instanceof UnknownHostException) && page.getRetry() < 3) {
                    page.setRetry(page.getRetry() + 1);
                    fetcher.add(page);
                }
            }

            @Override
            public void cancelled() {
                fetcher.release();
                if (logger.isInfoEnabled()) {
                    logger.info(request.getRequestLine() + " cancelled");
                }
            }
        });
    }
}

