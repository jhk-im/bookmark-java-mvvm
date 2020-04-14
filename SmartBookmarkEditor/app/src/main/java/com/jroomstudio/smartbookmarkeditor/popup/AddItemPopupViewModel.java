package com.jroomstudio.smartbookmarkeditor.popup;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksRepository;
import com.jroomstudio.smartbookmarkeditor.data.category.Category;
import com.jroomstudio.smartbookmarkeditor.data.category.source.CategoriesRepository;

import java.util.Objects;

public class AddItemPopupViewModel extends BaseObservable {

    // 카테고리 타이틀 입력 관찰
    public final ObservableField<String> categoryTitle = new ObservableField<>();
    // 북마크 타이틀 입력 관찰변수
    public final ObservableField<String> bookmarkTitle = new ObservableField<>();
    // 북마크 url 입력 관찰 변수
    public final ObservableField<String> bookmarkUrl = new ObservableField<>();
    // 북마크 카테고리 스피너 현재 아이템 관찰변수
    public final ObservableField<String> bookmarkCategory = new ObservableField<>();
    // 카테고리 or 북마크
    public final ObservableBoolean isCategory = new ObservableBoolean();

    // 카테고리 리스트
    public final ObservableList<String> categories = new ObservableArrayList<>();

    // 액티비티 네비게이터
    private PopupAddItemNavigator mNavigator;
    // 액티비티 시작시 네비게이터 셋팅
    void onActivityCreated(PopupAddItemNavigator navigator){ mNavigator = navigator; }
    // 액티비티 종료시 네비게이터 종료
    void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mNavigator = null;
    }



    // 북마크 데이터 소스
    private BookmarksRepository mBookmarksRepository;
    // 카테고리 데이터 소스
    private CategoriesRepository mCategoriesRepository;
    // To avoid leaks, this must be an Application Context.
    private Context mContext;
    /**
     * ViewModel 생성자
     * @param bookmarksRepository - 북마크 로컬, 원격 데이터 액세스
     * @param categoriesRepository - 카테고리 로컬, 원격 데이터 액세스
     * @param context - 응용프로그램 context 를 강제로 사용함
     **/
    public AddItemPopupViewModel(BookmarksRepository bookmarksRepository,
                                    CategoriesRepository categoriesRepository, Context context) {
        mBookmarksRepository = bookmarksRepository;
        mCategoriesRepository = categoriesRepository;
        mContext = context.getApplicationContext();
        isCategory.set(true);
    }

    // 프래그먼트 onResume 때 실행
    public void start() {

    }

    // 아이템이 저장되었으니 종료
    private void navigationAddNewItem(){
        if(mNavigator!=null){
            mNavigator.addNewItem();
        }
    }

    // 취소버튼 클릭
    public void cancelButtonOnClick(){

        if(mNavigator!=null){
            mNavigator.cancelAddItem();
        }
    }

    // 확인버튼 클릭
    public void okButtonOnClick(){
        createItem();
    }

    // 아이템 생성 (카테고리와 북마크 구분)
    private void createItem(){
        if(isCategory.get()){
            //카테고리 생성
            createCategory();
        }else{
            //북마크
            Log.e("current",bookmarkCategory.get());
            Log.e("title",bookmarkTitle.get());
            Log.e("URL",bookmarkUrl.get());
            Log.e("isCategory",isCategory.get()+"");
        }
    }

    // 카테고리 저장
    private void createCategory(){
        // 카테고리 제목 null 체크
        if(categoryTitle.get().equals("")){
            Toast.makeText(mContext, "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
            // 비어있는 값 저장안함
            return;
        }

        // 카테고리 중복체크
        for(String title : categories){
            if(Objects.equals(categoryTitle.get(), title)){
                Toast.makeText(mContext, "중복된 카테고리", Toast.LENGTH_SHORT).show();
                // 중복되면 저장안함
                return;
            }
        }

        // 중복 아니면 저장진행
        Category newCategory = new Category(
                Objects.requireNonNull(categoryTitle.get()),
                categories.size(),
                false);
        mCategoriesRepository.saveCategory(newCategory);
        navigationAddNewItem();
        Toast.makeText(mContext, "카테고리 생성", Toast.LENGTH_SHORT).show();
    }

}
