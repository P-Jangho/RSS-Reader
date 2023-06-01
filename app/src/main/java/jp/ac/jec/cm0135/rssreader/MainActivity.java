package jp.ac.jec.cm0135.rssreader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    protected RowModelAdapter adapter;
    private ExecutorService executorService;
    private TextView txtLastUpdated;
    private String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        selectedCategory = intent.getStringExtra("category");

        txtLastUpdated = findViewById(R.id.txtLastUpdated);
        Looper mainLooper = getMainLooper();
        Handler handler = HandlerCompat.createAsync(mainLooper);

        executorService = Executors.newSingleThreadExecutor();
        adapter = new RowModelAdapter(this);

        ArrayList<RssItem> ary = JsonHelper.parseJson(getData());
        for(RssItem item: ary){
            adapter.add(item);
        }

        String retrievalTime = getCurrentDateTime();
        txtLastUpdated.setText(retrievalTime);

        ListView list = findViewById(R.id.resultList);
        list.setAdapter(adapter);
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                // ここにリストの項目がクリックされた時の処理を書く
//                RssItem item = (RssItem) adapterView.getAdapter().getItem(i);
//                Uri uri = Uri.parse((item.getLink()));
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
////                Toast.makeText(MainActivity.this, item.getLink(), Toast.LENGTH_SHORT).show();
//            }
//         });
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https");
        uriBuilder.authority("jec-cm-linux2020.lolipop.io");
        uriBuilder.path("test.php");
        uriBuilder.appendQueryParameter("url", "https://news.yahoo.co.jp/rss/categories/sports.xml");

        Log.i("MainActivity", uriBuilder.build().toString());
        AsyncHttpRequest asyncHttpRequest = new AsyncHttpRequest(handler, MainActivity.this, uriBuilder.toString() );
        executorService.submit(asyncHttpRequest);
    }

    private String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd(E) HH:mm:ss", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private String getData(){
        String json = "";
        BufferedReader br = null;
        try{
            InputStream in = null;
            if (selectedCategory.equals("スポーツニュース")) {
                in = getAssets().open("rssSport.json");
            }else if (selectedCategory.equals("ITニュース") ) {
                in = getAssets().open("rssIT.json");
            }else if (selectedCategory.equals("芸能ニュース")) {
                in = getAssets().open("rssEnt.json");
            }
            br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null){
                sb.append(line);
            }
            json = sb.toString();

        }catch (Exception e){
            Log.e("MainActivity", Log.getStackTraceString(e));
        }finally {
            try{
                if(br != null) br.close();
            }catch (IOException e){
                Log.e("MainActivity", Log.getStackTraceString(e));
            }
        }
        return json;
    }

    class RowModelAdapter extends ArrayAdapter {
        public RowModelAdapter(Context context) {
            super(context, R.layout.row_item);
        }

        @NonNull
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            RssItem item = (RssItem) getItem(position);
            if(convertView == null){
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.row_item, null);
            }
            ImageButton btnNext = convertView.findViewById(R.id.btnNext);
            if(item != null){
                TextView txtTitle = convertView.findViewById(R.id.txtTitle);
                if(txtTitle != null){
                    txtTitle.setText(item.getTitle());
                }
                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = item.getLink();

                        // WebActivity를 시작하여 WebView로 기사 페이지를 표시
                        Intent intent = new Intent(MainActivity.this, WebActivity.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                });
                TextView txtTime = convertView.findViewById(R.id.txtTime);
                if(txtTime != null){
                    String inputDate = item.getPubDate();
                    String outputFormat = "yyyy-MM-dd(E) HH:mm:ss";

                    SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
                    SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat, Locale.US);

                    try {
                        // 입력된 문자열을 Date 객체로 파싱
                        Date date = inputDateFormat.parse(inputDate);

                        // 출력 형식으로 포맷팅
                        String formattedDate = outputDateFormat.format(date);
                        txtTime.setText(formattedDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            return convertView;
        }
    }
}