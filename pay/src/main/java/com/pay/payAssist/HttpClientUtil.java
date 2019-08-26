package com.pay.payAssist;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: daihw
 * @date: 2018/7/13
 * @time: 17:26
 * @description: 连接池方式 HttpClient
 **/
public class HttpClientUtil {
    protected static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    /**
     * 默认字符集
     */
    public static final String DEFAULT_CHARST = "utf-8";
    /**
     * 监控连接间隔
     */
    public static final long RELEASE_CONNECTION_WAIT_TIME = 5000;
    /**
     * 连接超时
     */
    public static final int CONNECT_TIMEOUT = 5 * 1000;
    /**
     * 从连接池里获取连接超时
     */
    public static final int CONNECTION_REQUEST_TIMEOUT = 500;
    /**
     * 获取请求数据的时间
     */
    public static final int SOCKET_TIMEOUT = 20 * 1000;

    private static PoolingHttpClientConnectionManager httpClientConnectionManager;

    private static RequestConfig requestConfig;

    private static RedirectStrategy redirectStrategy;

    private static ConnectionKeepAliveStrategy connectionKeepAliveStrategy;

    private static MyResponseHandler responseHandler;

    static {
        SSLContext sslContext = SSLContexts.createDefault();
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
                .build();
        //创建连接池
        httpClientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        //设置连接池最大数量
        httpClientConnectionManager.setMaxTotal(200);
        //设置单个路由的最大连接数，表示单个域名的最大连接数
        httpClientConnectionManager.setDefaultMaxPerRoute(50);

        //请求设置
        requestConfig = RequestConfig.copy(RequestConfig.DEFAULT)
                //忽略cookis
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                //连接超时
                .setConnectTimeout(CONNECT_TIMEOUT)
                //请求数据超时
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                //获取请求数据的时间
                .setSocketTimeout(SOCKET_TIMEOUT)
                //允许转发
                .setRedirectsEnabled(true)
                .build();

        //设置keepalive时长
        connectionKeepAliveStrategy = ((httpResponse, httpContext) -> 20 * 1000);

        responseHandler = new MyResponseHandler();
        // 重定向策略初始化
        redirectStrategy = new LaxRedirectStrategy();

        new IdleConnectionMonitorThread(httpClientConnectionManager).start();
    }

    public static CloseableHttpClient getHttpClient(){
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(httpClientConnectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setKeepAliveStrategy(connectionKeepAliveStrategy)
                .setRedirectStrategy(redirectStrategy)
                .build();
        return httpClient;
    }

    /**
     * 设置通道请求属性
     * @param request
     */
    private static void setCommonHeaders(AbstractHttpMessage request) {
        request.addHeader("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        request.addHeader("Connection", "keep-alive");
        request.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
        request.addHeader("Accept-Encoding", "gzip, deflate, br");
        // User-Agent最好随机换
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.81 Safari/537.36");
    }

    /**
     * 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
     */
    public static String getParamSrc(Map<String, String> paramsMap) {
        if (paramsMap == null || paramsMap.size() == 0){
            return null;
        }
        StringBuffer paramstr = new StringBuffer();
        for (String key : paramsMap.keySet()) {
            String pvalue = paramsMap.get(key);
            //空值不传递
            if (null != pvalue && "" != pvalue) {
                //签名原串，不url编码
                paramstr.append(key + "=" + pvalue + "&");
            }
        }
        if (!StringUtils.isEmpty(paramstr)){
            // 去掉最后一个&
            String result = paramstr.substring(0, paramstr.length() - 1);
            return result;
        }
        return paramstr.toString();
    }

    /**
     * get请求
     * @param urlString
     * @param params
     * @param headers
     * @return
     */
    public static String doGet(String urlString, Map<String, String> params, Map<String, String>... headers){
        return doGet(urlString, params, DEFAULT_CHARST, headers);
    }

    /**
     * get请求
     * @param urlString
     * @param params
     * @param charset
     * @param headers
     * @return
     */
    public static String doGet(String urlString, Map<String, String> params, String charset, Map<String, String>... headers){
        String html = "";
        HttpGet httpGet = null;
        urlString = urlString.trim();
        try {
            // 参数设置
            if (null == urlString || urlString.isEmpty() || !urlString.startsWith("http")) {
                logger.error("请求错误，url异常，url:{}", urlString);
                return html;
            }
            if (null != params && !params.isEmpty()){
                String query = getParamSrc(params);
                if (!StringUtils.isEmpty(query)){
                    urlString = urlString + "?" + query;
                }
            }
            URL url = new URL(urlString);
            URI uri = new URI(url.getProtocol(), url.getAuthority(), url.getPath(), url.getQuery(), null);
            httpGet = new HttpGet(uri);
            setCommonHeaders(httpGet);
            // 额外的header
            if (headers != null && headers.length > 0) {
                for (Map.Entry<String, String> header : headers[0].entrySet()) {
                    if (httpGet.containsHeader(header.getKey())) {
                        httpGet.setHeader(header.getKey(), header.getValue());
                    } else {
                        httpGet.addHeader(header.getKey(), header.getValue());
                    }
                }
            }
            HttpClient httpClient = getHttpClient();

            MyResponseHandler myResponseHandler = responseHandler;
            if (!DEFAULT_CHARST.equalsIgnoreCase(charset)){
                myResponseHandler = new MyResponseHandler(charset);
            }
            html = httpClient.execute(httpGet, myResponseHandler);
        } catch (Exception e) {
            logger.error("请求错误， url:{}", urlString, e);
        } finally {
            httpGet.abort();
        }
        return html;
    }

    /**
     * get请求
     * @param urlString
     * @param params
     * @param headers
     * @return
     */
    public static String doGet(String urlString, String params, Map<String, String>... headers){
        return doGet(urlString, params, DEFAULT_CHARST, headers);
    }

    /**
     * get请求
     * @param urlString
     * @param params
     * @param charset
     * @param headers
     * @return
     */
    public static String doGet(String urlString, String params, String charset, Map<String, String>... headers){
        String html = "";
        HttpGet httpGet = null;
        urlString = urlString.trim();
        try {
            // 参数设置
            if (null == urlString || urlString.isEmpty() || !urlString.startsWith("http")) {
                logger.error("请求错误，url异常，url:{}", urlString);
                return html;
            }
            if (!StringUtils.isEmpty(params)){
                urlString = urlString + "?" + params;
            }
            URL url = new URL(urlString);
            URI uri = new URI(url.getProtocol(), url.getAuthority(), url.getPath(), url.getQuery(), null);
            httpGet = new HttpGet(uri);
            setCommonHeaders(httpGet);
            // 额外的header
            if (headers != null && headers.length > 0) {
                for (Map.Entry<String, String> header : headers[0].entrySet()) {
                    if (httpGet.containsHeader(header.getKey())) {
                        httpGet.setHeader(header.getKey(), header.getValue());
                    } else {
                        httpGet.addHeader(header.getKey(), header.getValue());
                    }
                }
            }
            HttpClient httpClient = getHttpClient();

            MyResponseHandler myResponseHandler = responseHandler;
            if (!DEFAULT_CHARST.equalsIgnoreCase(charset)){
                myResponseHandler = new MyResponseHandler(charset);
            }
            html = httpClient.execute(httpGet, myResponseHandler);
        } catch (Exception e) {
            logger.error("请求错误， url:{}", urlString, e);
        } finally {
            httpGet.abort();
        }
        return html;
    }

    /**
     * http post请求 表单类型 默认 UTF-8
     * @param urlString
     * @param params
     * @param headers
     * @return
     */
    public static String doPost(String urlString, Map<String, String> params, Map<String, String>... headers) {
        return doPost(urlString, params, DEFAULT_CHARST, headers);
    }

    /**
     * http post请求 表单类型
     * @param urlString
     * @param params
     * @param charset
     * @param headers
     * @return
     */
    public static String doPost(String urlString, Map<String, String> params, String charset, Map<String, String>... headers) {
        String html = "";
        HttpPost httpPost = null;
        urlString = urlString.trim();
        try {
            // 参数设置
            if (null == urlString || urlString.isEmpty() || !urlString.startsWith("http")) {
                logger.error("请求错误，url异常，url:{}", urlString);
                return html;
            }
            URL url = new URL(urlString);
            URI uri = new URI(url.getProtocol(), url.getAuthority(), url.getPath(), url.getQuery(), null);
            httpPost = new HttpPost(uri);
            setCommonHeaders(httpPost);
            // 额外的header
            if (headers != null && headers.length > 0) {
                for (Map.Entry<String, String> header : headers[0].entrySet()) {
                    if (httpPost.containsHeader(header.getKey())) {
                        httpPost.setHeader(header.getKey(), header.getValue());
                    } else {
                        httpPost.addHeader(header.getKey(), header.getValue());
                    }
                }
            }
            if (params != null && params.size() > 0) {
                List<BasicNameValuePair> nvps = new ArrayList<>();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                // 这里设置的是返回结果编码,大多数都是UTF-8,如果乱码,可以查看网页的meta中的content的编码.如果是GBK,这里改为GBK即可.
                // 这里entity只能读取一次,想要读取多次,百度一下.
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, charset));
            }
            HttpClient httpClient = getHttpClient();

            MyResponseHandler myResponseHandler = responseHandler;
            if (!DEFAULT_CHARST.equalsIgnoreCase(charset)){
                myResponseHandler = new MyResponseHandler(charset);
            }
            html = httpClient.execute(httpPost, myResponseHandler);
        } catch (Exception e) {
            logger.error("请求错误， url:{}", urlString, e);
        } finally {
            httpPost.abort();
        }
        return html;
    }

    /**
     *  http post请求 stream流类型 默认 UTF-8
     *  适用application/json、application/xml之类
     * @param urlString
     * @param postData
     * @param headers
     * @return
     */
    public static String doPost(String urlString, String postData, Map<String, String>... headers) {
        return doPost(urlString, postData, DEFAULT_CHARST, headers);
    }
    /**
     * http post请求 stream流类型
     * 适用application/json、application/xml之类
     * @param urlString
     * @param postData
     * @param headers
     * @return
     */
    public static String doPost(String urlString, String postData, String charset, Map<String, String>... headers) {
        String html = "";
        HttpPost httpPost = null;
        urlString = urlString.trim();
        try {
            // 参数设置
            if (null == urlString || urlString.isEmpty() || !urlString.startsWith("http")) {
                logger.error("请求错误，url异常，url:{}", urlString);
                return html;
            }
            URL url = new URL(urlString);
            URI uri = new URI(url.getProtocol(), url.getAuthority(), url.getPath(), url.getQuery(), null);
            httpPost = new HttpPost(uri);
            setCommonHeaders(httpPost);
            // 额外的header
            if (headers != null && headers.length > 0) {
                for (Map.Entry<String, String> header : headers[0].entrySet()) {
                    if (httpPost.containsHeader(header.getKey())) {
                        httpPost.setHeader(header.getKey(), header.getValue());
                    } else {
                        httpPost.addHeader(header.getKey(), header.getValue());
                    }
                }
            }

            StringEntity entity = new StringEntity(postData, charset);
            httpPost.setEntity(entity);

            MyResponseHandler myResponseHandler = responseHandler;
            if (!DEFAULT_CHARST.equalsIgnoreCase(charset)){
                myResponseHandler = new MyResponseHandler(charset);
            }
            HttpClient httpClient = getHttpClient();
            html = httpClient.execute(httpPost, responseHandler);
        } catch (Exception e) {
            logger.error("请求错误，url:{}", urlString, e);
        } finally {
            httpPost.abort();
        }
        return html;
    }

    /**
     * 响应处理类,处理返回结果
     * @author anonymous
     */
    public static class MyResponseHandler implements ResponseHandler<String> {

        private String charset = "utf-8";

        public MyResponseHandler(){
        }

        public MyResponseHandler(String charset){
            this.charset = charset;
        }

        @Override
        public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
            try {
                // 返回状态码
                int statusCode = response.getStatusLine().getStatusCode();
                switch (statusCode) {
                    case 200:
                        //inStream.close()才会释放连接到连接池
                        return EntityUtils.toString(response.getEntity(), charset);
                    case 400:
                        logger.error("返回400错误代码，请求无效.");
                        break;
                    case 403:
                        logger.error("返回403错误代码，资源不可用.");
                        break;
                    case 404:
                        logger.error("返回404错误代码，无法找到指定资源地址.");
                        break;
                    case 503:
                        logger.error("返回503错误代码，服务不可用.");
                        break;
                    case 504:
                        logger.error("返回504错误代码，网关超时.");
                        break;
                    default:
                        logger.error("未处理的错误,code:{}" + statusCode);
                }

            } finally {
                if (response != null) {
                    try {
                        ((CloseableHttpResponse) response).close();
                    } catch (IOException e) {
                        logger.error("关闭响应错误.", e);
                    }
                }
            }
            return "";
        }
    }


    /**
     * 释放链接请求线程
     */
    public static class IdleConnectionMonitorThread extends Thread {

        private static volatile boolean shutdown = false;
        private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;

        public IdleConnectionMonitorThread(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
            this.poolingHttpClientConnectionManager = poolingHttpClientConnectionManager;
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(RELEASE_CONNECTION_WAIT_TIME);
                        // Close expired connections
                        poolingHttpClientConnectionManager.closeExpiredConnections();
                        // that have been idle longer than 30 sec
                        poolingHttpClientConnectionManager.closeIdleConnections(30, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException ex) {
                logger.error("释放连接池连接出错.");
            }
        }
    }


}
