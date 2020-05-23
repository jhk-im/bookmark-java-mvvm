package com.jroomstudio.smartbookmarkeditor.main.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.jroomstudio.smartbookmarkeditor.BR;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.local.BookmarksLocalDataSource;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.local.BookmarksLocalRepository;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.remote.BookmarksRemoteDataSource;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.remote.BookmarksRemoteRepository;
import com.jroomstudio.smartbookmarkeditor.data.category.Category;
import com.jroomstudio.smartbookmarkeditor.data.category.source.local.CategoriesLocalDataSource;
import com.jroomstudio.smartbookmarkeditor.data.category.source.local.CategoriesLocalRepository;
import com.jroomstudio.smartbookmarkeditor.main.MainHomeNavigator;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * - 메인 액티비티에서 사용할 데이터를 노출한다.
 *
 * {@link BaseObservable}
 * - 속성이 변경될 때 알림을 받는 리스너 등록 메커니즘을 구현
 **/
public class MainHomeViewModel extends BaseObservable {

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
    // 현재 선택된 카테고리 옵저버블 변수
    public final ObservableField<Category> currentCategory = new ObservableField<>();

    /**
     * - 해당 뷰모델과 연결될 액티비티,프래그먼트 의 Context
     * - leak 을 피하려면 응용 프로그램 context 여야 한다.
     **/
    private Context mContext;

    // 액티비티 상태저장 Shared Preferences
    private SharedPreferences spActStatus;

    /**
     * - 로컬 , 원격에서 데이터를 액세스
     **/
    // 북마크
    private BookmarksLocalRepository mBookmarksLocalRepository;
    // 카테고리
    private CategoriesLocalRepository mCategoriesRepository;

    // 북마크 원격 카테고리
    private BookmarksRemoteRepository mBookmarksRemoteRepository;

    // 액티비티 아이템 네비게이터
    private MainHomeNavigator mNavigator;

    // 네비게이터 셋팅
    public void setNavigator(MainHomeNavigator navigator) { mNavigator = navigator; }

    /**
     * mNavigator -> ItemNavigator 메소드
     **/
    // 네비게이터 null 셋팅
    public void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mNavigator = null;
    }

    // 프래그먼트 옵션메뉴 -> + 버튼 클릭
    // 액티비티 네비게이터 메소드 실행
    // -> 새로운 아이템 추가하는 팝업이 실행된다.
    void addNewItem(){
        if(mNavigator != null){
            // 카테고리 리스트를 메인액티비티의 addNewItems 로 전달
            mNavigator.addNewItems(categoryItems);
        }
    }


    /**
     * 카테고리, 북마크의 롱클릭 메소드
     * - 메인 액티비티로 선택된 카테고리, 북마크, 카테고리 리스트를 전달한다.
     **/

    // 카테고리 아이템 롱클릭하여 편집팝업 띄우기
    public void editLongClickCategory(Category category){
        if(mNavigator != null){
            // 선택된 카테고리 객체를 메인액티비티의 editSelectCategory 로 전달
            mNavigator.editCategory(category,categoryItems);
        }
    }
    // 북마크 리사이클러뷰의 아이템 클릭
    // 북마크 아이템 롱클릭하여 편집팝업 띄우기
    public void editLongClickBookmark(Bookmark bookmark){
        if(mNavigator != null){
            // 선택된 북마크 객체를 메인 액티비티의 editSelectBookmark 로 전달
            mNavigator.editBookmark(bookmark,categoryItems);
        }
    }

    /**
     * Main Activity ViewModel 생성자
     * @param bookmarksLocalRepository - 북마크 로컬 액세스
     * @param categoriesRepository - 카테고리 로컬, 원격 데이터 액세스
     * @param context - 응용프로그램 context 를 강제로 사용함
     **/
    public MainHomeViewModel(BookmarksLocalRepository bookmarksLocalRepository,
                             CategoriesLocalRepository categoriesRepository,
                             BookmarksRemoteRepository bookmarksRemoteRepository,
                             Context context, SharedPreferences sharedPreferences){
        mBookmarksLocalRepository = bookmarksLocalRepository;
        mCategoriesRepository = categoriesRepository;
        mBookmarksRemoteRepository = bookmarksRemoteRepository;
        mContext = context.getApplicationContext();
        spActStatus = sharedPreferences;
    }

    // 프래그먼트 onResume
    void start(){
        if(!spActStatus.getBoolean("login_status",false)){
            // 게스트 유저
            loadLocalCategories();
        }else{
            // 회원
            loadRemoteCategories();
        }
    }

    // 아이템 클릭시 실행
    public void changeSelectCategory(Category category){
        if(!spActStatus.getBoolean("login_status",false)){
            // 게스트 유저
            // 현재 카테고리 isSelected false 로 변경
            mCategoriesRepository.
                    selectedCategory(Objects.requireNonNull(currentCategory.get()),false);
            // 전달받은 카테고리 isSelected true 로 변경
            mCategoriesRepository.selectedCategory(category,true);
            loadLocalCategories();
        }else{
            // 회원
            mBookmarksRemoteRepository.selectedCategory(category.getTitle(),
                    new BookmarksRemoteDataSource.UpdateCallback() {
                @Override
                public void onCompletedUpdate() {
                    // 북마크 가져오기
                    loadRemoteCategories();
                }
                @Override
                public void onFailedUpdate() {
                    Toast.makeText(mContext, "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // 북마크 리스트를 position 값에 맞게 순서정렬
    private List<Bookmark> sortToBookmarks(List<Bookmark> bookmarks){
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
    private List<Category> sortToCategories(List<Category> categories){
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

    /**
     * 원격 데이터 베이스
    **/
    private void loadRemoteCategories(){
        mBookmarksRemoteRepository.getAllCategories(
                new BookmarksRemoteDataSource.LoadCategoriesCallback() {
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
                loadRemoteBookmarks();
            }

            @Override
            public void onDataNotAvailable() {
                // 카테고리 없음
                categoryItems.clear();
                notifyPropertyChanged(BR._all);
            }
        });
    }

    private void loadRemoteBookmarks(){
        mBookmarksRemoteRepository.getBookmarks(currentCategory.get().getTitle(),
                new BookmarksRemoteDataSource.LoadBookmarksCallback() {
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

    /**
     * 로컬 데이터베이스
     **/
    private void loadLocalCategories()
    {
        // 카테고리
        mCategoriesRepository.getCategories(new CategoriesLocalDataSource.LoadCategoriesCallback() {
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
                loadLocalBookmarks();
            }
            @Override
            public void onDataNotAvailable() {
                //onDataNotAvailable()
                // 카테고리 없음
                categoryItems.clear();
                notifyPropertyChanged(BR._all);
            }
        });
    }
    // 데이터베이스에서 북마크 로드
    private void loadLocalBookmarks(){
        // 현재 선택된 카테고리 북마크 가져오기
        //mBookmarksLocalDataSource.refreshBookmarks();
        mBookmarksLocalRepository.getBookmarks(currentCategory.get().getTitle(),
                new BookmarksLocalDataSource.LoadBookmarksCallback() {
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


}
