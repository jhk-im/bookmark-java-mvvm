package com.jroomstudio.smartbookmarkeditor.itemtouch;

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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * - ItemTouchEditActivity 에서 사용할 데이터를 노출한다.
 *
 * {@link BaseObservable}
 * - 속성이 변경될 때 알림을 받는 리스너 등록 메커니즘을 구현
 **/
public class ItemTouchEditViewModel extends BaseObservable {

    /**
     * Observable
     * 해당 뷰모델과 연결된 액티비티의 UI 를 관찰하고 컨트롤한다.
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

    /**
     * - 로컬 , 원격에서 데이터를 액세스
     **/
    // 북마크
    private BookmarksLocalRepository mBookmarksLocalRepository;
    // 카테고리
    private CategoriesLocalRepository mCategoriesRepository;
    // 북마크 원격 데이터 소스
    private BookmarksRemoteRepository mBookmarksRemoteRepository;
    // 네비게이터
    private ItemTouchEditNavigator mNavigator;
    // 네비게이터 셋팅 - 프래그먼트와 뷰모델 생성시
    void setNavigator(ItemTouchEditNavigator navigator){ mNavigator = navigator; }
    // 액티비티 상태저장 Shared Preferences
    private SharedPreferences spActStatus;
    /**
     * mNavigator  메소드
     **/
    // 네비게이터 null 셋팅
    void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mNavigator = null;
    }

    // fab 버튼 누르고 편집 종료
    // -> 액티비티에서 종료를 진행한다.
    private void onItemsSaved(){
        if(mNavigator != null){
            Toast.makeText(mContext, "아이템 포지션 업데이트", Toast.LENGTH_SHORT).show();
            mNavigator.onItemsSaved();
        }
    }

    /**
     * ItemTouchEditViewModel 생성자
     * @param bookmarksLocalRepository - 북마크 로컬 데이터 액세스
     * @param categoriesRepository - 카테고리 로컬, 원격 데이터 액세스
     * @param context - 응용프로그램 context 를 강제로 사용함
     **/
    ItemTouchEditViewModel(BookmarksLocalRepository bookmarksLocalRepository,
                           CategoriesLocalRepository categoriesRepository, Context context,
                           SharedPreferences sharedPreferences,
                           BookmarksRemoteRepository bookmarksRemoteRepository){
        mBookmarksLocalRepository = bookmarksLocalRepository;
        mCategoriesRepository = categoriesRepository;
        mContext = context.getApplicationContext();
        spActStatus = sharedPreferences;
        mBookmarksRemoteRepository = bookmarksRemoteRepository;
    }

    // 프래그먼트 onResume 에서 실행
    void start() {
        Toast.makeText(mContext, "롱클릭으로 아이템 순서변경", Toast.LENGTH_SHORT).show();
        if(!spActStatus.getBoolean("login_status",false)){
            // 게스트 유저
            loadLocalCategories();
        }else{
            // 회원 유저
            loadRemoteCategories();
        }
    }

    // 포지션 변경후 fab 버튼 누르고 종료
    public void updatePosition(){
        if(!spActStatus.getBoolean("login_status",false)){
            // 게스트 유저
            // 북마크 포지션 값 변경
            for(Bookmark bookmark : bookmarkItems){
                // 포지션값 변경
                mBookmarksLocalRepository.updatePosition(bookmark,bookmarkItems.indexOf(bookmark));
            }
            // 카테고리 포지션값 변경
            for(Category category : categoryItems){
                // 포지션값 변경
                mCategoriesRepository.updatePosition(category,categoryItems.indexOf(category));
            }
            // 저장하고 종료
            onItemsSaved();
        }else{
            mBookmarksRemoteRepository.updateCategoryPosition(categoryItems,
                    new BookmarksRemoteDataSource.UpdateCallback() {
                        @Override
                        public void onCompletedUpdate() {
                            mBookmarksRemoteRepository.updateBookmarkPosition(bookmarkItems,
                                    new BookmarksRemoteDataSource.UpdateCallback() {
                                        @Override
                                        public void onCompletedUpdate() {
                                            // 저장하고 종료
                                            onItemsSaved();
                                        }

                                        @Override
                                        public void onFailedUpdate() {
                                            Toast.makeText(mContext, "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        @Override
                        public void onFailedUpdate() {
                            Toast.makeText(mContext, "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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
                                currentCategory.set(category);
                            }
                        }

                        if(currentCategory.get() == null){
                            Category c = new Category("",0,false);
                            currentCategory.set(c);
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
     * 데이터베이스에서 카테고리 정보 받아오기
     * -> 프래그먼트가 시작될 때 실행된다.
     *  -> 카테고리를 먼저 받아온다.
     *   -> 카테고리 중 선택된 카테고리를 찾아 저장한다.
     *    -> 선택된 카테고리에 해당하는 북마크를 로드한다.
     **/
    private void loadLocalCategories()
    {
        // 카테고리
        mCategoriesRepository.getCategories(new CategoriesLocalDataSource.LoadCategoriesCallback() {
            @Override
            public void onCategoriesLoaded(List<Category> categories) {

                for(Category category : categories){
                    if(category.isSelected()){
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
                //onDataNotAvailable();
            }
        });
    }

    // 데이터베이스에서 북마크 로드
    private void loadLocalBookmarks(){
        // 현재 선택된 카테고리 북마크 가져오기
        //mBookmarksLocalRepository.refreshBookmarks();
        mBookmarksLocalRepository.getBookmarks(Objects.requireNonNull(currentCategory.get().getTitle()),
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


}
