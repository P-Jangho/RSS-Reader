package jp.ac.jec.cm0135.rssreader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        // WebView 초기화
        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // WebViewClient 설정
        webView.setWebViewClient(new WebViewClient());

        // MainActivity에서 전달된 URL을 가져와서 로드
        String url = getIntent().getStringExtra("url");
        webView.loadUrl(url);
    }

    public void onBackPressed() {
        // WebView에서 뒤로 가기 버튼을 누르면 앱 내에서 뒤로 가기 동작 수행
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}