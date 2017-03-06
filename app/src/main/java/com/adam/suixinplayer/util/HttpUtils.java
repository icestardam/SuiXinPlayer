package com.adam.suixinplayer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2017/2/28.
 */
public class HttpUtils  {

    /**
     * 使用get方式联网获取输入流
     * @param path  URL 需要取得流的网址
     * @return  InputStream 输入流
     * @throws Exception
     */
    public  static InputStream getInputStream(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        InputStream is = connection.getInputStream();
        return  is;
    }

    /**
     * 将is转成String
     * @param is 输入流
     * @return
     */
    public static String is2String(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line=reader.readLine())!=null){
            sb.append(line);
        }
        String str = sb.toString();
        return str;
    }

}
