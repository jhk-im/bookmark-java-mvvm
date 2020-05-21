package com.jroomstudio.smartbookmarkeditor;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;

import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.local.BookmarksLocalDataSource;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.local.BookmarksLocalRepository;

/**
 * 북마크 단일객체를 아이템 뷰모델에서 표시하기위해 상속받아 구현하는 추상클래스
 * 아이템은 여러개로 늘어나기 때문에 추상 클래스를 상속받아서 구현한다.
**/
public abstract class BookmarkViewModel extends BaseObservable
        implements BookmarksLocalDataSource.GetBookmarkCallback {

    // 북마크 아이템의 TITLE 관찰
    public final ObservableField<String> title = new ObservableField<>();

    // 북마크 아이템의 URL 관찰
    public final ObservableField<String> url = new ObservableField<>();

    // 북마크 아이템의 이미지 url 관찰
    public final ObservableField<String> faviconUrl = new ObservableField<>();

    // 북마크 단일 객체 관찰
    private final ObservableField<Bookmark> mBookmarkObservable = new ObservableField<>();

    // 북마크 원격, 로컬 데이터 소스 멤버변수
    private final BookmarksLocalRepository mBookmarksLocalRepository;

    // context 멤버변수
    private final Context mContext;

    // 북마크 뷰모델 생성자
    public BookmarkViewModel(Context context, BookmarksLocalRepository bookmarksLocalRepository){
        mContext = context;
        mBookmarksLocalRepository = bookmarksLocalRepository;
        // 노출 된 관찰 가능 항목은 mBookmarkObservable 관찰 가능 항목에 따라 다르다.
        mBookmarkObservable.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Bookmark bookmark = mBookmarkObservable.get();
                if(bookmark != null){
                    title.set(bookmark.getTitle());
                    url.set(bookmark.getUrl());
                    faviconUrl.set(bookmark.getFaviconUrl());
                }

            }
        });
    }

    // 시작하면서 id 로 bookmark 단일 객체 찾기
    public void start(String id){
        if(id != null){
            mBookmarksLocalRepository.getBookmark(id,this);
        }
    }

    // 객체 관찰 변수에 북마크 객체 추가
    public void setBookmark(Bookmark bookmark){ mBookmarkObservable.set(bookmark); }


    // GetBookmarkCallback 에서 데이터 찾았을 때
    @Override
    public void onBookmarkLoaded(Bookmark bookmark) {
        mBookmarkObservable.set(bookmark);
        notifyChange(); // For the @Bindable properties
    }

    // GetBookmarkCallback 에서 데이터 없을때
    @Override
    public void onDataNotAvailable() {
        mBookmarkObservable.set(null);
    }


    // 객체반환
    protected Bookmark getBookmark() { return mBookmarkObservable.get(); }
}
