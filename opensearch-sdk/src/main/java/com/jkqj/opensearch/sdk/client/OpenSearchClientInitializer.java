package com.jkqj.opensearch.sdk.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Slf4j
public class OpenSearchClientInitializer {


    public static RestHighLevelClient buildRestClient(String host, int port) {
        return buildRestClientNoneSSL(host, port);
    }

    public static RestHighLevelClient buildRestClient(String userName, String password, String host, int port) {
        return buildRestClient(userName, password, host, port, "https");
    }

    public static RestHighLevelClient buildRestClient(String userName, String password, String host, int port, String scheme) {
        if (scheme.equalsIgnoreCase("https")) {
            return buildRestClientSSL(userName, password, host, port);
        }
        return buildRestClientNoneSSL(host, port);

    }

    private static RestHighLevelClient buildRestClientNoneSSL(String host, int port) {
        //Initialize the client with SSL and TLS enabled
        RestClientBuilder builder = RestClient.builder(new HttpHost(host, port, "http"));
        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }

    private static RestHighLevelClient buildRestClientSSL(String userName, String password, String host, int port) {
        final SSLContext ctx = getSslContext();

        X509TrustManager tm = new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] arg0,
                                           String arg1) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] arg0,
                                           String arg1) throws CertificateException {
            }
        };
        try {
            ctx.init(null, new TrustManager[]{tm}, null);
        } catch (KeyManagementException e) {
            log.error("ctx init error.", e);
            throw new RuntimeException(e);
        }
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(userName, password));

        //Initialize the client with SSL and TLS enabled
        RestClientBuilder builder = RestClient.builder(new HttpHost(host, port, "https")).
                setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider).setSSLContext(ctx)
                                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
                    }
                });
        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }

    private static SSLContext getSslContext() {
        SSLContext ctx;
        try {
            ctx = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            log.error("get tls error.", e);
            throw new RuntimeException(e);
        }
        return ctx;
    }


}
