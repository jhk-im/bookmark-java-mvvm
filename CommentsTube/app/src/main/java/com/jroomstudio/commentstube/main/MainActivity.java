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

    /**
     * 메인액티비티의 뷰모델에 있는 start() 메소드 실행
     *
     * start() 메소드는 loadTabs() 메소드를 실행하여
     * tab 객체의 정보를 로컬,원격 데이터베이스에 접근하여 받아온다.
     * 받아온 정보대로 뷰페이저에 프래그먼트를 셋팅한다.
     **/
    @Override
    protected void onResume() {
        super.onResume();
        mMainActViewModel.start();
    }

    // 뷰페이저 어댑터 셋팅
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
    //네비게이션 드로어 뷰 내부에 표시될 리스트
    public void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(item -> {
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

        /**
         * MainPagerAdapter 를 생성할때 입력받는 값 들의 멤버변수
         **/
        private FragmentManager mFragmentManager;
        private Context mContext;
        private List<Tab> mTabs;
        private TabsRepository mTabsReository;

        /**
         * 각각의 프래그먼트의 비즈니스 로직을 담당할 뷰 모델
         * {@link MainFragViewModel} - Tab 의 뷰타입이 MAIN_FRAG 인 프래그먼트에 셋팅 될 뷰모델
         * {@link SubViewModel} - Tab 의 뷰타입이 SUB_FRAG 인 프래그먼트에 셋팅 될 뷰모델
         **/
        private MainFragViewModel mMainFragViewModel;
        private SubViewModel mSubViewModel;



        /**
         * MainPagerAdapter 생성자
         *
         * @param fm - 프래그먼트 매니저 , fragment 생성에 필요하다.
         * @param behavior -
         * @param context - 뷰페이저가 셋팅된 액티비티의 context
         * @param tabs - 뷰페이저에 셋팅되는 각각의 tab 객체를 담아둔 List
         * @param tabsRepository - 로컬, 원격 으로부터 tab 객체의 데이터를 받아오는 인스턴스
         **/
        public MainPagerAdapter(@NonNull FragmentManager fm, int behavior,
                                Context context, List<Tab> tabs, TabsRepository tabsRepository) {
            super(fm, behavior);
            this.mFragmentManager = fm;
            this.mContext = context;
            this.mTabsReository = tabsRepository;
            setList(tabs);
        }


        /**
         * 표시될 뷰페이저의 갯수를 반환한다.
         * 반환되는 숫자 만큼 내부적으로 getItem 이 실행된다.
         *
         * mTab -> Tab 객체 즉, 뷰페이저의 아이템들을 담아두는 리스트
         * mTab 리스트 사이즈 만큼 getItem 이 실행된다.
         **/
        @Override
        public int getCount() {
            // return 3;
            return mTabs != null ? mTabs.size() : 0;
             }

        /**
         * getCount 에서 반환되는 숫자만큼 실행된다.
         *
         * ex) getCount 가 3을 반환하면  0 -> 1 -> 2 getCount() 에 입력되어 실행된다.
         *
         * position 값으로 List<Tab> 을 순차적으로 검사할 수 있다.
         * 리턴값으로 프래그먼트를 반환하고 반환된 프래그먼트가 뷰페이저에 표시되게 된다.
         *  position 값과 viewType 을 입력하여 findOrCreateFragment 를 실행하여 프래그먼트를 받아온다.
         **/
        @Override
        public Fragment getItem(int position) {
            // Tab 리스트에서 각각 뷰타입을 가져옴
            String viewType = mTabs.get(position).getViewType();
            return findOrCreateFragment(position, viewType);
        }

        /**
         * 프래그먼트를 생성하여 반환하는 메소드이다.
         * position 값으로 Tab<List> 에서 각각의 Tab 객체를 검사하고
         * viewType 의 값을 구분하여 프래그먼트를 각각 생성하고 반환한다.
         *
         * 각각의 프래그먼트의 비즈니스 로직을 담당할 뷰모델을 생성하고 연결한다.
         **/
        @NonNull
        private Fragment findOrCreateFragment(int position, String viewType){
            // 뷰타입에 따라 프래그먼트와 뷰모델을 각각 셋팅
            switch (viewType){
                case "SUB_FRAG" :
                    SubFragment subFragment = SubFragment.newInstance();
                    mSubViewModel = findOrCreateSubViewModel(position);
                    subFragment.setSubViewModel(mSubViewModel);
                    return subFragment;
                case "MAIN_FRAG" :
                    MainFragment mainFragment = MainFragment.newInstance();
                    mMainFragViewModel = findOrCreateViewModel(position);
                    mainFragment.setMainViewModel(mMainFragViewModel);
                    return mainFragment;
                default:
                    return null;
            }
        }

        /**
         * 각각의 프래그먼트의 비즈니스 로직을 담당하고
         * BaseObservable 인터페이스로 해당 프래그먼트의 UI 를 관찰한다.
         *
         * {@link MainFragViewModel} - MAIN_FRAG 타입의 프래그먼트 뷰모델
         * {@link SubViewModel} - SUB_FRAG 타입의 프래그먼트 뷰모델
         **/
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


        /**
         * 각각의 프래그먼트의 탭 이름을 지정
         *
         * 뷰페이저 내부에 TabLayout 이 셋팅되어있다.
         * Tab 객체의 정보대로 프래그먼트가 셋팅되어 뷰페이저가 구성될 때
         * TabLayout 에 각각의 프래그먼트 별로 탭이 생성된다.
         **/
        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs.get(position).getName();
        }

        /**
         * 입력받은 리스트로 멤버 List<Tab> 에 셋팅하는 메소드
         *
         * notifyDataSetChanged() 메소드로 데이터가 변경되었음을 Adapter 에 알린다.
         * 없을 경우 런타임 에러 발생
         **/
        private void setList(List<Tab> tabs){
            mTabs = tabs;
            notifyDataSetChanged();
        }

        // 리스트 얻기 - 아직 사용되지 않음
        public List<Tab> getTabs() {
            return mTabs;
        }

        /**
         * 멤버 List<Tab> 을 replace 한다.
         *
         * {@link TabsListBinding} 에서 호출된다.
         * - ViewPager 에 프래그먼트가 추가될 때 Tab 객체의 데이터를 사용한다.
         *   ViewPager 와 연결되어 있으며 ViewPager 에 아이템이 추가될 때
         *   ViewPager 가 셋팅된 액티비티의 뷰모델에서 Tab 객체를 ObservableField 에 저장한다.
         *   저장 된 객체를 TabListBinding 에서 관찰하고 있다가 변화가 감지되면
         *   해당 메소드를 호출하여 멤버 Tab<List> 의 데이터를 갱신하다.
         **/
        public void replaceData(List<Tab> tabs){
            setList(tabs);
        }

    }

}
