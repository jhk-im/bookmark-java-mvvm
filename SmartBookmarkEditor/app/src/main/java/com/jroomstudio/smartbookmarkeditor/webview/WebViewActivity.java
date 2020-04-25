package com.jroomstudio.smartbookmarkeditor.webview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.jroomstudio.smartbookmarkeditor.R;

public class WebViewActivity extends AppCompatActivity {

    private WebView testWebview;
    private WebSettings mWebSettings;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_act);


        // 웹뷰시작
        testWebview = (WebView) findViewById(R.id.test_web_view);
        // 새창 안뜨도록
        testWebview.setWebViewClient(new WebViewClient());
        // 웹뷰 세부사항 등록
        mWebSettings = testWebview.getSettings();
        mWebSettings.setJavaScriptEnabled(true); // 웹페이지 자바스클비트 허용 여부
        mWebSettings.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        mWebSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        mWebSettings.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부
        mWebSettings.setSupportZoom(false); // 화면 줌 허용 여부
        mWebSettings.setBuiltInZoomControls(false); // 화면 확대 축소 허용 여부
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠 사이즈 맞추기
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부
        mWebSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부

        testWebview.loadUrl(getIntent().getStringExtra("bookmarkUrl")); // 웹뷰에 표시할 웹사이트 주소, 웹뷰 시작

    }
}
