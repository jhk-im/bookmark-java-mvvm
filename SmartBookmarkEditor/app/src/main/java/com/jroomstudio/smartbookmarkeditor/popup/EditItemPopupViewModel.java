package com.jroomstudio.smartbookmarkeditor.popup;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;

import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksRepository;
import com.jroomstudio.smartbookmarkeditor.data.category.source.CategoriesRepository;

public class EditItemPopupViewModel extends BaseObservable {

    /**
     * 뷰관찰변수
     **/
    // 현재 아이템의 타이틀
    public final ObservableField<String> itemTitle = new ObservableField<>();
    public final ObservableField<String> itemId = new ObservableField<>();
    public final ObservableField<String> itemType = new ObservableField<>();



    // 북마크 데이터 소스
    private BookmarksRepository mBookmarksRepository;
    // 카테고리 데이터 소스
    private CategoriesRepository mCategoriesRepository;
    // To avoid leaks, this must be an Application Context.
    private Context mContext;
    // 액티비티 네비게이터
    private EditAddItemPopupNavigator mNavigator;

    /**
     * ViewModel 생성자
     * @param bookmarksRepository - 북마크 로컬, 원격 데이터 액세스
     * @param categoriesRepository - 카테고리 로컬, 원격 데이터 액세스
     * @param context - 응용프로그램 context 를 강제로 사용함
     **/
    public EditItemPopupViewModel(BookmarksRepository bookmarksRepository,
                                 CategoriesRepository categoriesRepository, Context context) {
        mBookmarksRepository = bookmarksRepository;
        mCategoriesRepository = categoriesRepository;
        mContext = context.getApplicationContext();
    }

    // 액티비티 시작시 네비게이터 셋팅
    void onActivityCreated(EditAddItemPopupNavigator navigator){ mNavigator = navigator; }
    // 액티비티 종료시 네비게이터 종료
    void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mNavigator = null;
    }



}
