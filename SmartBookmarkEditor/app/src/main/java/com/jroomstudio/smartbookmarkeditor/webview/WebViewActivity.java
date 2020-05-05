package com.jroomstudio.smartbookmarkeditor.webview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.jroomstudio.smartbookmarkeditor.Injection;
import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksDataSource;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksRepository;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

public class WebViewActivity extends AppCompatActivity {

    // 북마크 데이터 소스
    private BookmarksRepository mBookmarksRepository;

    // 액티비티 상태저장 Shared Preferences
    private SharedPreferences spActStatus;

    // 웹뷰와 웹뷰 셋팅
    private WebView mWebView;
    private WebSettings mWebSettings;
    // 스크롤뷰
    private NestedScrollView mNestedScrollView;

    // 액션바
    private ActionBar mActionBar;

    // url edit text
    private EditText urlEditText;
    // 현재 카테고리
    private String category;
    // 현재 ID
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // dark 모드 상태 가져오기
        spActStatus = getSharedPreferences("act_status", MODE_PRIVATE);
        SharedPreferences.Editor editor = spActStatus.edit();
        editor.apply();

        // 다크모드일 경우 다크모드로 변경
        if(spActStatus.getBoolean("dark_mode",true)){
            setTheme(R.style.DarkAppTheme);
        }
        setContentView(R.layout.web_view_act);

        // 북마크 데이터 레포지토리 접근
        mBookmarksRepository = Injection.provideBookmarksRepository(getApplicationContext());

        // 웹뷰
        mWebView = (WebView) findViewById(R.id.web_view);
        // url edit text
        urlEditText = (EditText) findViewById(R.id.et_url);
        urlEditText.setText(getIntent().getStringExtra("bookmarkUrl"));
        setupUrlEditText();

        // 스크롤뷰
        mNestedScrollView = (NestedScrollView) findViewById(R.id.nested_scroll);

        // 카테고리, id
        category = getIntent().getStringExtra("bookmarkCategory");
        id = getIntent().getStringExtra("bookmarkId");


        // 툴바셋팅
        setupToolbar();

        // 웹뷰셋팅
        setupWebView();

    }

    // 웹뷰 셋팅
    @SuppressLint("SetJavaScriptEnabled")
    void setupWebView(){
        // 웹뷰 셋팅
        // 새창 안뜨도록
        mWebView.setWebViewClient(new webClient());
        // 웹뷰 세부사항 등록
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true); // 웹페이지 자바스클비트 허용 여부
        mWebSettings.setSupportMultipleWindows(true); // 새창 띄우기 허용 여부
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        mWebSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        mWebSettings.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부
        mWebSettings.setSupportZoom(false); // 화면 줌 허용 여부
        mWebSettings.setBuiltInZoomControls(false); // 화면 확대 축소 허용 여부
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠 사이즈 맞추기
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부
        mWebSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부
        loadURL();
    }
    void loadURL(){
        mWebView.loadUrl(urlEditText.getText().toString());
        urlEditText.clearFocus();
    }

    // edit text setting
    void setupUrlEditText(){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        // key board enter 리스너
        urlEditText.setOnEditorActionListener((v, actionId, event) -> {
           // 키보드의 확인버튼을 누르게 되면
            if(actionId == EditorInfo.IME_ACTION_DONE){
               loadURL();
               imm.hideSoftInputFromWindow(urlEditText.getWindowToken(), 0);
           }
            return false;
        });

        // text change 리스너
        urlEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력되는 텍스트에 변화가 있을 때
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력이 끝났을 때
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 입력하기 전에 입력값이 비워져있으면
                if(urlEditText.getText().toString().equals("")){
                    // https:// 를 추가한다.
                    urlEditText.setText(R.string.https);
                    // 커서 뒤로이동
                    urlEditText.setSelection(urlEditText.getText().length());
                }
            }
        });
    }

    // 툴바셋팅
    @SuppressLint("SetTextI18n")
    void setupToolbar(){
        // 동적 툴바 셋팅
        Toolbar activeToolbar = (Toolbar) findViewById(R.id.active_toolbar);
        setSupportActionBar(activeToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(false);

        // 툴바 셋팅
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 동적 툴바 옵션메뉴 셋팅
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.web_view_menu,menu);
        return true;
    }

    /**
     * 옵션메뉴 버튼 셀렉트 리스너
     **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home : // 뒤로가기버튼
                if(mWebView.canGoBack()){
                    // 뒤로갈 페이지가 있으면
                    mWebView.goBack();
                }else{
                    // 더이상 페이지가 없으면 종료
                    finish();
                }
                break;
            case R.id.web_view_menu_close : // 종료버튼
                finish();
                break;
            case R.id.web_view_menu_refresh : // refresh 버튼
                if(Objects.equals(mActionBar.getTitle(), "로딩중")){
                    Toast.makeText(this, "페이지 로딩중 입니다.", Toast.LENGTH_SHORT).show();
                }else{
                    // 새로고침
                    loadURL();
                }
                break;
            case R.id.web_view_menu_bookmark : // 북마크 버튼
                if(Objects.equals(mActionBar.getTitle(), "로딩중")){
                    Toast.makeText(this, "페이지 로딩중 입니다.", Toast.LENGTH_SHORT).show();
                }else{
                    // 카테고리에 포함된 북마크 url 중복체크 후 저장진행
                    getBookmarksInCategoryOverlapCheck();
                }
                break;
            case R.id.web_view_menu_share : // 공유버튼
                if(Objects.equals(mActionBar.getTitle(), "로딩중")){
                    Toast.makeText(this, "페이지 로딩중 입니다.", Toast.LENGTH_SHORT).show();
                }else{
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, urlEditText.getText().toString());
                    sendIntent.setType("text/plain");
                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    startActivity(shareIntent);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 현재 카테고리 내부에 추가하려는 url 이 존재하는지 체크한다.
     **/
    void getBookmarksInCategoryOverlapCheck(){
        mBookmarksRepository.getBookmarks(category,
                new BookmarksDataSource.LoadBookmarksCallback() {
            @Override
            public void onBookmarksLoaded(List<Bookmark> bookmarks) {
                for(Bookmark bookmark : bookmarks){
                    if(bookmark.getUrl().equals(urlEditText.getText().toString())){
                        // 있는경우
                        Toast.makeText(WebViewActivity.this,
                                "카테고리에 이미 존재하는 url 입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                addBookmarkPopup();
            }

            @Override
            public void onDataNotAvailable() {
                // 카테고리 없으면 바로추가하는 팝업 실행
                addBookmarkPopup();
            }
        });
    }

    /**
     * 북마크 생성팝업을 띄운다.
    **/
    void addBookmarkPopup(){
        Intent intent = new Intent(WebViewActivity.this,
                WebAddBookmarkPopupActivity.class);
        intent.putExtra(WebAddBookmarkPopupActivity.ADD_TITLE,mActionBar.getTitle());
        intent.putExtra(WebAddBookmarkPopupActivity.ADD_URL,urlEditText.getText().toString());
        intent.putExtra(WebAddBookmarkPopupActivity.ADD_CATEGORY,category);
        startActivity(intent);
    }


    /**
     * web url 접속시 load, start, finish, error 로 나뉘어 상황별로 호출됨
     *
     * shouldOverrideUrlLoading
     * - 다른 앱을 열겨나 없을경우 마켓에서 찾는것을 구현
     * - 이것이 구현되어있지 않으면 웹뷰에서 다른앱을 실행 할 때 에러를 표시하게됨
     **/
    private class webClient extends WebViewClient{

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            //Log.e("load",view.getUrl());
            //Log.e("load",view.getTitle());
            //Log.e("load",request.toString());
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            //Log.e("start",view.getTitle());
            // 액션바 타이틀 loading...
            mActionBar = getSupportActionBar();
            mActionBar.setTitle(R.string.loading);
            urlEditText.setText(view.getUrl());
            view.refreshDrawableState();
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //Log.e("finish",view.getTitle());
            // 페이지가 에러없이 로드되면 url 과 타이틀 업데이트
            mActionBar = getSupportActionBar();
            mActionBar.setTitle(view.getTitle());
            mNestedScrollView.setScrollY(0);
            view.refreshDrawableState();
        }

        // 다른앱을 찾아서 열때 try catch
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url != null && url.startsWith("intent://")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                    if (existPackage != null) {
                        startActivity(intent);
                    } else {
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                        marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                        startActivity(marketIntent);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (url != null && url.startsWith("market://")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    if (intent != null) {
                        startActivity(intent);
                    }
                    return true;
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            view.loadUrl(url);
            return false;
        }

    }

}
