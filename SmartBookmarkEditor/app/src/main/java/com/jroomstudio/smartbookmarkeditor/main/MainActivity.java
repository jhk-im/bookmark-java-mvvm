package com.jroomstudio.smartbookmarkeditor.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jroomstudio.smartbookmarkeditor.Injection;
import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.ViewModelHolder;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.category.Category;
import com.jroomstudio.smartbookmarkeditor.data.member.Member;
import com.jroomstudio.smartbookmarkeditor.data.member.MemberRemoteRepository;
import com.jroomstudio.smartbookmarkeditor.data.notice.NoticeLocalDatabase;
import com.jroomstudio.smartbookmarkeditor.data.notice.NoticeLocalRepository;
import com.jroomstudio.smartbookmarkeditor.databinding.MainNavViewContainerBinding;
import com.jroomstudio.smartbookmarkeditor.information.InformationActivity;
import com.jroomstudio.smartbookmarkeditor.login.LoginActivity;
import com.jroomstudio.smartbookmarkeditor.main.home.MainHomeFragment;
import com.jroomstudio.smartbookmarkeditor.main.home.MainHomeViewModel;
import com.jroomstudio.smartbookmarkeditor.main.home.item.BookmarkItemNavigator;
import com.jroomstudio.smartbookmarkeditor.main.home.item.CategoryItemNavigator;
import com.jroomstudio.smartbookmarkeditor.notice.NoticeActivity;
import com.jroomstudio.smartbookmarkeditor.popup.EditAddItemPopupActivity;
import com.jroomstudio.smartbookmarkeditor.util.ActivityUtils;
import com.jroomstudio.smartbookmarkeditor.util.AppExecutors;
import com.jroomstudio.smartbookmarkeditor.webview.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements MainHomeNavigator, CategoryItemNavigator, BookmarkItemNavigator, MainNavNavigator{

    // 액티비티 상태저장 Shared Preferences
    private SharedPreferences spActStatus;

    // 메인 네비게이션 뷰
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;

    // Activity request
    public static int LOGIN_COMPLETE = 1;

    // 프래그먼트 관리할 매니저
    private FragmentManager fragmentManager;

    /**
     * MainHomeFragment
     **/
    // 홈 프래그먼트 뷰모델 태그
    public static final String HOME_VM_TAG = "HOME_VM_TAG";
    // 홈 프래그먼트 뷰모델
    private MainHomeViewModel mViewModel;
    // 홈 프래그먼트
    private MainHomeFragment mainHomeFragment;
    // 아이템추가 or 편집 -> 스피너리스트로 전달할 카테고리 리스트 카운트
    private int mSelectCategoryCount;
    // 현재 선택된 카테고리
    private String mSelectCategory;

    /**
     * MainNavContainer
     **/
    // 태그
    public static final String NAV_VM_TAG = "NAV_VM_TAG";
    // 뷰모델
    private MainNavViewModel mNavViewModel;
    // 데이터 바이딩
    private MainNavViewContainerBinding mNavDataBinding;

    /**
     * 로그인 여부
     **/
    private MemberRemoteRepository mMemberRemoteRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 사용자 데이터 가져오기
        spActStatus = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = spActStatus.edit();
        editor.apply();
        if(!spActStatus.getBoolean("login_status",false)){
            // 게스트
            setupDarkTheme("dark_theme","push_notice");
        }else{
            // 회원일때
            setupDarkTheme("member_dark_theme","member_push_notice");
        }

        setContentView(R.layout.main_act);

        //툴바셋팅
        setupToolbar();

        // 네비게이션 뷰 데이터 바인딩
        View view = findViewById(R.id.drawer_include_layout);
        mNavDataBinding  = MainNavViewContainerBinding.bind(view);
        // 메인 네비게이션 뷰모델
        mNavViewModel = findOrCreateNavViewModel();
        mNavDataBinding.setViewmodel(mNavViewModel);
        mNavViewModel.onLoaded();
        //navigation drawer 셋팅
        setupNavigationDrawer();

        //홈 프래그먼트 생성
        getSelectedHome();

        // 로그인 되어있는 유저의 경우
        if(spActStatus.getBoolean("login_status",false)){
            loginCompleted(
                    new Member("",
                            "",
                            "",
                            "",
                            false,
                            false,
                            0),
                    false);
        }
    }

    @Override
    protected void onDestroy() {
        // 뷰모델의 ItemNavigator null 셋팅
        mViewModel.onActivityDestroyed();
        super.onDestroy();
    }

    /**
     * 다크테마 셋팅
    **/
    void setupDarkTheme(String darkThemeKey, String noticeKey){
        if(spActStatus.getBoolean(darkThemeKey,true)){
            setTheme(R.style.DarkAppTheme);
        }
        // firebase 구독 알림 설정
        if(spActStatus.getBoolean(noticeKey,true)){
            // fcm 구독추가
            FirebaseMessaging.getInstance().subscribeToTopic("notice");
        }else{
            // fcm 구독 끊기
            FirebaseMessaging.getInstance().unsubscribeFromTopic("notice");
        }
    }

    /**
     * Nav container 뷰모델
     **/
    private MainNavViewModel findOrCreateNavViewModel(){
        // ViewModelHolder(UI 없는 Fragment)
        // -> 뷰모델 생성 후 TAG 구분자인 MAIN_VM_TAG 을 입력하여 생성 또는 재활용
        @SuppressWarnings("unchecked")
        ViewModelHolder<MainNavViewModel> retainedViewModel =
                (ViewModelHolder<MainNavViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(NAV_VM_TAG);
        // 입력한 tag 의 뷰모델이 존재한다면
        if(retainedViewModel != null && retainedViewModel.getViewModel() != null){
            //getViewModel() 로 가져와 리턴한다.
            return retainedViewModel.getViewModel();
        }else{
            // 입력한 TAG 의 뷰모델이 없다면 뷰모델을 생성하고 ViewModelHolder 에 추가한다.
            // 로컬 데이터소스 생성, 액티비티 context 입력
            NoticeLocalDatabase database = NoticeLocalDatabase.getInstance(this);
            NoticeLocalRepository noticeLocalRepository = NoticeLocalRepository.
                    getInstance(new AppExecutors(), database.notificationsDAO());
            MainNavViewModel viewModel = new MainNavViewModel(
                    noticeLocalRepository,
                    this,
                    mMemberRemoteRepository = Injection.provideMemberRepository(spActStatus),
                    spActStatus,
                    getApplicationContext()
                    );
            // ViewModelHolder(UI 없는 Fragment) 생성
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    ViewModelHolder.createViewModelHolder(viewModel),
                    NAV_VM_TAG
            );
            return viewModel;
        }
    }
    /**
     * Home 프래그먼트 , 뷰모델 생성 메소드
     **/
    //Home 프래그먼트 생성
    void getSelectedHome(){
        // 프래그먼트 매니져
        fragmentManager= getSupportFragmentManager();
        // 프래그먼트 생성 및 재활용
        mainHomeFragment = findOrCreateViewFragment();
        // 프래그먼트의 뷰모델 생성 및 재활용
        mViewModel = findOrCreateViewModel();
        // 네비게이터 셋팅
        mViewModel.setNavigator(this);
        // 프래그먼트와 뷰모델 연결
        mainHomeFragment.setMainViewModel(mViewModel);
    }
    // 홈 프래그먼트 생성 또는 재활용
    @NonNull
    private MainHomeFragment findOrCreateViewFragment() {
        // Main 프래그먼트
        MainHomeFragment mainHomeFragment =
                (MainHomeFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if(mainHomeFragment == null){
            // 프래그먼트 생성
            mainHomeFragment = MainHomeFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    mainHomeFragment, R.id.content_frame);
        }
        return mainHomeFragment;
    }
    // 홈 프래그먼트 뷰모델 생성 또는 재활용
    private MainHomeViewModel findOrCreateViewModel() {
        // ViewModelHolder(UI 없는 Fragment)
        // -> 뷰모델 생성 후 TAG 구분자인 MAIN_VM_TAG 을 입력하여 생성 또는 재활용
        @SuppressWarnings("unchecked")
        ViewModelHolder<MainHomeViewModel> retainedViewModel =
                (ViewModelHolder<MainHomeViewModel>) getSupportFragmentManager()
                .findFragmentByTag(HOME_VM_TAG);
        // 입력한 tag 의 뷰모델이 존재한다면
        if(retainedViewModel != null && retainedViewModel.getViewModel() != null){
            //getViewModel() 로 가져와 리턴한다.
            return retainedViewModel.getViewModel();
        }else{
            // 입력한 TAG 의 뷰모델이 없다면 뷰모델을 생성하고 ViewModelHolder 에 추가한다.
            // 로컬 데이터소스 생성, 원격데이터소스 생성, 액티비티 context 입력
            MainHomeViewModel viewModel = new MainHomeViewModel(
                    Injection.provideBookmarksRepository(getApplicationContext()),
                    Injection.provideCategoriesRepository(getApplicationContext()),
                    getApplicationContext(),spActStatus
            );
            // ViewModelHolder(UI 없는 Fragment) 생성
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    ViewModelHolder.createViewModelHolder(viewModel),
                    HOME_VM_TAG
            );
            return viewModel;
        }
    }

    /**
     * 툴바 , 옵션메뉴 셋팅
     **/
    // 툴바셋팅 메소드
    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }
    // 옵션메뉴 Item 셀렉트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {// Open navigation drawer
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * DrawerLayout Navigation View 관련 메소드
     **/
    // Navigation Drawer 셋팅 메소드
    private void setupNavigationDrawer(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimary);
        // 개인정보처리방침 버튼 밑줄
        mNavDataBinding.btnNavPipp.setPaintFlags(
                mNavDataBinding.btnNavPipp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG
        );
        // 오픈소스라이선스 버튼 밑줄
        mNavDataBinding.btnNavOsl.setPaintFlags(
                mNavDataBinding.btnNavOsl.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG
        );
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        if(mNavigationView != null){
            if(!spActStatus.getBoolean("login_status",false)){
                // 게스트
                setupNavigationSwitch("dark_theme",
                        "push_notice",false);
            }else{
                // 회원
                setupNavigationSwitch("member_dark_theme",
                        "member_push_notice",true);
            }
        }
    }
    // Navigation 스위치 checkedChange Listener
    private void setupNavigationSwitch(String darkThemeKey, String noticeKey,boolean isLoginUser){

        // 다크테마 셋팅
        if(spActStatus.getBoolean(darkThemeKey,true)){
            mNavDataBinding.ivBtnHome.setImageResource(R.drawable.ic_home);
            mNavDataBinding.ivDarkTheme.setImageResource(R.drawable.ic_dark_theme);
            mNavDataBinding.ivNotice.setImageResource(R.drawable.ic_notice);
            mNavDataBinding.ivInfo.setImageResource(R.drawable.ic_info);
        }

        // 게스트
        // 로컬에 저장되어있는 switch 상태 정보 가져옴
        mNavDataBinding.switchDarkTheme.setChecked(spActStatus.getBoolean(darkThemeKey,true));
        mNavDataBinding.switchDarkTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isLoginUser){
                // 다크테마 원격 업데이트
                mNavViewModel.updateUserDataRepository(isChecked,
                        spActStatus.getBoolean(noticeKey,true),
                        spActStatus.getBoolean("login_status",false),true);
            }else{
                // dark 모드 상태 업데이트
                spActStatus = getSharedPreferences("user_data", MODE_PRIVATE);
                SharedPreferences.Editor editor = spActStatus.edit();
                editor.putBoolean(darkThemeKey,isChecked);
                editor.apply();
                mDrawerLayout.closeDrawers();
                finish();
                startActivity(new Intent(this, MainActivity.class));
            }
        });

        // 로컬에 저장되어있는 알림 switch 상태정보 가져옴
        mNavDataBinding.switchNotice.setChecked(spActStatus.getBoolean(noticeKey,true));
        mNavDataBinding.switchNotice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                Toast.makeText(this, "알림 메세지를 허용합니다.", Toast.LENGTH_SHORT).show();
                FirebaseMessaging.getInstance().subscribeToTopic("notice");
            }else{
                Toast.makeText(this, "알림 메세지를 차단합니다.", Toast.LENGTH_SHORT).show();
                FirebaseMessaging.getInstance().unsubscribeFromTopic("notice");
            }

            if(isLoginUser) {
                // 푸쉬노트 원격 업데이트
                mNavViewModel.updateUserDataRepository(
                        spActStatus.getBoolean(darkThemeKey,true),
                        isChecked,
                        spActStatus.getBoolean("login_status",false),false);
            }else{
                // 알림 상태 업데이트
                spActStatus = getSharedPreferences("user_data", MODE_PRIVATE);
                SharedPreferences.Editor editor = spActStatus.edit();
                editor.putBoolean(noticeKey,isChecked);
                editor.apply();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 로그인 성공 후
        if(resultCode == LOGIN_COMPLETE){
            // 토큰발행받기
            mNavViewModel.getTokenRemoteRepository();
        }
    }

    /**
     * MainHomeNavigator 오버라이드 메소드
     **/
    // 아이템 추가 팝업 띄우기 - 툴바 + 버튼
    @Override
    public void addNewItems(List<Category> categories) {
        Intent intent = new Intent(this, EditAddItemPopupActivity.class);
        // 인텐트 타입
        intent.setType(EditAddItemPopupActivity.ADD_ITEM);
        // 카테고리 TITLE 리스트
        intent.putStringArrayListExtra(EditAddItemPopupActivity.CATEGORY_LIST,
                setCategoriesTitle(categories));
        intent.putExtra(EditAddItemPopupActivity.SELECT_CATEGORY_COUNT,mSelectCategoryCount);
        intent.putExtra(EditAddItemPopupActivity.SELECT_CATEGORY,mSelectCategory);
        startActivity(intent);
    }
    // 카테고리 롱클릭하여 편집하기 (팝업)
    @Override
    public void editCategory(Category category,List<Category> categories) {
        Intent intent = new Intent(this, EditAddItemPopupActivity.class);
        // 편집할 카테고리 데이터 전달
        // INTENT 타입
        intent.setType(EditAddItemPopupActivity.EDIT_CATEGORY);
        // 카테고리 ID, TITLE
        intent.putExtra(EditAddItemPopupActivity.ID,category.getId());
        intent.putExtra(EditAddItemPopupActivity.TITLE,category.getTitle());
        // 카테고리 TITLE 리스트
        intent.putStringArrayListExtra(EditAddItemPopupActivity.CATEGORY_LIST,
                setCategoriesTitle(categories));
        intent.putExtra(EditAddItemPopupActivity.SELECT_CATEGORY_COUNT,mSelectCategoryCount);
        intent.putExtra(EditAddItemPopupActivity.SELECT_CATEGORY,mSelectCategory);
        startActivity(intent);
    }
    // 북마크 롱클릭하여 편집하기 (팝업)
    @Override
    public void editBookmark(Bookmark bookmark,List<Category> categories) {
        Intent intent = new Intent(this, EditAddItemPopupActivity.class);
        // 편집할 북마크 데이터 전달
        // 인텐트 타입
        intent.setType(EditAddItemPopupActivity.EDIT_BOOKMARK);
        // 북마크 ID
        intent.putExtra(EditAddItemPopupActivity.ID,bookmark.getId());
        intent.putExtra(EditAddItemPopupActivity.TITLE,bookmark.getTitle());
        intent.putExtra(EditAddItemPopupActivity.URL,bookmark.getUrl());
        // 카테고리 TITLE 리스트
        intent.putStringArrayListExtra(EditAddItemPopupActivity.CATEGORY_LIST,
                setCategoriesTitle(categories));
        intent.putExtra(EditAddItemPopupActivity.SELECT_CATEGORY_COUNT,mSelectCategoryCount);
        intent.putExtra(EditAddItemPopupActivity.SELECT_CATEGORY,mSelectCategory);
        startActivity(intent);
    }
    // 툴바 타이틀 변경하기
    @Override
    public void setToolbarTitle(String title) {
        ActionBar ab = getSupportActionBar();
        ab.setTitle(title);
    }
    // 카테고리 리스트 셋팅
    private ArrayList<String> setCategoriesTitle(List<Category> categories){
        // 카테고리가 있으면 카테고리 리스트를 보낸다.
        // 현재 선택되어있는 카테고리도 보낸다.
        ArrayList<String> title = new ArrayList<String>();
        if(categories != null){
            for(Category category : categories){
                title.add(category.getTitle());
                if(category.isSelected()){
                    mSelectCategoryCount = categories.indexOf(category);
                    mSelectCategory = category.getTitle();
                }
            }
        }
        return title;
    }

    /**
     * BookmarkItemNavigator , CategoryItemNavigator 오버라이드 메소드
     **/
    // 카테고리 셀렉트 구현
    @Override
    public void selectedCategory(Category category) {
        mViewModel.changeSelectCategory(category);
    }
    // 북마크 셀렉트 구현
    @Override
    public void selectedBookmark(Bookmark bookmark) {
        // 웹뷰로 이동하여 웹페이지 보여주는 것 구현하기
        // Log.e("selectedBookmark",bookmark.toString());
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("bookmarkId",bookmark.getId());
        intent.putExtra("bookmarkUrl",bookmark.getUrl());
        intent.putExtra("bookmarkCategory",bookmark.getCategory());
        startActivity(intent);
    }

    /**
     * MainNavNavigator 오버라이드 메소드
     **/
    // 로그인 버튼
    @Override
    public void onClickLogin() {
        // 게스트 유저일 때
        if(!spActStatus.getBoolean("login_status",false)){
            // 로그인 activity 로 이동
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent,LOGIN_COMPLETE);
        }else{
            // 회원일때
            loginOut(true);
        }
    }

    // 홈 버튼
    @Override
    public void onClickHome() {
        // 홈 프래그먼트
        getSelectedHome();
        mDrawerLayout.closeDrawers();
    }
    //알림 버튼
    @Override
    public void onClickNotice() {
        // 알림 액티비티로 이동
        Intent intent = new Intent(MainActivity.this, NoticeActivity.class);
        startActivity(intent);
    }
    // 개인정보처리방침 버튼
    @Override
    public void onClickPIPP() {
        Intent intent = new Intent(MainActivity.this, InformationActivity.class);
        intent.putExtra("TYPE","PIPP");
        startActivity(intent);
    }
    // 오픈소스 라이선스 버튼
    @Override
    public void onClickOSL() {
        Intent intent = new Intent(MainActivity.this, InformationActivity.class);
        intent.putExtra("TYPE","OSL");
        startActivity(intent);
    }

    // 로그인 성공
    @Override
    public void loginCompleted(Member member, boolean refresh) {

        if(refresh){
            SharedPreferences.Editor editor = spActStatus.edit();
            editor.putBoolean("member_dark_theme",member.isDarkTheme());
            editor.putBoolean("member_push_notice",member.isPushNotice());
            editor.apply();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }else{
            // 유저 이미지 셋팅
            Glide.with(this)
                    .load(spActStatus.getString("photo_url",""))
                    .error(R.drawable.logo)
                    .into(mNavDataBinding.ivProfileImage);
            // 유저 이메일 셋팅
            mNavDataBinding.tvUserEmail.setText(spActStatus.getString("member_email",""));
            mNavDataBinding.tvUserName.setText(spActStatus.getString("member_name",""));
            // 로그아웃
            mNavDataBinding.btnIsSign.setText(getString(R.string.logout_text));
        }
    }

    // 로그인 실패
    @Override
    public void loginOut(boolean logout) {
        SharedPreferences.Editor editor = spActStatus.edit();
        editor.putBoolean("login_status",false);
        editor.putString("auto_password", "");
        editor.putString("member_email", "");
        editor.putString("member_name", "");
        editor.putString("photo_url", "");
        editor.putBoolean("member_dark_theme",false);
        editor.putBoolean("member_push_notice",false);
        editor.putString("jwt","");
        editor.apply();
        finish();
        startActivity(new Intent(this, MainActivity.class));
        if(!logout){
            Toast.makeText(this, "중복된 이메일 입니다.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "로그아웃 하였습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 원격 데이터 업데이트 성공 시
    @Override
    public void updateRemoteData(Member member,boolean isDarkTheme) {
        // 알림 상태 업데이트
        spActStatus = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = spActStatus.edit();
        editor.putBoolean("member_dark_theme",member.isDarkTheme());
        editor.putBoolean("member_push_notice",member.isPushNotice());
        editor.apply();
        if(isDarkTheme){
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }


}
