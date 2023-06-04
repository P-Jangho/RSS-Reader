package jp.ac.jec.cm0135.rssreader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.annotation.SuppressLint;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class Main2Activity extends AppCompatActivity {
    private TextView txtLastUpdated;
    private TextView title;
    private String selectedCategory;
    private TextView txtContents;
    private String[] splitContent;
    private String[] splitContentURL;
    private Button btnPrev;
    private Button btnNext;
    private ImageButton btnURL;
    private int currentIndex;
    private String url;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();

        selectedCategory = intent.getStringExtra("category");
        title = findViewById(R.id.title);
        txtContents = findViewById(R.id.txtContents);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnURL = findViewById(R.id.btnURL);

        txtLastUpdated = findViewById(R.id.txtLastUpdated);
        Looper mainLooper = getMainLooper();
        Handler handler = HandlerCompat.createAsync(mainLooper);

        ArrayList<RssItem> ary = JsonHelper.parseJson(getData());
        StringBuffer sb = new StringBuffer();
        StringBuffer sbURL = new StringBuffer();
        for(RssItem item: ary){
            sb.append(item.getTitle() + "\n");

            String inputDate = item.getPubDate();
            String outputFormat = "yyyy-MM-dd(E) HH:mm:ss";

            SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat, Locale.US);
            sbURL.append(item.getLink()).append("\n");

            try {
                // 입력된 문자열을 Date 객체로 파싱
                Date date = inputDateFormat.parse(inputDate);

                // 출력 형식으로 포맷팅
                String formattedDate = outputDateFormat.format(date);
                sb.append(formattedDate).append("\n");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            sb.append("------");
        }
        splitContent = sb.toString().split("------");
        splitContentURL = sbURL.toString().split("\n");
        txtContents.setText(splitContent[0]);
        url = splitContentURL[0];
        currentIndex = 0;
        btnPrev.setEnabled(false);
        btnNext.setEnabled(true);

        btnURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // WebActivity를 시작하여 WebView로 기사 페이지를 표시
                Intent intent = new Intent(Main2Activity.this, WebActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        String retrievalTime = getCurrentDateTime();
        txtLastUpdated.setText(retrievalTime);

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentIndex > 0) {
                    Log.i("aaa", "aaa = " + url);
                    currentIndex -= 1;
                    txtContents.setText(splitContent[currentIndex]);
                    url = splitContentURL[currentIndex];
                }

                if (currentIndex == 0) {
                    btnPrev.setEnabled(false);  // 버튼 비활성화
                } else {
                    btnPrev.setEnabled(true);  // 버튼 활성화
                }
                btnNext.setEnabled(true);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentIndex < splitContent.length) {
                    Log.i("aaa", "aaa = " + url);
                    currentIndex += 1;
                    txtContents.setText(splitContent[currentIndex]);
                    url = splitContentURL[currentIndex];
                }

                if (currentIndex == splitContent.length - 1) {
                    btnNext.setEnabled(false);  // 버튼 비활성화
                } else {
                    btnNext.setEnabled(true);  // 버튼 활성화
                }
                btnPrev.setEnabled(true);
            }
        });
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
                title.setText("Sports News");
            }else if (selectedCategory.equals("ITニュース") ) {
                in = getAssets().open("rssIT.json");
                title.setText("IT News");
            }else if (selectedCategory.equals("芸能ニュース")) {
                in = getAssets().open("rssEnt.json");
                title.setText("Entertainment News");
            }
            br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null){
                sb.append(line);
            }
            json = sb.toString();

        }catch (Exception e){
            Log.e("Main2Activity", Log.getStackTraceString(e));
        }finally {
            try{
                if(br != null) br.close();
            }catch (IOException e){
                Log.e("Main2Activity", Log.getStackTraceString(e));
            }
        }
        return json;
    }
}