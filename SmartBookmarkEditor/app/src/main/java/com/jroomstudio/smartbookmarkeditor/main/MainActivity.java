package com.jroomstudio.smartbookmarkeditor.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.jroomstudio.smartbookmarkeditor.Injection;
import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.ViewModelHolder;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.category.Category;
import com.jroomstudio.smartbookmarkeditor.main.home.MainHomeFragment;
import com.jroomstudio.smartbookmarkeditor.main.home.MainHomeViewModel;
import com.jroomstudio.smartbookmarkeditor.main.home.item.BookmarkItemNavigator;
import com.jroomstudio.smartbookmarkeditor.main.home.item.CategoryItemNavigator;
import com.jroomstudio.smartbookmarkeditor.popup.EditAddItemPopupActivity;
import com.jroomstudio.smartbookmarkeditor.util.ActivityUtils;
import com.jroomstudio.smartbookmarkeditor.webview.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements MainHomeNavigator, CategoryItemNavigator, BookmarkItemNavigator {

    // 액티비티 상태저장 Shared Preferences
    private SharedPreferences spActStatus;

    // 메인 네비게이션 뷰
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;

    // 현재 활성화된 프래그먼트
    private Fragment currentFragment;
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
    // 네비게이션뷰 내부 버튼
    private ConstraintLayout btnHome, btnUser, btnNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // dark 모드 상태 가져오기
        spActStatus = getSharedPreferences("act_status", MODE_PRIVATE);
        SharedPreferences.Editor editor = spActStatus.edit();
        editor.apply();

        // 다크모드일 경우 다크모드로 변경
        if(spActStatus.getBoolean("dark_mode",false)){
            setTheme(R.style.DarkAppTheme);
        }
        setContentView(R.layout.main_act);

        // 네비게이션뷰 내부버튼
        btnHome = (ConstraintLayout) findViewById(R.id.btn_nav_home);
        btnNote = (ConstraintLayout) findViewById(R.id.btn_nav_note);
        btnUser = (ConstraintLayout) findViewById(R.id.btn_nav_user);
        //툴바셋팅
        setupToolbar();
        //navigation drawer 셋팅
        setupNavigationDrawer();

        // 프래그먼트 매니져
        fragmentManager= getSupportFragmentManager();
        // 현재 활성화된 프래그먼트 생성
        getSelectedFragment();

    }

    @Override
    protected void onDestroy() {
        // 뷰모델의 ItemNavigator null 셋팅
        mViewModel.onActivityDestroyed();
        super.onDestroy();
    }

    /**
     * Home, Note, User 각각의 프래그먼트를 구분하여 관리
     **/
    // 네비게이션 현재 활성화된 프래그먼트 가져오기
    void getSelectedFragment(){
        int id = spActStatus.getInt("current_fragment",R.id.btn_nav_home);
        switch (id){
            case R.id.btn_nav_home :
                // 현재 프래그먼트 삭제
                if(currentFragment!=null){
                    fragmentManager.beginTransaction().remove(currentFragment).commit();
                }
                btnHome.setSelected(true);
                btnNote.setSelected(false);
                btnUser.setSelected(false);
                // 프래그먼트 생성 및 재활용
                mainHomeFragment = findOrCreateViewFragment();
                // 프래그먼트의 뷰모델 생성 및 재활용
                mViewModel = findOrCreateViewModel();
                // 네비게이터 셋팅
                mViewModel.setNavigator(this);
                // 프래그먼트와 뷰모델 연결
                mainHomeFragment.setMainViewModel(mViewModel);
                // 생성된 프래그먼트를 현재 프래그먼트로 셋팅
                currentFragment = mainHomeFragment;
                break;
            case R.id.btn_nav_note :
                // 현재 프래그먼트 삭제
                if(currentFragment!=null){
                    fragmentManager.beginTransaction().remove(currentFragment).commit();
                }
                btnHome.setSelected(false);
                btnNote.setSelected(true);
                btnUser.setSelected(false);
                // 생성된 프래그먼트를 현재 프래그먼트로 셋팅
                // currentFragment = mainHomeFragment;
                break;
            case R.id.btn_nav_user :
                // 현재 프래그먼트 삭제
                if(currentFragment!=null){
                    fragmentManager.beginTransaction().remove(currentFragment).commit();
                }
                btnHome.setSelected(false);
                btnNote.setSelected(false);
                btnUser.setSelected(true);
                // 생성된 프래그먼트를 현재 프래그먼트로 셋팅
                // currentFragment = mainHomeFragment;
                break;
        }

    }

    /**
     * 프래그먼트 , 뷰모델 생성 메소드
     **/
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
                    getApplicationContext()
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
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        if(mNavigationView != null){
            // 네비게이션에 include 되는 레이아웃
            View includeLayout = findViewById(R.id.drawer_include_layout);
            // 스위치 셋팅
            setupNavigationSwitch(includeLayout);
            // 다크모드일 경우 아이콘 이미지 변경
            setupNavigationIconColor(includeLayout);
            // 버튼셋팅
            setupNavigationButton();
        }
    }
    // Navigation switch checkedChange Listener
    private void setupNavigationSwitch(View includeLayout){
        // 네비게이션 스위치
        Switch mThemeSwitch, mNoticeSwitch;
        // 다크테마 switch 리스너 셋팅
        mThemeSwitch = (Switch) includeLayout.findViewById(R.id.switch_dark_theme);
        // 로컬에 저장되어있는 switch 상태 정보 가져옴
        mThemeSwitch.setChecked(spActStatus.getBoolean("dark_mode",false));
        mThemeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //Toast.makeText(this, "다크테마 -> "+mThemeSwitch.isChecked(), Toast.LENGTH_SHORT).show();

            // dark 모드 상태 업데이트
            spActStatus = getSharedPreferences("act_status", MODE_PRIVATE);
            SharedPreferences.Editor editor = spActStatus.edit();
            editor.putBoolean("dark_mode",isChecked);
            editor.apply();

            mDrawerLayout.closeDrawers();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        });
        // 푸쉬알림 switch 리스너 셋팅
        mNoticeSwitch = (Switch) includeLayout.findViewById(R.id.switch_notice);
        // 로컬에 저장되어있는 알림 switch 상태정보 가져옴
        mNoticeSwitch.setChecked(spActStatus.getBoolean("notice",true));
        mNoticeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Toast.makeText(this, "푸쉬알림 -> "+mNoticeSwitch.isChecked(), Toast.LENGTH_SHORT).show();
            // 알림 상태 업데이트
            spActStatus = getSharedPreferences("act_status", MODE_PRIVATE);
            SharedPreferences.Editor editor = spActStatus.edit();
            editor.putBoolean("notice",isChecked);
            editor.apply();
            mDrawerLayout.closeDrawers();
        });

    }
    // Navigation view 아이콘 색상 셋팅
    private void setupNavigationIconColor(View includeLayout){
        ImageView ivHome, ivNote, ivUser, ivDarkTheme, ivNotice, ivInfo;
        ivHome = (ImageView) includeLayout.findViewById(R.id.iv_btn_home);
        ivNote = (ImageView) includeLayout.findViewById(R.id.iv_btn_note);
        ivUser = (ImageView) includeLayout.findViewById(R.id.iv_btn_user);
        ivDarkTheme = (ImageView) includeLayout.findViewById(R.id.iv_dark_theme);
        ivNotice = (ImageView) includeLayout.findViewById(R.id.iv_notice);
        ivInfo = (ImageView) includeLayout.findViewById(R.id.iv_info);
        // 다크모드이면
        if(spActStatus.getBoolean("dark_mode",false)){
            ivHome.setImageResource(R.drawable.ic_home);
            ivNote.setImageResource(R.drawable.ic_note);
            ivUser.setImageResource(R.drawable.ic_user);
            ivDarkTheme.setImageResource(R.drawable.ic_dark_theme);
            ivNotice.setImageResource(R.drawable.ic_notice);
            ivInfo.setImageResource(R.drawable.ic_info);
        }
    }
    // Navigation view 네부 아이템 select listener
    private void setupNavigationButton(){
        // 홈버튼 온클릭 리스너 (프래그먼트)
        btnHome.setOnClickListener(v -> {
            saveNavigationButtonStatus(btnHome.getId());
            getSelectedFragment();
            mDrawerLayout.closeDrawers();
        });

        // 노트버튼 온클릭 리스너 (프래그먼트)
        btnNote.setOnClickListener(v -> {
            saveNavigationButtonStatus(btnNote.getId());
            getSelectedFragment();
            mDrawerLayout.closeDrawers();
        });

        // 사용자 정보 온클릭 리스너 (액티비티)
        btnUser.setOnClickListener(v -> {
            saveNavigationButtonStatus(btnUser.getId());
            getSelectedFragment();
            mDrawerLayout.closeDrawers();
        });

    }
    // 네비게이션에서 현재 활성화된 프래그먼트 업데이트
    void saveNavigationButtonStatus(int id){
        spActStatus = getSharedPreferences("act_status", MODE_PRIVATE);
        SharedPreferences.Editor editor = spActStatus.edit();
        editor.putInt("current_fragment",id);
        editor.apply();
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



}
