package com.jroomstudio.commentstube.tabedit;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jroomstudio.commentstube.R;
import com.jroomstudio.commentstube.ViewModelHolder;
import com.jroomstudio.commentstube.util.ActivityUtils;

public class TabEditActivity extends AppCompatActivity implements TabEditNavigator{

    // private DrawerLayout mDrawerLayout;
    private TabEditViewModel mTabEditViewModel;

    public static final String TAB_EDIT_VM = "TAB_EDIT_VM";

    ChipGroup chipGroup;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabedit_act);

        // 툴바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        //첫번째 프래그먼트
        TabEditFragment tabEditFragment = findOrCreateViewFragment();
        mTabEditViewModel = findOrCreateViewModel();
        tabEditFragment.setTabEditViewModel(mTabEditViewModel);


        // Floating action button
        setupFab();

        // chip 임시
        chipGroup = (ChipGroup) findViewById(R.id.chip_group);
        for(int i = 0; i<5; i++){
            // Chip 인스턴스 생성
            Chip chip = new Chip(TabEditActivity.this);
            // Chip 의 텍스트 지정
            chip.setText("chip"+i);
            // 체크 표시 사용 여부
            chip.setCheckable(true);
            // chip close 아이콘 이미지 지정
            chip.setCloseIcon(getDrawable(R.drawable.ic_close));
            // close icon 표시 여부
            chip.setCloseIconVisible(true);
            // chip group 에 해당 chip 추가
            chipGroup.addView(chip);

            // chip 인스턴스 클릭 리스너
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(TabEditActivity.this, "Check", Toast.LENGTH_SHORT).show();
                }
            });

            // chip 인스턴스 close 버튼 클릭 리스너
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chipGroup.removeView(v);
                }
            });
        }

    }

    // 옵션 메뉴버튼 뒤로가기
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    //기본 프래그먼트와 뷰모델
    @NonNull
    private TabEditFragment findOrCreateViewFragment(){
        TabEditFragment tabEditFragment =
                (TabEditFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if(tabEditFragment == null) {
            tabEditFragment = TabEditFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    tabEditFragment,R.id.content_frame);
        }
        return tabEditFragment;
    }
    private TabEditViewModel findOrCreateViewModel(){
        @SuppressWarnings("unchecked")
        ViewModelHolder<TabEditViewModel> retainedViewModel =
                (ViewModelHolder<TabEditViewModel>) getSupportFragmentManager().
                        findFragmentByTag(TAB_EDIT_VM);
        if(retainedViewModel != null && retainedViewModel.getViewmodel() != null) {
            return retainedViewModel.getViewmodel();
        } else {
            TabEditViewModel viewModel = new TabEditViewModel(getApplicationContext());
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    ViewModelHolder.createContainer(viewModel), TAB_EDIT_VM);
            return viewModel;
        }
    }

    // Floating Action Button 셋팅
    private void setupFab() {
        FloatingActionButton fab =
                (FloatingActionButton) findViewById(R.id.fab_done);
       // fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTabEditViewModel.tabEditComplete();
            }
        });
    }

    @Override
    public void tabEditComplete() {

    }

}
