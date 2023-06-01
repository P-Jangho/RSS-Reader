package jp.ac.jec.cm0135.rssreader;

import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class AsyncHttpRequest implements Runnable{
    private Handler handler;
    private MainActivity mainActivity;
    private String urlStr;
    private String resStr;

    public AsyncHttpRequest(Handler handler, MainActivity mainActivity, String urlStr) {
        this.handler = handler;
        this.mainActivity = mainActivity;
        this.urlStr = urlStr;
    }

    @Override
    public void run() {
        Log.i("RSSReader", "BackGroundTask start...");

        resStr = "取得に失敗しました。";
        HttpsURLConnection connection = null;

        try{
            URL url = new URL(urlStr);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            resStr = inputStreamToString(connection.getInputStream());
        }catch (Exception e) {
            e.printStackTrace();
            Log.e("AsyncHttpRequest", e.toString());
        }finally {
            if(connection != null){
                connection.disconnect();
            }
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                onPostExecute();
            }
        });
    }

    private void onPostExecute() {
        Log.i("Android205", "onPostExecute start...");
        //非同期処理後に実行する処理を技術する

        ArrayList<RssItem> ary = JsonHelper.parseJson(resStr);
        for(RssItem tmp: ary){
            mainActivity.adapter.add(tmp);
        }
        ListView list = mainActivity.findViewById(R.id.resultList);
        list.setAdapter(mainActivity.adapter);
    }

    public AsyncHttpRequest(Handler handler) {
        this.handler = handler;
    }

    private String inputStreamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
}
