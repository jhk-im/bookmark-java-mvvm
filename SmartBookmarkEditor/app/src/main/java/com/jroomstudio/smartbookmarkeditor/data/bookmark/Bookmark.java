package com.jroomstudio.smartbookmarkeditor.data.bookmark;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.UUID;

/**
 * Bookmark 불변 모델 클래스
 * - bookmark 의 상태를 Room 데이터베이스에 저장하고 관리할 모델 클래스
 **/
@Entity(tableName = "bookmarks")
public final class Bookmark {

    /**
     * - bookmarks 테이블에 Bookmark 가 저장될때 구분지을 프라이머리 키
     * - null 허용하지않음
     * - column = id , 변수타입 = String
     **/
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private final String mId;

    /**
     * - bookmark 의 타이틀
     * - null 허용하지않음
     * - column = title , 변수타입 = String
     **/
    @NonNull
    @ColumnInfo(name = "title")
    private final String mTitle;

    /**
     * - bookmark 의 url 주소
     * - null 허용하지않음
     * - column = url , 변수타입 = String
     **/
    @NonNull
    @ColumnInfo(name = "url")
    private final String mUrl;

    /**
     * - bookmark 의 실행 액션 정의
     *(일반 - 기본 웹뷰로 실행 / 앱 - 전용 앱이 있는경우 앱으로 실행)
     * - null 허용하지않음
     * - column = action , 변수타입 = String
     **/
    @NonNull
    @ColumnInfo(name = "action")
    private final String mAction;

    /**
     * - bookmark 가 어떤 카테고리에 있는지 정의
     * - null 허용하지않음
     * - column = category
     **/
    @NonNull
    @ColumnInfo(name = "category")
    private final String mCategory;

    /**
     * - bookmark 의 카테고리 상의 순서
     */
    @ColumnInfo(name = "position")
    private final int mPosition;


    /**
     * 새로운 북마크를 생성할 때 사용함
     * @param title - bookmark 제목
     * @param url - bookmark 주소
     * @param action - bookmark 실행액션
     * @param category - bookmark 가 속한 카테고리
     * @param position - category 내부에서의 리스트 순서
     **/
    @Ignore // -> 저장하지 않고 싶은 필드
    public Bookmark(@NonNull String title, @NonNull String url,
                    @NonNull String action, @NonNull String category, int position){
        this(UUID.randomUUID().toString(),title,url,action,category,position);
    }

    /**
     * - 생성자
     * - 객체가 새로 만들어지거나 정보를 가져와 셋팅할 때 사용함
     **/
    public Bookmark(@NonNull String id, @NonNull String title,
                    @NonNull String url, @NonNull String action,
                    @NonNull String category, int position) {
        this.mId = id;
        this.mTitle = title;
        this.mUrl = url;
        this.mAction = action;
        this.mCategory = category;
        this.mPosition = position;
    }

    @NonNull
    public String getId() { return mId; }

    @NonNull
    public String getTitle() { return mTitle; }

    @NonNull
    public String getUrl() { return mUrl; }

    @NonNull
    public String getAction() { return mAction; }

    @NonNull
    public String getCategory() { return mCategory; }

    public int getPosition() { return mPosition; }

    public String getTitleForList() {
        if (!Strings.isNullOrEmpty(mTitle)){
            return mTitle;
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Bookmark bookmark = (Bookmark) obj;
        return  Objects.equal(mId,bookmark.mId) &&
                Objects.equal(mTitle,bookmark.mTitle) &&
                Objects.equal(mUrl,bookmark.mUrl) &&
                Objects.equal(mAction,bookmark.mAction) &&
                Objects.equal(mCategory,bookmark.mCategory) &&
                Objects.equal(mPosition,bookmark.mPosition);
    }

    @Override
    public int hashCode() { return Objects.hashCode(mId,mTitle,mUrl,mAction,mCategory,mPosition); }

    @NonNull
    @Override
    public String toString() { return "Bookmark with title "+ mTitle; }
}
