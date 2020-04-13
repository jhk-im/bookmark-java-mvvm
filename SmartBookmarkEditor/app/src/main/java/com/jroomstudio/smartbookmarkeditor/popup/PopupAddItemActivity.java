package com.jroomstudio.smartbookmarkeditor.popup;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.databinding.PopupAddItemActBinding;

import java.util.ArrayList;

public class PopupAddItemActivity extends Activity implements PopupAddItemNavigator {

    // Request code
    public static final int REQUEST_CODE = 1;

    // 인텐트로 전달받은 리스트 key
    public static final String CATEGORY_LIST = "CATEGORY_LIST";

    // 인텐트로 전달받은 카테고리 제목 리스트
    private ArrayList<String> mTitleList;

    // 액티비티의 데이터 바인딩
    private PopupAddItemActBinding mPopupAddItemActBinding;

    // 카테고리 or 북마크 선택
    boolean addCategory = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_add_item_act);

        // POP 창 액티비티 데이터바인딩
        View view = findViewById(R.id.popup_add_item_layout);
        mPopupAddItemActBinding = PopupAddItemActBinding.bind(view);
        mPopupAddItemActBinding.setViewmodel(new PopupAddItemActViewModel());

        // 라디오그룹 체크 리스너
        mPopupAddItemActBinding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.rb_bookmarks :
                    // 북마크 선택 시
                    setLinearContainer(LinearLayout.GONE,LinearLayout.VISIBLE,false);
                    break;
                case R.id.rb_category :
                    // 카테고리 선택 시
                    setLinearContainer(LinearLayout.VISIBLE,LinearLayout.GONE,true);
                    break;
            }
        });

        // 팝업 시작시 카테고리 라디오버튼 체크
        mPopupAddItemActBinding.radioGroup.check(R.id.rb_category);
        mTitleList = getIntent().getStringArrayListExtra(CATEGORY_LIST);

    }


    // 리니어 컨테이너 셋팅
    public void setLinearContainer(int categoryAction, int bookmarkAction, boolean isCategory){
        // 컨테이너 gone or visible 셋팅
        mPopupAddItemActBinding.contentLinearCategory.setVisibility(categoryAction);
        mPopupAddItemActBinding.contentLinearBookmark.setVisibility(bookmarkAction);
        // Edit text 초기화
        mPopupAddItemActBinding.etCategoryTitle.setText("");
        mPopupAddItemActBinding.etBookmarkTitle.setText("");
        mPopupAddItemActBinding.etBookmarkUrl.setText(R.string.https);
        addCategory = isCategory;
    }

    // 팝업이 시작될때
    @Override
    public void onStartPopupAddItem() {
        mTitleList = getIntent().getStringArrayListExtra(CATEGORY_LIST);
    }
}
