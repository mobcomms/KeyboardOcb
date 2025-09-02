package com.enliple.keyboard.network;

import android.content.Context;
import android.util.Log;

import com.enliple.keyboard.activity.SharedPreference;
import com.enliple.keyboard.common.KeyboardLogPrint;
import com.enliple.keyboard.ui.common.Key;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class RequestHttpURLConnection {

    public String request(Context context, String _url, Map<String, String> _params, String requestMethod) {

        KeyboardLogPrint.d("RequestHttpURLConnection URL : " + _url);
        KeyboardLogPrint.d("RequestHttpURLConnection Params : " + _params);
        KeyboardLogPrint.d("RequestHttpURLConnection RequestMethod : " + requestMethod);

        HttpURLConnection urlConn = null;

        StringBuffer sbParams = new StringBuffer();

        if (_params == null)
            sbParams.append("");
        else {
            boolean isAnd = false;
            String key;
            String value;

            for (Map.Entry<String, String> parameter : _params.entrySet()) {
                key = parameter.getKey();
                value = parameter.getValue();

                if (isAnd)
                    sbParams.append("&");

                sbParams.append(key).append("=").append(value);

                if (!isAnd)
                    if (_params.size() >= 2)
                        isAnd = true;
            }
        }

        try {
            URL url = new URL(_url);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setReadTimeout(10000);
            urlConn.setConnectTimeout(15000);
            urlConn.setRequestMethod(requestMethod);
            urlConn.setRequestProperty ("token", SharedPreference.getString(context, Key.KEY_TOKEN));
            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);

            PrintWriter pw = new PrintWriter(new OutputStreamWriter(urlConn.getOutputStream()));
            pw.write(sbParams.toString());
            pw.flush(); // 출력 스트림을 flush. 버퍼링 된 모든 출력 바이트를 강제 실행.
            pw.close(); // 출력 스트림을 닫고 모든 시스템 자원을 해제.

            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            BufferedReader reader;
            if (urlConn.getContentType().toUpperCase().contains("EUC-KR")) {
                reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"EUC-KR"));
            } else if (urlConn.getContentType().toUpperCase().contains("UTF-8")){
                reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"UTF-8"));
            } else {
                reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"UTF-8"));
            }

            String line;
            String page = "";

            while ((line = reader.readLine()) != null) {
                page += line;
            }

            KeyboardLogPrint.d("RequestHttpURLConnection _url : " + _url + " / page : " + page);
//            int maxLogSize = 1000;
//            for(int i = 0; i <= page.length() / maxLogSize; i++) {
//                int start = i * maxLogSize;
//                int end = (i+1) * maxLogSize;
//                end = end > page.length() ? page.length() : end;
//                KeyboardLogPrint.d("RequestHttpURLConnection page : " + page.substring(start, end));
//            }

            return page;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }
        return null;
    }
}