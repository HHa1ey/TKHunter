package com.tkteam.utils;

import com.tkteam.bean.Response;
import com.tkteam.controller.Controller;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.HashMap;

public class HttpTool {


    private static HttpURLConnection getConn(String url) throws NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException, IOException {
        SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
        TrustManager[] trustManagers = new TrustManager[]{new TrustCert()};
        sslContext.init(null, trustManagers, new SecureRandom());
        HostnameVerifier hostnameVerifier = (s, sslSession) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        URL url_obj = new URL(url);
        HttpURLConnection url_conn = (HttpURLConnection) url_obj.openConnection();

        Proxy proxy = (Proxy) Controller.setProxy.get("proxy");
        if (proxy != null){
            url_conn = (HttpURLConnection)url_obj.openConnection(proxy);
        }
        url_conn.setRequestProperty("User-Agent", UserAgentTools.getRandomUA());
        url_conn.setConnectTimeout(10000);
        url_conn.setUseCaches(false);
        url_conn.setDoOutput(true);
        url_conn.setDoInput(true);
        url_conn.setInstanceFollowRedirects(false);
        return url_conn;
    }

    //返回数据流处理
    public static String streamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuffer stringBuffer = new StringBuffer();
        String line;
        while ((line=bufferedReader.readLine())!=null){
            stringBuffer.append(line);
        }
        return stringBuffer.toString();
    }

    public static Response get(String url, HashMap<String, String> headers) throws NoSuchAlgorithmException, IOException, NoSuchProviderException, KeyManagementException {
        Response response;
        HttpURLConnection connection = getConn(url);
        connection.setRequestMethod("GET");
        response = getResponse(connection);
        return response;
    }

    public static Response post(String url,HashMap<String,String> headers, String postdata) throws NoSuchAlgorithmException, IOException, NoSuchProviderException, KeyManagementException {
        Response response;
        HttpURLConnection connection = getConn(url);
        connection.setRequestMethod("POST");
        for (String key : headers.keySet()) {
            connection.setRequestProperty(key, headers.get(key));
        }
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(postdata.getBytes());
        outputStream.flush();
        outputStream.close();
        response = getResponse(connection);
        return response;
    }


    public static Response getResponse(HttpURLConnection connection) throws IOException {
        Response response = new Response(0,null,null);
        connection.connect();
        response.setCode(connection.getResponseCode());
        response.setHeader(connection.getHeaderFields().toString());
        response.setText(streamToString(connection.getInputStream()));
        return response;
    }
}
