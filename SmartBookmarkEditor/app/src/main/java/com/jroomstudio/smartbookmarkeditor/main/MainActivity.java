package com.jroomstudio.smartbookmarkeditor.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.jroomstudio.smartbookmarkeditor.Injection;
import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.ViewModelHolder;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.category.Category;
import com.jroomstudio.smartbookmarkeditor.popup.EditAddItemPopupActivity;
import com.jroomstudio.smartbookmarkeditor.util.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements MainNavigator, CategoryItemNavigator, BookmarkItemNavigator {

    private DrawerLayout mDrawerLayout;

    // 메인 프래그먼트 뷰모델 태그
    public static final String MAIN_VM_TAG = "MAIN_VM_TAG";

    // 액티비티 상태저장 Shared Preferences
    private SharedPreferences spActStatus;

    // 메인 프래그먼트 뷰모델
    private MainViewModel mViewModel;

    // 메인 네비게이션 뷰
    private NavigationView mNavigationView;

    // 아이템추가 or 편집 -> 스피너리스트로 전달할 카테고리 리스트 카운트
    private int mSelectCategoryCount;
    // 현재 선택된 카테고리
    private String mSelectCategory;


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

        //툴바셋팅
        setupToolbar();

        //navigation drawer 셋팅
        setupNavigationDrawer();

        // 프래그먼트 생성 및 재활용
        MainFragment mainFragment = findOrCreateViewFragment();
        // 프래그먼트의 뷰모델 생성 및 재활용
        mViewModel = findOrCreateViewModel();
        // 네비게이터 셋팅
        mViewModel.setNavigator(this);
        // 프래그먼트와 뷰모델 연결
        mainFragment.setMainViewModel(mViewModel);

    }

    // 프래그먼트 생성 또는 재활용
    @NonNull
    private MainFragment findOrCreateViewFragment() {
        // Main 프래그먼트
        MainFragment mainFragment =
                (MainFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if(mainFragment == null){
            // 프래그먼트 생성
            mainFragment = MainFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    mainFragment, R.id.content_frame);
        }
        return mainFragment;
    }
    // 프래그먼트 뷰모델 생성 또는 재활용
    private MainViewModel findOrCreateViewModel() {
        // ViewModelHolder(UI 없는 Fragment)
        // -> 뷰모델 생성 후 TAG 구분자인 MAIN_VM_TAG 을 입력하여 생성 또는 재활용
        @SuppressWarnings("unchecked")
        ViewModelHolder<MainViewModel> retainedViewModel =
                (ViewModelHolder<MainViewModel>) getSupportFragmentManager()
                .findFragmentByTag(MAIN_VM_TAG);
        // 입력한 tag 의 뷰모델이 존재한다면
        if(retainedViewModel != null && retainedViewModel.getViewModel() != null){
            //getViewModel() 로 가져와 리턴한다.
            return retainedViewModel.getViewModel();
        }else{
            // 입력한 TAG 의 뷰모델이 없다면 뷰모델을 생성하고 ViewModelHolder 에 추가한다.
            // 로컬 데이터소스 생성, 원격데이터소스 생성, 액티비티 context 입력
            MainViewModel viewModel = new MainViewModel(
                    Injection.provideBookmarksRepository(getApplicationContext()),
                    Injection.provideCategoriesRepository(getApplicationContext()),
                    getApplicationContext()
            );
            // ViewModelHolder(UI 없는 Fragment) 생성
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    ViewModelHolder.createViewModelHolder(viewModel),
                    MAIN_VM_TAG
            );
            return viewModel;
        }
    }

    // 툴바셋팅 메소드
    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }
    // 툴바 타이틀 변경하기
    @Override
    public void setToolbarTitle(String title) {
        ActionBar ab = getSupportActionBar();
        ab.setTitle(title);
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

    // Navigation Drawer 셋팅 메소드ㅇ
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
            startActivity(new Intent(this,MainActivity.class));
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

        ConstraintLayout btnHome, btnUser, btnNote;
        btnHome = (ConstraintLayout) findViewById(R.id.btn_nav_home);
        btnNote = (ConstraintLayout) findViewById(R.id.btn_nav_note);
        btnUser = (ConstraintLayout) findViewById(R.id.btn_nav_user);
        btnHome.setSelected(false);
        btnNote.setSelected(false);
        btnUser.setSelected(false);

        // 홈버튼 온클릭 리스너
        btnHome.setOnClickListener(v -> {
            btnHome.setSelected(true);
            btnNote.setSelected(false);
            btnUser.setSelected(false);
            //mDrawerLayout.closeDrawers();
            Toast.makeText(this, "HOME", Toast.LENGTH_SHORT).show();
        });

        // 노트버튼 온클릭 리스너
        btnNote.setOnClickListener(v -> {
            btnHome.setSelected(false);
            btnNote.setSelected(true);
            btnUser.setSelected(false);
            //mDrawerLayout.closeDrawers();
            Toast.makeText(this, "NOTE", Toast.LENGTH_SHORT).show();
        });

        // 사용자 정보 온클릭 리스너
        btnUser.setOnClickListener(v -> {
            btnHome.setSelected(false);
            btnNote.setSelected(false);
            btnUser.setSelected(true);
            //mDrawerLayout.closeDrawers();
            Toast.makeText(this, "USER", Toast.LENGTH_SHORT).show();
        });

    }

    // 현재 활성화된 프래그먼트 업데이트
    void setupNavigationButtonStatus(Button btn){
        spActStatus = getSharedPreferences("act_status", MODE_PRIVATE);
        SharedPreferences.Editor editor = spActStatus.edit();
        editor.putInt("current_fragment",btn.getId());
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        // 뷰모델의 ItemNavigator null 셋팅
        mViewModel.onActivityDestroyed();
        super.onDestroy();
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

    // 카테고리 셀렉트 구현
    @Override
    public void selectedCategory(Category category) {
        mViewModel.changeSelectCategory(category);
    }

    // 북마크 셀렉트 구현
    @Override
    public void selectedBookmark(Bookmark bookmark) {
        // 웹뷰로 이동하여 웹페이지 보여주는 것 구현하기
        Log.e("selectedBookmark",bookmark.toString());
    }



}
