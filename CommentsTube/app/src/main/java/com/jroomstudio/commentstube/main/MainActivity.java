package com.jroomstudio.commentstube.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.Observable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.jroomstudio.commentstube.Injection;
import com.jroomstudio.commentstube.R;
import com.jroomstudio.commentstube.ViewModelHolder;
import com.jroomstudio.commentstube.data.Tab;
import com.jroomstudio.commentstube.data.source.TabsRepository;
import com.jroomstudio.commentstube.data.source.local.AppLocalDatabase;
import com.jroomstudio.commentstube.databinding.MainActBinding;
import com.jroomstudio.commentstube.tabedit.TabEditActivity;
import com.jroomstudio.commentstube.util.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    // 메인 액티비티 뷰모델
    private MainActViewModel mMainActViewModel;

    // 메인 액티비티 데이터 바인딩
    private MainActBinding mMainActBinding;

    // 메인 액티비티 뷰페이저
    private FragmentPagerAdapter adapterViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_act);

        // 툴바와 네비게이션뷰 셋팅
        setupToolbar();
        setupNavigationDrawer();

        // 데이터 바인딩
        View view = findViewById(R.id.drawer_layout);
        mMainActBinding = MainActBinding.bind(view);
        mMainActViewModel = new MainActViewModel(
                Injection.provideTabsRepository(getApplicationContext()),getApplicationContext());
        mMainActBinding.setViewmodel(mMainActViewModel);

        // 뷰페이저 셋팅
        setupViewPagerAdapter();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMainActViewModel.start();
    }

    private void setupViewPagerAdapter(){
        //ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        //viewPager.setAdapter(adapterViewPager);
        adapterViewPager = new MainPagerAdapter(getSupportFragmentManager(),
                0,this,new ArrayList<Tab>(0),
                Injection.provideTabsRepository(getApplicationContext()));
        mMainActBinding.viewPager.setAdapter(adapterViewPager);
    }


    //툴바표시
    public void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }
    //네비게이션뷰 표시
    public void setupNavigationDrawer(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if(navigationView != null){
            setupDrawerContent(navigationView);
        }
    }
    //네비게이션 내부에 표시될 리스트
    public void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.info_navigation_menu_item:
                        Toast.makeText(MainActivity.this, "info", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.notice_navigation_menu_item:
                        Toast.makeText(MainActivity.this, "notice", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.bookmark_navigation_menu_item:
                        Toast.makeText(MainActivity.this, "bookmark", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                item.setCheckable(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }
    //왼쪽 상단 홈버튼 생성
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.menu_filter:
                Intent intent = new Intent(this, TabEditActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_search:
                Toast.makeText(this, "search", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
    // 필터, 검색버튼 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * 메인 액티비티에 구현되는 뷰 페이저 어댑터
     * FragmentManager, TAG, context 등을 입력받아 각각의 프래그먼트를 구분하여 관리한다.
     * */
    public static class MainPagerAdapter extends FragmentPagerAdapter {


        private FragmentManager mFragmentManager;
        private Context mContext;
        private MainFragViewModel mMainFragViewModel;
        private SubViewModel mSubViewModel;

        // 데이터베이스
        private List<Tab> mTabs;

        private TabsRepository mTabsReository;

        public MainPagerAdapter(@NonNull FragmentManager fm, int behavior,
                                Context context, List<Tab> tabs, TabsRepository tabsRepository) {
            super(fm, behavior);
            this.mFragmentManager = fm;
            this.mContext = context;
            this.mTabsReository = tabsRepository;
            setList(tabs);
        }


        // 뷰페이저에 표시 될 프래그먼트의 총 갯수
        @Override
        public int getCount() {
            // return 3;
            return mTabs != null ? mTabs.size() : 0;
             }

        // 현재 프래그먼트의 포지션값으로 프래그먼트 반환
        @Override
        public Fragment getItem(int position) {
            // Tab 리스트에서 각각 뷰타입을 가져옴
            String viewType = mTabs.get(position).getViewType();
            return findOrCreateFragment(position, viewType);
        }

        // 페이지와 태그로 각각의 프래그먼트를 구분하여 생성하고 그에맞는 뷰 모델과 연결한다.
        @NonNull
        private Fragment findOrCreateFragment(int position, String viewType){
            // 뷰타입에 따라 프래그먼트와 뷰모델을 각각 셋팅
            switch (viewType){
                case "SUB_FRAG" :
                    SubFragment subFragment = SubFragment.newInstance();
                    mSubViewModel = findOrCreateSubViewModel(position);
                    subFragment.setSubViewModel(mSubViewModel);
                    return subFragment;
                case "DEF_FRAG" :
                    MainFragment mainFragment = MainFragment.newInstance();
                    mMainFragViewModel = findOrCreateViewModel(position);
                    mainFragment.setMainViewModel(mMainFragViewModel);
                    return mainFragment;
                default:
                    return null;
            }
        }

        // 메인 프래그먼트의 메인 뷰 모델을 연결
        private MainFragViewModel findOrCreateViewModel(int position){
            @SuppressWarnings("unchecked")
            ViewModelHolder<MainFragViewModel> retainedViewModel =
                    (ViewModelHolder<MainFragViewModel>) mFragmentManager.
                            findFragmentByTag(mTabs.get(position).getId());
            if(retainedViewModel != null && retainedViewModel.getViewmodel() != null) {
                return retainedViewModel.getViewmodel();
            } else {
                MainFragViewModel viewModel = new MainFragViewModel(mContext.getApplicationContext());
                ActivityUtils.addFragmentToActivity(mFragmentManager,
                        ViewModelHolder.createContainer(viewModel),mTabs.get(position).getId());

                // 테스트용 텍스트뷰
                viewModel.tvTest.set(mTabs.get(position).getViewType()+"\n"
                                     +mTabs.get(position).isUsed());

                return viewModel;
            }

        }

        // 구독 프래그먼트의 Sub 뷰 모델을 연결
        private SubViewModel findOrCreateSubViewModel(int position){
            @SuppressWarnings("unchecked")
            ViewModelHolder<SubViewModel> retainedViewModel =
                    (ViewModelHolder<SubViewModel>) mFragmentManager.
                            findFragmentByTag(mTabs.get(position).getId());
            if(retainedViewModel != null && retainedViewModel.getViewmodel() != null) {
                return retainedViewModel.getViewmodel();
            } else {
                SubViewModel viewModel = new SubViewModel(mContext.getApplicationContext());
                ActivityUtils.addFragmentToActivity(mFragmentManager,
                        ViewModelHolder.createContainer(viewModel),mTabs.get(position).getId());

                // 테스트용 텍스트뷰
                viewModel.tvTest.set(mTabs.get(position).getViewType()+"\n"
                        +mTabs.get(position).isUsed());

                return viewModel;
            }
        }

        // 뷰페이저 탭 이름 설정
        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs.get(position).getName();
        }

        // 입력받은 리스트를 멤버 리스트로 셋팅
        private void setList(List<Tab> tabs){
            mTabs = tabs;
            notifyDataSetChanged();
        }

        // 리스트 얻기
        public List<Tab> getTabs() {
            return mTabs;
        }

        // TabsListBinding 에서 리스트의 변화를 감지하여 멤버변수 재 셋팅
        public void replaceData(List<Tab> tabs){
            setList(tabs);
        }

    }

}
