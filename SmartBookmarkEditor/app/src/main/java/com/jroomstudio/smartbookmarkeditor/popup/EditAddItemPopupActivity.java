package com.jroomstudio.smartbookmarkeditor.popup;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jroomstudio.smartbookmarkeditor.Injection;
import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.ViewModelHolder;
import com.jroomstudio.smartbookmarkeditor.util.ActivityUtils;

import java.util.ArrayList;

public class EditAddItemPopupActivity extends AppCompatActivity implements EditAddItemPopupNavigator {


    /**
     * Intent TYPE 구분자
     * ADD_ITEM - 아이템 추가
     * EDIT_CATEGORY - 카테고리 편집
     * EDIT_BOOKMARK - 북마크 편집
     *
     * 뷰모델 구분 태그로도 사용됨
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

    // 뷰모델
    private EditItemPopupViewModel mViewModel;

    // 편집, add, url 표시 리니어 레이아웃
    private LinearLayout mEditPopupLinear,mFragmentPopupLinear,mEditPopupUrlLinear;

    // 인텐트로 넘어온 뷰타입
    // 추가 / 북마크편집 / 카테고리편집
    private String mIntentViewType;

    // 편집할 아이템의 ID
    private String mItemId;

    // Bookmark 인지 Category 인지 구분
    // 추가와 북마크 편집은 true , 카테고리편집은 false 로 구분한다.
    private boolean mIsBookmark;

    // 현재 화면이 편집인지 추가인지 구분
    private boolean mIsEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 상태바 제거하고 전체화면 모드로
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.edit_add_item_popup_act);

        // edit 레이아웃이 담겨있는 리니어 레이아웃
        mEditPopupLinear = (LinearLayout) findViewById(R.id.popup_edit_item_linear);
        // add 프래그먼트를 담을 content 리니어를 가지고있는 리니어레이아웃
        mFragmentPopupLinear = (LinearLayout) findViewById(R.id.popup_fragment_linear);
        // 롱클릭된 아이템이 북마크일때만 표시할 리니어 레이아웃
        mEditPopupUrlLinear = (LinearLayout) findViewById(R.id.popup_edit_item_url_linear);


        //인텐트 구분자 확인
        mIntentViewType = getIntent().getType();
        // 인텐트로 구분하여 편집 화면 (카테고리 or 북마크) 또는 추가화면 구현
        setEditOrAddView();

    }

    @Override
    protected void onDestroy() {
        if(mViewModel!=null){
            mViewModel.onActivityDestroyed();
        }
        super.onDestroy();
    }



    /**
     * 1. 아이템 추가 팝업 셋팅
     * 2. 카테고리 편집 팝업 셋팅
     * 3. 북마크 편집 팝업 셋팅
     *
     * 추가 화면 관련 메소드
     * findOrCreateViewFragment() , findOrCreateViewModel
     * -> 추가 화면일 경우 프래그먼트를 사용하기 때문에 프래그먼트와 뷰모델을 생성한다.
     *
     * 편집화면 관련 메소드
     * setEditLinear
     * -> 편집일경우 카테고리와 북마크를 구분하기위해 사용되는 메소드
     * setEditButtonListener
     * -> 편집 화면의 버튼들의 리스너를 생성한다.
     *
     * 편집과 추가 화면을 구분하여 실행하는 메소드
     * setEditOrAddView
     * -> 최초에 편집과 추가 화면을 구분하여 셋팅하는 메소드
     * setLinearLayout
     * -> 편집과 추가 뷰를 구분하여 표시하고 편집일 경우 리니어의 크기지정
     * setFragmentViewModel
     * -> 편집 (북마크, 카테고리) 과 추가 뷰를 구분하여 프래그먼트와 뷰모델 생성
     **/
    private void setEditOrAddView(){

        switch (mIntentViewType){

            case ADD_ITEM :
                mIsEdit = false;
                mIsBookmark = true;
                mItemId = "";
                // 프래그먼트와 뷰모델 생성
                setFragmentViewModel();
                break;

            case EDIT_CATEGORY:
                // 편집 레이아웃 셋팅
                mIsBookmark = false;
                mIsEdit = true;
                mItemId = getIntent().getStringExtra(ID);
                setEditLinear(View.GONE);
                break;

            case EDIT_BOOKMARK:
                // 편집 레이아웃 셋팅
                mIsBookmark = true;
                mIsEdit = true;
                mItemId = getIntent().getStringExtra(ID);
                setEditLinear(View.VISIBLE);
                break;

        }
    }

    // Linear Layout 셋팅 및 크기 조절
    private void setLinearLayout(LinearLayout layout){

        // Edit 뷰 셋팅 시 화면 width 가 조절이 안됨
        if(mIsEdit){
            // 리니어 visibility 설정
            mEditPopupLinear.setVisibility(View.VISIBLE);
            mFragmentPopupLinear.setVisibility(View.GONE);
            // 레이아웃 크기설정
            // 너무 작게나와서 디바이스 화면 전체크기 구하고
            // 전체크기의 60 퍼센트로 셋팅
            layout.post(() -> {
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int screenWidth = metrics.widthPixels * 60 / 100;

                LinearLayout.LayoutParams position =
                        new LinearLayout.LayoutParams(screenWidth,
                                layout.getHeight());
                layout.setLayoutParams(position);
            });
        }else{
            // 프래그먼트 생성 시 문제없음
            // 리니어 visibility 만 설정
            mEditPopupLinear.setVisibility(View.GONE);
            mFragmentPopupLinear.setVisibility(View.VISIBLE);
        }
    }

    // 프래그먼트 생성 구분
    // 추가, 북마크편집, 카테고리편집 구분
    private void setFragmentViewModel(){

        // Popup 창 프래그먼트와 뷰모델 연결
        EditAddItemPopupFragment addItemPopupFragment = findOrCreateViewFragment();


        // 인텐트로 전달받은 카테고리 리스트
        ArrayList<String> mTitleList = getIntent().getStringArrayListExtra(CATEGORY_LIST);
        // 선택된 카테고리
        int mSelectCategory = getIntent().getIntExtra(SELECT_CATEGORY,0);
        // 프래그먼트의 스니퍼에 리스트 전달
        addItemPopupFragment.setSpinnerList(mTitleList,mSelectCategory);


        // 편집화면인지 추가화면인지 구분하기 위해 ViewType 전달
        addItemPopupFragment.setViewType(mIntentViewType);
        // 프래그먼트 뷰모델 생성
        mViewModel = findOrCreateViewModel();
        // 네비게이터 연결
        mViewModel.onActivityCreated(this);
        // 뷰모델 연결
        addItemPopupFragment.setViewModel(mViewModel);
        // 리니어 visibility 지정
        setLinearLayout(mFragmentPopupLinear);

    }

    // 추가 화면을 표시할 프레그먼트 찾거나 생성
    @NonNull
    private EditAddItemPopupFragment findOrCreateViewFragment(){
        EditAddItemPopupFragment fragment = (EditAddItemPopupFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content_frame);
        if(fragment==null){
            fragment = EditAddItemPopupFragment.newInstance();

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
    private EditItemPopupViewModel findOrCreateViewModel(){
        // ViewModelHolder(UI 없는 Fragment)
        // -> 뷰모델 생성 후 TAG 구분자를 입력
        @SuppressWarnings("unchecked")
        ViewModelHolder<EditItemPopupViewModel> retainedViewModel =
                (ViewModelHolder<EditItemPopupViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(mIntentViewType);
        // 입력한 tag 의 뷰모델이 존재한다면
        if(retainedViewModel != null && retainedViewModel.getViewModel() != null){
            //getViewModel() 로 가져와 리턴한다.
            return retainedViewModel.getViewModel();
        }else{
            // 입력한 TAG 의 뷰모델이 없다면 뷰모델을 생성하고 ViewModelHolder 에 추가한다.
            // 로컬 데이터소스 생성, 원격데이터소스 생성, 액티비티 context 입력
            EditItemPopupViewModel viewModel = new EditItemPopupViewModel(
                    Injection.provideBookmarksRepository(getApplicationContext()),
                    Injection.provideCategoriesRepository(getApplicationContext()),
                    getApplicationContext(),
                    mIsBookmark,
                    mIntentViewType,
                    mItemId
            );
            // ViewModelHolder(UI 없는 Fragment) 생성
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    ViewModelHolder.createViewModelHolder(viewModel),
                    mIntentViewType
            );
            return viewModel;
        }
    }

    // edit 화면일 경우 셋팅
    private void setEditLinear(int urlVisibility){
        // 화면 유형 구분 (카테고리 or 북마크)
        TextView mItemTypeTextView = (TextView) findViewById(R.id.tv_edit_item_type);
        // 타이틀 표시
        TextView mTitleTextView = (TextView) findViewById(R.id.tv_edit_item_title);
        // url 표시
        TextView mUrlTextView = (TextView) findViewById(R.id.tv_edit_item_url);
        // 리니어 크기와 visibility 지정
        setLinearLayout(mEditPopupLinear);
        // 버튼 리스너 셋팅
        setEditButtonListener();

        // 타이틀 셋팅
        mTitleTextView.setText(getIntent().getStringExtra(TITLE));
        if(!mIsBookmark){
            // 카테고리일 경우 타입 표시
            mItemTypeTextView.setText("카테고리");
            // url text view visible
            mEditPopupUrlLinear.setVisibility(urlVisibility);
        }else{
            // 북마크일 경우 타입표시
            mItemTypeTextView.setText("북마크");
            // url text view visible
            mEditPopupUrlLinear.setVisibility(urlVisibility);
            mUrlTextView.setText(getIntent().getStringExtra(URL));
        }
    }

    // 편집화면에서 버튼 리스너 셋팅
    private void setEditButtonListener(){

        // 편집버튼
        Button mEditBtn = (Button) findViewById(R.id.btn_edit);
        mEditBtn.setOnClickListener(v -> {
            // 프래그먼트 생성하기 위해 false 로 변경
            mIsEdit = false;
            // 프래그먼트와 뷰모델 생성
            setFragmentViewModel();
        });

        // 삭제버튼
        Button mDeleteBtn = (Button) findViewById(R.id.btn_delete);
        mDeleteBtn.setOnClickListener(v -> {
            Toast.makeText(this,
                    "삭제", Toast.LENGTH_SHORT).show();
        });

        // 공유버튼
        Button mShareBtn = (Button) findViewById(R.id.btn_share);
        mShareBtn.setOnClickListener(v -> {
            Toast.makeText(this,
                    "공유", Toast.LENGTH_SHORT).show();
        });

        // 확인버튼
        Button mCompleteBtn = (Button) findViewById(R.id.btn_complete);
        mCompleteBtn.setOnClickListener(v -> finish());

    }


    /**
     * EditAddItemPopupNavigator 인터페이스 오버라이드 메소드
     **/

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
