package com.jroomstudio.smartbookmarkeditor.main;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.databinding.library.baseAdapters.BR;

import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksDataSource;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksRepository;
import com.jroomstudio.smartbookmarkeditor.data.category.Category;
import com.jroomstudio.smartbookmarkeditor.data.category.source.CategoriesDataSource;
import com.jroomstudio.smartbookmarkeditor.data.category.source.CategoriesRepository;

import java.util.ArrayList;
import java.util.List;

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
    // test 용
    public final ObservableField<String> testCategories = new ObservableField<>();
    public final ObservableField<String> testBookmarks = new ObservableField<>();

    // 북마크 리스트 옵저버블 변수
    public final ObservableList<Bookmark> bookmarkItems = new ObservableArrayList<>();
    // 카테고리 리스트 옵저버블 변수
    public final ObservableList<Category> categoryItems = new ObservableArrayList<>();

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
        loadDatabase(false);
    }

    // 데이터베이스 정보 받아오기
    private void loadDatabase(boolean forceUpdate)
    {
        if(forceUpdate){
            mCategoriesRepository.refreshCategories();
            mBookmarksRepository.refreshBookmarks();
        }
        // 북마크
        mBookmarksRepository.getBookmarks(new BookmarksDataSource.LoadBookmarksCallback() {
            @Override
            public void onBookmarksLoaded(List<Bookmark> bookmarks) {
                List<Bookmark> bookmarksToShow = new ArrayList<Bookmark>();
                for(Bookmark bookmark : bookmarks){
                    bookmarksToShow.add(bookmark);
                }
                bookmarkItems.clear();
                bookmarkItems.addAll(bookmarksToShow);
                notifyPropertyChanged(BR._all);


                // test tv 에 표시
                //testBookmarks.set(bookmarks.toString());
            }

            @Override
            public void onDataNotAvailable() { testBookmarks.set("Bookmarks - load failed"); }
        });
        // 카테고리
        mCategoriesRepository.getCategories(new CategoriesDataSource.LoadCategoriesCallback() {
            @Override
            public void onCategoriesLoaded(List<Category> categories) {
                List<Category> categoriesToShow = new ArrayList<Category>();
                for(Category category : categories){
                    categoriesToShow.add(category);
                }
                categoryItems.clear();
                categoryItems.addAll(categoriesToShow);
                notifyPropertyChanged(BR._all);

                // test tv 에 표시
                //testCategories.set(categoriesToShow.toString());
            }

            @Override
            public void onDataNotAvailable() { testBookmarks.set("Categories - load failed"); }
        });
    }

    void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
    }
}
