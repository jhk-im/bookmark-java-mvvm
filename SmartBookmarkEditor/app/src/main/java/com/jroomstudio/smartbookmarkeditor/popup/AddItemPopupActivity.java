package com.jroomstudio.smartbookmarkeditor.popup;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jroomstudio.smartbookmarkeditor.Injection;
import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.ViewModelHolder;
import com.jroomstudio.smartbookmarkeditor.util.ActivityUtils;

import java.util.ArrayList;

public class AddItemPopupActivity extends AppCompatActivity implements PopupAddItemNavigator {

    // Request code
    public static final int REQUEST_CODE = 1;

    // 인텐트로 전달받은 카테고리 리스트 key 값
    public static final String CATEGORY_LIST = "CATEGORY_LIST";

    // 카테고리 리스트
    private ArrayList<String> mTitleList = new ArrayList<>();

    // 뷰모델 생성 태그
    public static final String POP_ADD_ITEM_TAG = "POP_ADD_ITEM_TAG";

    // 뷰모델
    private AddItemPopupViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 상태바 제거하고 전체화면 모드로
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.add_item_popup_act);


        // 인텐트로 전달받은 카테고리 리스트
        mTitleList = getIntent().getStringArrayListExtra(CATEGORY_LIST);

        // Popup 창 프래그먼트와 뷰모델 연결
        AddItemPopupFragment addItemPopupFragment = findOrCreateViewFragment();
        // 프래그먼트의 스니퍼에 리스트 전달
        addItemPopupFragment.setSpinnerList(mTitleList);
        // 프래그먼트 뷰모델 생성
        mViewModel = findOrCreateViewModel();
        // 네비게이터 연결
        mViewModel.onActivityCreated(this);
        // 뷰모델 연결
        addItemPopupFragment.setViewModel(mViewModel);

    }

    @Override
    protected void onDestroy() {
        mViewModel.onActivityDestroyed();
        super.onDestroy();
    }

    // 프레그먼트 찾거나 생성
    @NonNull
    private AddItemPopupFragment findOrCreateViewFragment(){
        AddItemPopupFragment fragment = (AddItemPopupFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content_frame);
        if(fragment==null){
            fragment = AddItemPopupFragment.newInstance();

            // Send the task ID to the fragment
            Bundle bundle = new Bundle();
            bundle.putString(AddItemPopupFragment.ARGUMENT_EDIT_TASK_ID,
                    getIntent().getStringExtra(AddItemPopupFragment.ARGUMENT_EDIT_TASK_ID));
            fragment.setArguments(bundle);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    fragment, R.id.content_frame);
        }
        return fragment;
    }
    // 뷰모델 찾거나 생성
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

    // 아이템 추가 완료후 팝업 액티비티 종료
    @Override
    public void addNewItem() {
        finish();
    }

    // 취소버튼
    @Override
    public void cancelAddItem() {
        onBackPressed();
    }


}
