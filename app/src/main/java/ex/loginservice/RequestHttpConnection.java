package ex.loginservice;

import android.content.ContentValues;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

public class RequestHttpConnection {

    public String request(String _url, ContentValues _params) {

        //Http URL Connection 참조변수.
        HttpURLConnection urlConn = null;

        // URL 뒤에 붙여서 보낼 파라미터.
        StringBuffer sbParams = new StringBuffer();


        if (_params == null)
            sbParams.append("");
        else {
            boolean isAnd = false;

            String key;
            String value;

            for (Map.Entry<String, Object> parameter : _params.valueSet()) {
                key = parameter.getKey();
                value = parameter.getValue().toString();

                if (isAnd)
                    sbParams.append("&");

                sbParams.append(key).append("=").append(value);

                if (!isAnd)
                    if (_params.size() >= 2)
                        isAnd = true;

            }
        }

        //2. URL 커넥션을 통해 Web 데이터 가져옴

        try {
            URL url = new URL(_url);
            urlConn = (HttpURLConnection) url.openConnection();

            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("Accept-Charset", "UTF-8");
            urlConn.setDoOutput(false);

            //2-2
            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d("HTTP_OK", "연결 요청 실패");
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "utf-8"));

            String line;
            String page = "";

            while ((line = reader.readLine()) != null) {
                page += line;
            }

            return page;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }


        return null;


    }
}