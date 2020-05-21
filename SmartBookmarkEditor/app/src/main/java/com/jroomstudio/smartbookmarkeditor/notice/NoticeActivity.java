package com.jroomstudio.smartbookmarkeditor.notice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.ViewModelHolder;
import com.jroomstudio.smartbookmarkeditor.data.notice.Notice;
import com.jroomstudio.smartbookmarkeditor.data.notice.NoticeLocalRepository;
import com.jroomstudio.smartbookmarkeditor.data.notice.NoticeLocalDatabase;
import com.jroomstudio.smartbookmarkeditor.main.MainActivity;
import com.jroomstudio.smartbookmarkeditor.util.ActivityUtils;
import com.jroomstudio.smartbookmarkeditor.util.AppExecutors;

public class NoticeActivity extends AppCompatActivity implements NoticeNavigator {

    // 뷰모델
    private NoticeViewModel mViewModel;

    // 뷰
    private NoticeFragment mFragment;

    // 뷰모델 태그
    public static final String NOTICE_VM_TAG = "NOTICE_VM_TAG";

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
        setContentView(R.layout.notice_act);

        // 툴바 셋팅
        setupToolbar();

        // 프래그먼트와 뷰모델 셋팅
        mFragment = findOrCreateViewFragment();
        mViewModel = findOrCreateViewModel();
        mViewModel.setNavigator(this);
        mFragment.setViewModel(mViewModel);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.onActivityDestroyed();
    }

    /**
     * 다크테마 셋팅
     **/
    void setupDarkTheme(String darkThemeKey){
        if(spActStatus.getBoolean(darkThemeKey,true)){
            setTheme(R.style.DarkAppTheme);
        }
    }

    /**
     * 프래그먼트 , 뷰모델 생성 메소드
     **/
    // 홈 프래그먼트 생성 또는 재활용
    @NonNull
    private NoticeFragment findOrCreateViewFragment() {
        // Main 프래그먼트
        NoticeFragment fragment =
                (NoticeFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if(fragment == null){
            // 프래그먼트 생성
            fragment = NoticeFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    fragment, R.id.content_frame);
        }
        return fragment;
    }
    // 홈 프래그먼트 뷰모델 생성 또는 재활용
    private NoticeViewModel findOrCreateViewModel() {
        // ViewModelHolder(UI 없는 Fragment)
        // -> 뷰모델 생성 후 TAG 구분자인 MAIN_VM_TAG 을 입력하여 생성 또는 재활용
        @SuppressWarnings("unchecked")
        ViewModelHolder<NoticeViewModel> retainedViewModel =
                (ViewModelHolder<NoticeViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(NOTICE_VM_TAG);
        // 입력한 tag 의 뷰모델이 존재한다면
        if(retainedViewModel != null && retainedViewModel.getViewModel() != null){
            //getViewModel() 로 가져와 리턴한다.
            return retainedViewModel.getViewModel();
        }else{

            // 입력한 TAG 의 뷰모델이 없다면 뷰모델을 생성하고 ViewModelHolder 에 추가한다.
            // 로컬 데이터소스 생성액티비티 context 입력
            NoticeLocalDatabase database = NoticeLocalDatabase.getInstance(this);
            NoticeLocalRepository noticeLocalRepository = NoticeLocalRepository.
                    getInstance(new AppExecutors(), database.notificationsDAO());
            // 알림 메세지 저장하는 룸 데이터베이스 생성
            NoticeViewModel viewModel = new NoticeViewModel(noticeLocalRepository,this);
            // ViewModelHolder(UI 없는 Fragment) 생성
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    ViewModelHolder.createViewModelHolder(viewModel),
                    NOTICE_VM_TAG
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
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setTitle(R.string.notice_title);
        ab.setDisplayHomeAsUpEnabled(true);
    }
    // 옵션메뉴 셀렉트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 메인 액티비티 이동
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 모든 알림 삭제
    @Override
    public void allDeleteItems() {
        Intent intent = new Intent(this,NoticePopupActivity.class);
        intent.putExtra("delete_type",NoticePopupActivity.ALL_DELETE);
        startActivity(intent);
    }

    // 선택된 알림 삭제
    @Override
    public void deleteItem(String id) {
        Intent intent = new Intent(this,NoticePopupActivity.class);
        intent.putExtra("delete_type",NoticePopupActivity.ITEM_DELETE);
        intent.putExtra("delete_id",id);
        startActivity(intent);
    }

    // 알림 자세히 보기로 이동
    @Override
    public void moveToDetail(Notice notice) {
        Intent intent = new Intent(this, NoticeDetailActivity.class);
        intent.putExtra("title",notice.getTitle());
        intent.putExtra("body",notice.getDescription());
        intent.putExtra("date",notice.getDate());
        intent.putExtra("id",notice.getId());
        startActivity(intent);
    }
}
