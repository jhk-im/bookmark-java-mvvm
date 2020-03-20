package com.jroomstudio.commentstube.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.jroomstudio.commentstube.R;
import com.jroomstudio.commentstube.ViewModelHolder;
import com.jroomstudio.commentstube.tabedit.TabEditActivity;
import com.jroomstudio.commentstube.util.ActivityUtils;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    // 프래그먼트 뷰모델 태그
    public static final String MAIN_VM_TAG = "MAIN_VM_TAG";

    private FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_act);

        // 툴바와 네비게이션뷰 셋팅
        setupToolbar();
        setupNavigationDrawer();

        // 뷰페이저
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        adapterViewPager = new MainPagerAdapter(getSupportFragmentManager(),
                0,MAIN_VM_TAG,this);
        viewPager.setAdapter(adapterViewPager);
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
        private static int NUM_ITEMS = 3;

        private FragmentManager mFragmentManager;
        private Context mContext;
        private String mTAG;

        private MainViewModel mMainViewModel;

        public MainPagerAdapter(@NonNull FragmentManager fm, int behavior,
                                String TAG, Context context) {
            super(fm, behavior);
            this.mFragmentManager = fm;
            this.mContext = context;
            this.mTAG = TAG;
        }

        // 뷰페이저에 표시 될 프래그먼트의 총 갯수
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // 현재 프래그먼트의 뷰페이저 포지션값을 반환함
        @Override
        public Fragment getItem(int position) {
            return findOrCreateFragment(position);
        }

        // 페이지와 태그로 각각의 프래그먼트를 구분하여 생성하고 그에맞는 뷰 모델과 연결한다.
        @NonNull
        private Fragment findOrCreateFragment(int page){
            switch (mTAG){
                case"MAIN_VM_TAG":
                    MainFragment mainFragment =
                            MainFragment.newInstance(page);
                    mMainViewModel = findOrCreateViewModel(mTAG + page);
                    mainFragment.setMainViewModel(mMainViewModel);
                    return mainFragment;
                default:
                    return null;
            }
        }

        // 메인 프래그먼트에 맞는 메인 뷰 모델을 연결
        private MainViewModel findOrCreateViewModel(String TAG){
            @SuppressWarnings("unchecked")
            ViewModelHolder<MainViewModel> retainedViewModel =
                    (ViewModelHolder<MainViewModel>) mFragmentManager.
                            findFragmentByTag(TAG);
            if(retainedViewModel != null && retainedViewModel.getViewmodel() != null) {
                retainedViewModel.getViewmodel().tvTest.set("default 재사용");
                return retainedViewModel.getViewmodel();
            } else {
                MainViewModel viewModel = new MainViewModel(mContext.getApplicationContext());
                ActivityUtils.addFragmentToActivity(mFragmentManager,
                        ViewModelHolder.createContainer(viewModel),TAG);
                viewModel.tvTest.set(TAG);
                return viewModel;
            }

        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

}
