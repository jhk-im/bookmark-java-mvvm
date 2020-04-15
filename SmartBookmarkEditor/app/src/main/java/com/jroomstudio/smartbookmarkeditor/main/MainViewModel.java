package com.jroomstudio.smartbookmarkeditor.main;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.jroomstudio.smartbookmarkeditor.BR;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksDataSource;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksRepository;
import com.jroomstudio.smartbookmarkeditor.data.category.Category;
import com.jroomstudio.smartbookmarkeditor.data.category.source.CategoriesDataSource;
import com.jroomstudio.smartbookmarkeditor.data.category.source.CategoriesRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * - 메인 액티비티에서 사용할 데이터를 노출한다.
 *
 * {@link BaseObservable}
 * - 속성이 변경될 때 알림을 받는 리스너 등록 메커니즘을 구현
 **/
public class MainViewModel extends BaseObservable {

    /**
     * Observable
     * 해당 뷰모델과 연결된 액티비티의 UI 를 관찰하고 컨트롤한다.
     *
     * main_act.xml 의 데이터 바인딩 뷰모델로 지정이 되어있기 때문에
     * Observable 로 선언한 변수를 main_act.xml 에서 연결 할 수 있다.
     **/

    // 북마크 리스트 옵저버블 변수
    public final ObservableList<Bookmark> bookmarkItems = new ObservableArrayList<>();
    // 카테고리 리스트 옵저버블 변수
    public final ObservableList<Category> categoryItems = new ObservableArrayList<>();
    // 선택된 카테고리 옵저버블 변수
    public final ObservableField<Category> currentCategory = new ObservableField<>();

    /**
     * - 해당 뷰모델과 연결될 액티비티,프래그먼트 의 Context
     * - leak 을 피하려면 응용 프로그램 context 여야 한다.
     **/
    private Context mContext;

    /**
     * - 로컬 , 원격에서 데이터를 액세스
     **/
    // 북마크
    private BookmarksRepository mBookmarksRepository;
    // 카테고리
    private CategoriesRepository mCategoriesRepository;

    // 액티비티 아이템 네비게이터
    private ItemNavigator mNavigator;

    // 네비게이터 셋팅
    void setNavigator(ItemNavigator navigator) { mNavigator = navigator; }

    // 네비게이터 null 셋팅
    void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mNavigator = null;
    }
    // 액티비티 네비게이터 메소드 실행
    // -> 새로운 아이템 추가하는 팝업이 실행된다.
    public void addNewItem(){
        if(mNavigator != null){
            mNavigator.addNewItems(categoryItems);
        }
    }

    /**
     * Main Activity ViewModel 생성자
     * @param bookmarksRepository - 북마크 로컬, 원격 데이터 액세스
     * @param categoriesRepository - 카테고리 로컬, 원격 데이터 액세스
     * @param context - 응용프로그램 context 를 강제로 사용함
     **/
    public MainViewModel(BookmarksRepository bookmarksRepository,
                         CategoriesRepository categoriesRepository, Context context){
        mBookmarksRepository = bookmarksRepository;
        mCategoriesRepository = categoriesRepository;
        mContext = context.getApplicationContext();
    }

    public void start(){
        loadCategories();
    }

    // 아이템 클릭시 실행
    public void changeSelectCategory(Category category){
        // 현재 카테고리 isSelected false 로 변경
        mCategoriesRepository.
                selectedCategory(Objects.requireNonNull(currentCategory.get()),false);
        // 전달받은 카테고리 isSelected true 로 변경
        mCategoriesRepository.selectedCategory(category,true);
        // 업데이트
        loadCategories();
    }

    /**
     * 데이터베이스에서 카테고리 정보 받아오기
     * -> 프래그먼트가 시작될 때 실행된다.
     *  -> 카테고리를 먼저 받아온다.
     *   -> 카테고리 중 선택된 카테고리를 찾아 저장한다.
     *    -> 선택된 카테고리에 해당하는 북마크를 로드한다.
     **/
    private void loadCategories()
    {
        /*
        if(forceUpdate){
            mCategoriesRepository.refreshCategories();
            mBookmarksRepository.refreshBookmarks();
        }
        */
        // 카테고리
        mCategoriesRepository.getCategories(new CategoriesDataSource.LoadCategoriesCallback() {
            @Override
            public void onCategoriesLoaded(List<Category> categories) {
                for(Category category : categories){
                    if(category.isSelected()){
                        // 액티비티 액션바 타이틀을 현재 선택된 카테고리로 업데이트
                        mNavigator.setToolbarTitle(category.getTitle());
                        currentCategory.set(category);
                    }
                }
                // 옵저버블 리스트에 추가
                // 카테고리 position 순서대로 정렬
                categoryItems.clear();
                categoryItems.addAll(sortToCategories(categories));
                notifyPropertyChanged(BR._all);
                // 북마크 가져오기
                loadBookmarks();
            }
            @Override
            public void onDataNotAvailable() {
                     //onDataNotAvailable();
            }
        });
    }

    // 데이터베이스에서 북마크 로드
    private void loadBookmarks(){
        // 현재 선택된 카테고리 북마크 가져오기
        mBookmarksRepository.getBookmarks(Objects.requireNonNull(currentCategory.get().getTitle()),
                new BookmarksDataSource.LoadBookmarksCallback() {
                    @Override
                    public void onBookmarksLoaded(List<Bookmark> bookmarks) {
                        // 옵저버블 리스트에 추가
                        // 북마크 포지션대로 정렬
                        bookmarkItems.clear();
                        bookmarkItems.addAll(sortToBookmarks(bookmarks));
                        notifyPropertyChanged(BR._all);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        // 해당 카테고리의 북마크가 없으면 리스트 비우기
                        bookmarkItems.clear();
                        //onDataNotAvailable();
                    }
                });
    }

    // 북마크 리스트를 position 값에 맞게 순서정렬
    public List<Bookmark> sortToBookmarks(List<Bookmark> bookmarks){
        Collections.sort(bookmarks, (o1, o2) -> {
            if(o1.getPosition() < o2.getPosition()){
                return -1;
            } else if (o1.getPosition() > o2.getPosition()){
                return 1;
            }
            return 0;
        });
        return bookmarks;
    }

    //카테고리 리스트를 position 값에 맞게 순서 정렬
    public List<Category> sortToCategories(List<Category> categories){
        Collections.sort(categories, (o1, o2) -> {
            if(o1.getPosition() < o2.getPosition()){
                return -1;
            } else if (o1.getPosition() > o2.getPosition()){
                return 1;
            }
            return 0;
        });
        return categories;
    }

}
