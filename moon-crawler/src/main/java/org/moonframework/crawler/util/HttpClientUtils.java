package org.moonframework.crawler.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.codecs.DefaultHttpRequestWriterFactory;
import org.apache.http.impl.nio.codecs.DefaultHttpResponseParserFactory;
import org.apache.http.impl.nio.conn.ManagedNHttpClientConnectionFactory;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.ManagedNHttpClientConnection;
import org.apache.http.nio.conn.NHttpConnectionFactory;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.springframework.core.io.ClassPathResource;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/6/3
 */
public class HttpClientUtils {

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";

    private static volatile CloseableHttpClient httpClient;
    private static volatile CloseableHttpAsyncClient httpAsyncClient;

    public static URI newInstance(String link) {
        try {
            return URI.create(link);
        } catch (IllegalArgumentException e) {
            try {
                URL url = new URL(link);
                return new URI(url.getProtocol(), url.getAuthority(), url.getPath(), url.getQuery(), null);
            } catch (MalformedURLException | URISyntaxException e1) {
                throw new IllegalArgumentException(e1);
            }
        }
    }

    public static void execute(HttpUriRequest request, FutureCallback<HttpResponse> callback) {
        getHttpAsyncClient().execute(request, callback);
    }

    public static void execute(HttpUriRequest request, Consumer<HttpResponse> completed, BiConsumer<HttpUriRequest, Exception> failed) {
        getHttpAsyncClient().execute(request, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse response) {
                completed.accept(response);
            }

            @Override
            public void failed(Exception ex) {
                failed.accept(request, ex);
            }

            @Override
            public void cancelled() {
            }
        });
    }

    public static <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) {
        try {
            return getHttpClient().execute(request, responseHandler);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static CloseableHttpAsyncClient getHttpAsyncClient() {
        if (httpAsyncClient == null) {
            synchronized (HttpClientUtils.class) {
                if (httpAsyncClient == null) {
                    httpAsyncClient = newHttpAsyncClient();
                }
            }
        }
        return httpAsyncClient;
    }

    //添加sslcontext
    public static SSLContext getSSLContext() {
        SSLContext sc = null;
        InputStream instream = null;
        String keyStorepass = "changeit";
        KeyStore trustStore = null;
        if (new ClassPathResource("cacerts").exists()) {
            try {
                trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                instream = new ClassPathResource("cacerts").getInputStream();
                trustStore.load(instream, keyStorepass.toCharArray());
                // CA和证书
                sc = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
                sc.init(null, null, null);

                // 构造 javax.net.ssl.TrustManager 对象
            /*TrustManagerFactory tmf =
                    TrustManagerFactory.getInstance("SunX509", "SunJSSE");
            tmf.init(trustStore);
            TrustManager tms [] = tmf.getTrustManagers();
            // 使用构造好的 TrustManager 访问相应的 https 站点
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tms, new java.security.SecureRandom());*/
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    instream.close();
                } catch (IOException e) {
                }
            }
        } else {
            sc = SSLContexts.createSystemDefault();
        }
        return sc;
    }

    public static CloseableHttpAsyncClient newHttpAsyncClient() {
        CloseableHttpAsyncClient httpClient;
        try {
            // Use a custom connection factory to customize the process of
            // initialization of outgoing HTTP connections. Beside standard connection
            // configuration parameters HTTP connection factory can define message
            // parser / writer routines to be employed by individual connections.
            NHttpConnectionFactory<ManagedNHttpClientConnection> connFactory = new ManagedNHttpClientConnectionFactory(
                    new DefaultHttpRequestWriterFactory(), new DefaultHttpResponseParserFactory(), HeapByteBufferAllocator.INSTANCE);

            // SSL context for secure connections can be created either based on
            // system or application specific properties.
            //SSLContext sslcontext = SSLContexts.createSystemDefault();
            SSLContext sslcontext = HttpClientUtils.getSSLContext();

            /*try {
                sslcontext.init(null,null, null);
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }*/
            // Use custom hostname verifier to customize SSL hostname verification.
            HostnameVerifier hostnameVerifier = new DefaultHostnameVerifier();

            // Create a registry of custom connection session strategies for supported
            // protocol schemes.
            Registry<SchemeIOSessionStrategy> sessionStrategyRegistry = RegistryBuilder.<SchemeIOSessionStrategy>create()
                    .register("http", NoopIOSessionStrategy.INSTANCE)
                    .register("https", new SSLIOSessionStrategy(sslcontext, hostnameVerifier))
                    .build();

            // Create I/O reactor configuration
            IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                    .setIoThreadCount(Runtime.getRuntime().availableProcessors())
                    .setConnectTimeout(60000)
                    .setSoTimeout(60000)
                    .build();

            // Create a custom I/O reactor
            ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);

            // Create a connection manager with custom configuration.
            PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(
                    ioReactor, connFactory, sessionStrategyRegistry, new SystemDefaultDnsResolver());
            // Configure total max or per route limits for persistent connections
            // that can be kept in the pool or leased by the connection manager.
            connManager.setMaxTotal(500);
            connManager.setDefaultMaxPerRoute(50);

            // Use custom cookie store if necessary.
            CookieStore cookieStore = new BasicCookieStore();
            // Use custom credentials provider if necessary.
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            // Create global request configuration
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setCookieSpec(CookieSpecs.DEFAULT)
                    .setExpectContinueEnabled(true)
                    .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                    .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                    .build();

            // Create an HttpClient with the given custom dependencies and configuration.
            httpClient = HttpAsyncClients.custom()
                    .setUserAgent(USER_AGENT)
                    .setConnectionManager(connManager)
                    .setDefaultCookieStore(cookieStore)
                    .setDefaultCredentialsProvider(credentialsProvider)
                    .setDefaultRequestConfig(defaultRequestConfig)
                    .build();
            httpClient.start();
        } catch (IOReactorException e) {
            throw new IllegalStateException(e);
        }
        return httpClient;
    }

    public static CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (HttpClientUtils.class) {
                if (httpClient == null) {
                    httpClient = newInstance();
                }
            }
        }
        return httpClient;
    }

    public static CloseableHttpClient newInstance() {
        // SSL context for secure connections can be created either based on
        // system or application specific properties.
        //SSLContext sslcontext = SSLContexts.createSystemDefault();
        SSLContext sslcontext = HttpClientUtils.getSSLContext();

        /*try {
            sslcontext.init(null,null, null);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }*/

        // Create a registry of custom connection socket factories for supported
        // protocol schemes.
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslcontext))
                .build();

        // Create a connection manager with custom configuration.
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry);
        // Configure total max or per route limits for persistent connections
        // that can be kept in the pool or leased by the connection manager.
        connManager.setMaxTotal(200);
        connManager.setDefaultMaxPerRoute(20);

        // Use custom cookie store if necessary.
        CookieStore cookieStore = new BasicCookieStore();

        // Create global request configuration
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setExpectContinueEnabled(true)
                .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                .setProxyPreferredAuthSchemes(Collections.singletonList(AuthSchemes.BASIC))
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setUserAgent(USER_AGENT)
                .setConnectionManager(connManager)
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();

        return httpClient;
    }

    /**
     * @return
     */
    private static DefaultConnectionKeepAliveStrategy defaultConnectionKeepAliveStrategy() {
        return new DefaultConnectionKeepAliveStrategy() {

            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                long keepAlive = super.getKeepAliveDuration(response, context);
                if (keepAlive == -1) {
                    // Keep connections alive 5 seconds if a keep-alive value
                    // has not be explicitly set by the server
                    keepAlive = 5000;
                }
                return keepAlive;
            }

        };
    }

}
