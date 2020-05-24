package com.jroomstudio.smartbookmarkeditor.webview;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jroomstudio.smartbookmarkeditor.Injection;
import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.local.BookmarksLocalDataSource;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.local.BookmarksLocalRepository;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.remote.BookmarksRemoteRepository;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebAddBookmarkPopupActivity extends AppCompatActivity {

    public static final String ADD_TITLE = "ADD_TITLE";
    public static final String ADD_URL = "ADD_URL";
    public static final String ADD_CATEGORY = "ADD_CATEGORY";

    // 북마크 데이터 소스
    private BookmarksLocalRepository mBookmarksLocalRepository;
    private BookmarksRemoteRepository mBookmarksRemoteRepository;

    // url, 카테고리, 타이틀
    private TextView tvCategory, tvUrl;
    private EditText etTitle;

    // 확인, 취소 버튼
    private Button btnComplete, btnCancel;

    // 액티비티 상태저장 Shared Preferences
    private SharedPreferences spActStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // dark 모드 상태 가져오기
        spActStatus = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = spActStatus.edit();
        editor.apply();
        if(!spActStatus.getBoolean("login_status",false)){
            // 게스트
            setupDarkTheme("dark_theme");
        }else{
            // 회원일때
            setupDarkTheme("member_dark_theme");
        }

        // 상태바 제거하고 전체화면 모드로
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.web_add_bookmark_popup_act);

        if(!spActStatus.getBoolean("login_status",false)){
            // 게스트 유저
            // 북마크 데이터 레포지토리 접근
            mBookmarksLocalRepository = Injection.provideBookmarksRepository(getApplicationContext());
        }else{
            // 회원 유저
            mBookmarksRemoteRepository = Injection.provideRemoteBookmarksRepository(spActStatus);
        }

        // 인텐트로 넘어온 데이터 셋팅
        getIntentData();

        // 버튼 리스너 생성
        buttonListener();

    }

    /**
     * 다크테마 셋팅
     **/
    void setupDarkTheme(String darkThemeKey){
        if(spActStatus.getBoolean(darkThemeKey,true)){
            setTheme(R.style.DarkAppPopup);
        }
    }

    void getIntentData(){
        // 카테고리 셋팅
        tvCategory = (TextView) findViewById(R.id.tv_category);
        tvCategory.setText(getIntent().getStringExtra(ADD_CATEGORY));
        // URL 주소 셋팅
        tvUrl = (TextView) findViewById(R.id.tv_url);
        tvUrl.setText(getIntent().getStringExtra(ADD_URL));
        // 타이틀 셋팅
        etTitle = (EditText) findViewById(R.id.et_title);
        etTitle.setText(getIntent().getStringExtra(ADD_TITLE));
    }

    // 버튼셋팅
    void buttonListener(){
        btnComplete = (Button) findViewById(R.id.btn_complete);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        // 저장
        btnComplete.setOnClickListener(v -> {
            urlPatternMatching(tvUrl.getText().toString());
        });
        // 취소
        btnCancel.setOnClickListener(v -> onBackPressed());
    }

    // 북마크 저장
    void saveBookmark(String faviconUrl){
        if(!spActStatus.getBoolean("login_status",false)){
            // 게스트 유저
            // favicon 추출하고 저장
            mBookmarksLocalRepository.getBookmarks(tvCategory.getText().toString(),
                    new BookmarksLocalDataSource.LoadBookmarksCallback() {
                        @Override
                        public void onBookmarksLoaded(List<Bookmark> bookmarks) {
                            // 1. 아이템 추가
                            // 1-1 해당 카테고리에 북마크가 있는경우
                            // position 값을 카테고리안의 북마크 사이즈 크기로 지정
                            // -> 해당 카테고리 전체 사이즈, 파비콘 url
                            addBookmark(bookmarks.size(),faviconUrl);

                        }

                        @Override
                        public void onDataNotAvailable() {
                            // 1-2. 카테고리 추가시 해당 카테고리에 북마크가 없는경우
                            // 해당 카테고리에 북마크가 없다면 position 0 으로 추가한다.
                            addBookmark(0,faviconUrl);
                        }
                    });
        }
        else{
            // 회원
            addBookmark(0,faviconUrl);
        }

    }

    // 북마크 저장
    private void addBookmark(int position, String faviconUrl){
        Bookmark bookmark = new Bookmark(
                Objects.requireNonNull(etTitle.getText().toString()),
                Objects.requireNonNull(tvUrl.getText().toString()),
                "WEB_VIEW",
                Objects.requireNonNull(tvCategory.getText().toString()),
                position,
                faviconUrl);
        if(!spActStatus.getBoolean("login_status",false)){
            // 게스트 유저
            // favicon 추출하고 저장
            mBookmarksLocalRepository.saveBookmark(bookmark);
        }else{
            // 회원 유저
            mBookmarksRemoteRepository.saveBookmark(bookmark);
        }
        Toast.makeText(this, "북마크를 추가하였습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * urlPatternMatchingCheck
     * - 정규표현식으로 url 주소를 검사하고 결과를 반환한다.
     * - 북마크 추가, 편집시 호출된다.
     * @param url - 입력된 url 주소
     **/
    private void urlPatternMatching(String url){
        // 2. 검사를 통과한 url 주소를 http , 도메인, 경로를 각각 추출한다.
        Pattern urlPattern = Pattern.compile("^(https?):\\/\\/([^:\\/\\s]+)((\\/[^\\s\\/]+)*)");
        Matcher mc = urlPattern.matcher(url);
        String faviconUrl;
        if(mc.matches()){
            // http 와 도메인에 favicon.ico 를 입력하여 url 완성
            faviconUrl = mc.group(1) + "://" + mc.group(2)+ "/favicon.ico";
            Log.e("favicon url -> ",faviconUrl);

            saveBookmark(faviconUrl);
            //String http = mc.group(1);
            //String domain = mc.group(2);
            //String route = mc.group(3);
        }else{
            Toast.makeText(this, "중복된 북마크입니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
