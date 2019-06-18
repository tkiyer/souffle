package org.souffle.utils;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.souffle.standard.QueryException;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Do network request. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/3/5 下午2:23
 * @see
 * @since JDK1.7
 */
public class Network {

    private final static Logger LOGGER = LoggerFactory.getLogger(Network.class);

    public enum Protocol {
        HTTP,
        HTTPS
    }

    public final static Protocol[] SUPPORT_PROTOCOLS = new Protocol[] {
        Protocol.HTTP, Protocol.HTTPS
    };

    private final static int DEFAULT_TIMEOUT = 5 * 60 * 1000;  // 5min timeout

    private final static long RETRY_INTERVAL_MILLIS = 30 * 1000L;   // 30sec retry interval

    private String url;

    private Protocol protocol;

    private CloseableHttpClient client;

    public Network(String url) {
        this.url = url;
        String strProtocol = url.substring(0, url.indexOf(":"));
        this.protocol = Protocol.valueOf(strProtocol.toUpperCase());
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                                                            .setConnectTimeout(DEFAULT_TIMEOUT)
                                                            .setSocketTimeout(DEFAULT_TIMEOUT)
                                                            .setConnectionRequestTimeout(DEFAULT_TIMEOUT)
                                                            .build();
        switch (this.protocol) {
            case HTTPS:
                this.client = createHttpsClient(defaultRequestConfig);
                break;
            case HTTP:
                this.client = createHttpClient(defaultRequestConfig);
                break;
        }
    }

    public boolean verify() {
        boolean isOk = false;
        for (Protocol supportProtocol : SUPPORT_PROTOCOLS) {
            if (supportProtocol == this.protocol) {
                isOk = true;
                break;
            }
        }
        Objects.requireNonNull(this.client, "Http client init error.");
        return isOk;
    }

    public void destroy() {
        if (null != this.client) {
            try {
                this.client.close();
            } catch (IOException ignored) {
            }
        }
    }

    public void doHttpPost(Map<String, Object> params, int retryTimes) {
        int alreadyRetryTimes = 0;
        do {
            HttpPost httpPost = new HttpPost(url);
            if (!params.isEmpty()) {
                List<NameValuePair> nvps = new ArrayList<>();
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue() + ""));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, Charset.forName("UTF-8")));
            }
            try {
                CloseableHttpResponse response = client.execute(httpPost);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    break;
                }
            } catch (IOException e) {
                LOGGER.warn("Execute http(s) request error. already retry " + alreadyRetryTimes + "times.", e);
                alreadyRetryTimes++;
                try {
                    Thread.sleep(RETRY_INTERVAL_MILLIS);
                } catch (InterruptedException ignored) {
                }
            } finally {
                httpPost.releaseConnection();
            }
        } while (alreadyRetryTimes < retryTimes);
    }

    private CloseableHttpClient createHttpsClient(RequestConfig defaultRequestConfig) {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            SSLContext sslContext = SSLContexts.custom()
                                                .loadTrustMaterial(keyStore, (chain, authType) -> true)
                                                .build();
            return HttpClients.custom()
                                .setDefaultRequestConfig(defaultRequestConfig)
                                .setSslcontext(sslContext)
                                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                                .build();
        } catch (KeyStoreException | NoSuchAlgorithmException | KeyManagementException e) {
            throw new QueryException("Create https client error.", e);
        }
    }

    private CloseableHttpClient createHttpClient(RequestConfig defaultRequestConfig) {
        return HttpClients.custom()
                            .setDefaultRequestConfig(defaultRequestConfig)
                            .build();
    }
}
