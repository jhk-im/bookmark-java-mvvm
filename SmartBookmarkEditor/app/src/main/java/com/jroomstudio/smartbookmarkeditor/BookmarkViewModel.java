package com.jroomstudio.smartbookmarkeditor;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;

import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksDataSource;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksRepository;

/**
 * 북마크 단일객체를 아이템 뷰모델에서 표시하기위해 상속받아 구현하는 추상클래스
 * 아이템은 여러개로 늘어나기 때문에 추상 클래스를 상속받아서 구현한다.
**/
public abstract class BookmarkViewModel extends BaseObservable
        implements BookmarksDataSource.GetBookmarkCallback {

    // 북마크 아이템의 TITLE 관찰
    public final ObservableField<String> title = new ObservableField<>();

    // 북마크 아이템의 URL 관찰
    public final ObservableField<String> url = new ObservableField<>();

    // 북마크 아이템의 이미지 url 관찰
    // public final ObservableField<String> imgUrl = new ObservableField<>();

    // 북마크 단일 객체 관찰
    private final ObservableField<Bookmark> mBookmarkObservable = new ObservableField<>();

    // 북마크 원격, 로컬 데이터 소스 멤버변수
    private final BookmarksRepository mBookmarksRepository;

    // context 멤버변수
    private final Context mContext;

    // 북마크 뷰모델 생성자
    public BookmarkViewModel(Context context, BookmarksRepository bookmarksRepository){
        mContext = context;
        mBookmarksRepository = bookmarksRepository;

        // 노출 된 관찰 가능 항목은 mBookmarkObservable 관찰 가능 항목에 따라 다르다.
        mBookmarkObservable.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Bookmark bookmark = mBookmarkObservable.get();
                if(bookmark != null){
                    title.set(bookmark.getTitle());
                    url.set(bookmark.getUrl());
                }
            }
        });
    }

    // 시작하면서 id 로 bookmark 단일 객체 찾기
    public void start(String id){
        if(id != null){
            mBookmarksRepository.getBookmark(id,this);
        }
    }

    // 객체 관찰 변수에 북마크 객체 추가
    public void setBookmark(Bookmark bookmark){ mBookmarkObservable.set(bookmark); }

    // 객체 널 체크
    @Bindable
    public boolean isDataAvailable() { return mBookmarkObservable.get() != null; }


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

    // 객체삭제
    public void deleteBookmark() {
        if(mBookmarkObservable.get() != null){
            mBookmarksRepository.deleteBookmark(mBookmarkObservable.get().getId());
        }
    }

    // 객체 refresh
    public void onRefresh() {
        if(mBookmarkObservable.get() != null){
            start(mBookmarkObservable.get().getId());
        }
    }

    // 객체의 id 반환
    @Nullable
    protected String getBookmarkId() { return mBookmarkObservable.get().getId(); }

}
