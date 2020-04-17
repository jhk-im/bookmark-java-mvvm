package com.jroomstudio.smartbookmarkeditor.popup;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jroomstudio.smartbookmarkeditor.Injection;
import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.ViewModelHolder;
import com.jroomstudio.smartbookmarkeditor.util.ActivityUtils;

import java.util.ArrayList;
import java.util.Objects;

public class EditAddItemPopupActivity extends AppCompatActivity implements EditAddItemPopupNavigator {

    /**
     * Request code
     * -> 메인 액티비티에서 intent 로 전달받는다.
     * 1. 카테고리편집
     * 2. 북마크편집
     * -> 편집이 끝나고 다시 반환 가능  -> seResult()
     **/
    public static final int REQUEST_CODE = 1;


    /**
     * Intent TYPE 구분자
     * ADD_ITEM - 아이템 추가
     * EDIT_CATEGORY - 카테고리 편집
     * EDIT_BOOKMARK - 북마크 편집
     **/
    public static final String ADD_ITEM = "ADD_ITEM";
    public static final String EDIT_CATEGORY = "EDIT_CATEGORY";
    public static final String EDIT_BOOKMARK = "EDIT_BOOKMARK";

    // 아이템 추가버튼 -> 인텐트로 전달받은 카테고리 리스트 key 값
    public static final String CATEGORY_LIST = "CATEGORY_LIST";
    // 선택된 아이템 key 값
    public static final String SELECT_CATEGORY = "SELECT_CATEGORY";

    // 아이템 편집시 intent key 값
    public static final String ID = "ID";
    public static final String TITLE = "TITLE";
    public static final String POSITION = "POSITION";
    public static final String SELECTED = "SELECTED";
    public static final String URL = "URL";
    public static final String FAVICON_URL = "FAVICON_URL";
    public static final String BOOKMARK_ACTION = "BOOKMARK_ACTION";

    // 카테고리 리스트
    private ArrayList<String> mTitleList = new ArrayList<>();
    // 선택된 카테고리
    private int mSelectCategory;

    // 뷰모델 생성 태그
    public static final String POP_ADD_ITEM_TAG = "POP_ADD_ITEM_TAG";

    // 뷰모델
    private AddItemPopupViewModel mViewModel;

    // 편집으로 사용될 컨테이너
    private LinearLayout mEditPopupContainer;
    // 프래그먼트 표시 컨테이너
    private FrameLayout mAddPopupContainer;

    private TextView mTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 상태바 제거하고 전체화면 모드로
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.edit_add_item_popup_act);

        mEditPopupContainer = (LinearLayout) findViewById(R.id.popup_edit_item_container);
        mAddPopupContainer = (FrameLayout) findViewById(R.id.content_frame);
        mTitleTextView = (TextView) findViewById(R.id.tv_edit_item_title);

        //인텐트 구분자 확인
        String intentType = getIntent().getType();
        // 인텐트로 구분하여 fragment 와 viewModel 셋팅
        setFragmentViewModel(Objects.requireNonNull(intentType));

    }

    // 프래그먼트와 뷰모델 셋팅
    public void setFragmentViewModel(String intentType){

        switch (intentType){
            case ADD_ITEM :
                mEditPopupContainer.setVisibility(View.GONE);
                mAddPopupContainer.setVisibility(View.VISIBLE);
                // 인텐트로 전달받은 카테고리 리스트
                mTitleList = getIntent().getStringArrayListExtra(CATEGORY_LIST);
                mSelectCategory = getIntent().getIntExtra(SELECT_CATEGORY,0);

                // Popup 창 프래그먼트와 뷰모델 연결
                AddItemPopupFragment addItemPopupFragment = findOrCreateViewFragment();
                // 프래그먼트의 스니퍼에 리스트 전달
                addItemPopupFragment.setSpinnerList(mTitleList,mSelectCategory);
                // 프래그먼트 뷰모델 생성
                mViewModel = findOrCreateViewModel();
                // 네비게이터 연결
                mViewModel.onActivityCreated(this);
                // 뷰모델 연결
                addItemPopupFragment.setViewModel(mViewModel);
                break;
            case EDIT_CATEGORY:
                mEditPopupContainer.setVisibility(View.VISIBLE);
                mAddPopupContainer.setVisibility(View.GONE);

                // 타이틀 셋팅
                mTitleTextView.setText(getIntent().getStringExtra(TITLE));

                // 레이아웃 크기설정
                // 너무 작게나와서 디바이스 화면 전체크기 구하고
                // 전체크기의 60 퍼센트로 셋팅
                mEditPopupContainer.post(() -> {
                    DisplayMetrics metrics = getResources().getDisplayMetrics();
                    int screenWidth = metrics.widthPixels * 60 / 100;

                    LinearLayout.LayoutParams position =
                            new LinearLayout.LayoutParams(screenWidth,
                                    mEditPopupContainer.getHeight());
                    mEditPopupContainer.setLayoutParams(position);
                });

                break;
            case EDIT_BOOKMARK:
                break;

        }
    }


    @Override
    protected void onDestroy() {
        if(mViewModel!=null){
            mViewModel.onActivityDestroyed();
        }
        super.onDestroy();
    }

    // 추가 프레그먼트 찾거나 생성
    @NonNull
    private AddItemPopupFragment findOrCreateViewFragment(){
        AddItemPopupFragment fragment = (AddItemPopupFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content_frame);
        if(fragment==null){
            fragment = AddItemPopupFragment.newInstance();

            // Send the task ID to the fragment
            /*
            Bundle bundle = new Bundle();
            bundle.putString(AddItemPopupFragment.ARGUMENT_EDIT_TASK_ID,
                    getIntent().getStringExtra(AddItemPopupFragment.ARGUMENT_EDIT_TASK_ID));
            fragment.setArguments(bundle);
            */
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    fragment, R.id.content_frame);
        }
        return fragment;
    }
    // 추가 뷰모델 찾거나 생성
    private AddItemPopupViewModel findOrCreateViewModel(){
        // ViewModelHolder(UI 없는 Fragment)
        // -> 뷰모델 생성 후 TAG 구분자인 MAIN_VM_TAG 을 입력하여 생성 또는 재활용
        @SuppressWarnings("unchecked")
        ViewModelHolder<AddItemPopupViewModel> retainedViewModel =
                (ViewModelHolder<AddItemPopupViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(POP_ADD_ITEM_TAG);
        // 입력한 tag 의 뷰모델이 존재한다면
        if(retainedViewModel != null && retainedViewModel.getViewModel() != null){
            //getViewModel() 로 가져와 리턴한다.
            return retainedViewModel.getViewModel();
        }else{
            // 입력한 TAG 의 뷰모델이 없다면 뷰모델을 생성하고 ViewModelHolder 에 추가한다.
            // 로컬 데이터소스 생성, 원격데이터소스 생성, 액티비티 context 입력
            AddItemPopupViewModel viewModel = new AddItemPopupViewModel(
                    Injection.provideBookmarksRepository(getApplicationContext()),
                    Injection.provideCategoriesRepository(getApplicationContext()),
                    getApplicationContext()
            );
            // ViewModelHolder(UI 없는 Fragment) 생성
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    ViewModelHolder.createViewModelHolder(viewModel),
                    POP_ADD_ITEM_TAG
            );
            return viewModel;
        }
    }

    // 편집 프래그먼트 찾거나 생성
    @NonNull
    private EditItemPopupFragment findOrCreateEditFragment(){
        EditItemPopupFragment fragment = (EditItemPopupFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content_frame);
        return fragment;
    }
    // 편집 뷰모델 찾거나 생성



    // 아이템 추가 완료후 팝업 액티비티 종료
    @Override
    public void updateItem() {
        finish();
    }

    // 취소버튼
    @Override
    public void cancelAddItem() {
        onBackPressed();
    }




}
