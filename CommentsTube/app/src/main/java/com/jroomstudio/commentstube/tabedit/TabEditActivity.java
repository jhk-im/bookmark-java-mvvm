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
import com.jroomstudio.commentstube.Injection;
import com.jroomstudio.commentstube.R;
import com.jroomstudio.commentstube.ViewModelHolder;
import com.jroomstudio.commentstube.main.MainActivity;
import com.jroomstudio.commentstube.util.ActivityUtils;

public class TabEditActivity extends AppCompatActivity{

    // private DrawerLayout mDrawerLayout;
    private TabEditViewModel mTabEditViewModel;

    public static final String TAB_EDIT_VM = "TAB_EDIT_VM";

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

        //프래그먼트 셋팅
        TabEditFragment tabEditFragment = findOrCreateViewFragment();
        mTabEditViewModel = findOrCreateViewModel();
        tabEditFragment.setTabEditViewModel(mTabEditViewModel);

        // Floating action button
        setupFab();


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
            TabEditViewModel viewModel = new TabEditViewModel(
                    Injection.provideTabsRepository(getApplicationContext()), getApplicationContext());
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
        fab.setOnClickListener(v -> {
                    mTabEditViewModel.saveTabs();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
        );
    }


}
