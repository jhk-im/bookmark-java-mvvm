package com.jroomstudio.smartbookmarkeditor.itemtouch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jroomstudio.smartbookmarkeditor.Injection;
import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.ViewModelHolder;
import com.jroomstudio.smartbookmarkeditor.util.ActivityUtils;

public class ItemTouchEditActivity extends AppCompatActivity implements ItemTouchEditNavigator {

    // 프래그머트 뷰모델 생성시 사용할 태그
    public static final String TOUCH_EDIT_TAG = "TOUCH_EDIT_TAG";

    // 프래그먼트 뷰모델
    private ItemTouchEditViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // dark 모드 상태 가져오기
        SharedPreferences spActStatus = getSharedPreferences("act_status", MODE_PRIVATE);
        SharedPreferences.Editor editor = spActStatus.edit();
        editor.apply();
        // 다크모드이면 다크모드로 테마변경
        if(spActStatus.getBoolean("dark_mode",true)){
            setTheme(R.style.DarkAppTheme);
        }

        setContentView(R.layout.item_touch_edit_act);

        // 툴바 셋팅
        setupToolbar();

        // 프래그먼트 생성 및 재활용
        ItemTouchEditFragment fragment = findOrCreateViewFragment();
        // 프래그먼트의 뷰모델 생성 및 재활용
        mViewModel = findOrCreateViewModel();
        // 네비게이터 셋팅
        mViewModel.setNavigator(this);
        // 프래그먼트 뷰모델 연결
        fragment.setViewModel(mViewModel);

    }

    @Override
    protected void onDestroy() {
        mViewModel.onActivityDestroyed();
        super.onDestroy();
    }

    // 프래그먼트 뷰 생성 또는 재활용
    @NonNull
    private ItemTouchEditFragment findOrCreateViewFragment(){
        ItemTouchEditFragment fragment =
                (ItemTouchEditFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if(fragment == null){
            // 프래그먼트 생성
            Log.d("create fragment","ItemTouchEditFragment");
            fragment = ItemTouchEditFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    fragment, R.id.content_frame);
        }
        return fragment;
    }
    // 프래그먼트 뷰모델 생성 또는 재활용
    private ItemTouchEditViewModel findOrCreateViewModel(){
        // ViewModelHolder(UI 없는 Fragment)
        // -> 뷰모델 생성 후 TAG 구분자인 MAIN_VM_TAG 을 입력하여 생성 또는 재활용
        @SuppressWarnings("unchecked")
        ViewModelHolder<ItemTouchEditViewModel> retainedViewModel =
                (ViewModelHolder<ItemTouchEditViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(TOUCH_EDIT_TAG);
        // 뷰모델 이미 생성되었다면
        if(retainedViewModel != null && retainedViewModel.getViewModel() != null){
            //getViewModel() 로 가져와 리턴한다.
            return retainedViewModel.getViewModel();
        }else{
            // 입력한 TAG 의 뷰모델이 없다면 뷰모델을 생성하고 ViewModelHolder 에 추가한다.
            // 로컬 데이터소스 생성, 원격데이터소스 생성, 액티비티 context 입력
            ItemTouchEditViewModel viewModel = new ItemTouchEditViewModel(
                    Injection.provideBookmarksRepository(getApplicationContext()),
                    Injection.provideCategoriesRepository(getApplicationContext()),
                    getApplicationContext()
            );
            Log.d("create view model","ItemTouchEditViewModel");
            // ViewModelHolder(UI 없는 Fragment) 생성
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    ViewModelHolder.createViewModelHolder(viewModel),
                    TOUCH_EDIT_TAG
            );
            return viewModel;
        }
    }

    // 툴바 셋팅
    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(false);
        ab.setTitle("아이템 순서변경");
    }

    // 아이템 순서 편집 완료
    @Override
    public void onItemsSaved() {
        finish();
    }


}
