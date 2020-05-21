package com.jroomstudio.smartbookmarkeditor.data.category;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

/**
 * Category 불변 모델 클래스
 * - category 의 상태를 Room 데이터베이스에 저장하고 관리할 모델 클래스
 **/
@Entity(tableName = "categories")
public final class Category {

    /**
     * - categories 테이블에 category 가 저장될때 구분지을 프라이머리 키
     * - null 허용하지않음
     * - column = id , 변수타입 = String
     **/
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    @SerializedName("category_id")
    private final String mId;

    /**
     * - category 의 타이틀
     * - null 허용하지않음
     * - column = title , 변수타입 = String
     **/
    @NonNull
    @ColumnInfo(name = "title")
    @SerializedName("category_title")
    private final String mTitle;


    /**
     * - categories 테이블에서의 객체 위치
     */
    @ColumnInfo(name = "position")
    @SerializedName("category_position")
    private final int mPosition;

    /**
     * - category 가 현재 선택된 카테고리인지 구분
     **/
    @ColumnInfo(name = "selected")
    @SerializedName("category_selected")
    private final boolean mSelected;

    /**
     * 새로운 카테고리 생성 시 사용
     * @param title - bookmark 제목
     * @param position - category 내부에서의 리스트 순서
     **/
    @Ignore // -> 저장하지 않고 싶은 필드
    public Category(@NonNull String title,int position,boolean selected){
        this(UUID.randomUUID().toString(),title,position,selected);
    }


    /**
     * - 생성자
     * - 객체가 새로 만들어지거나 정보를 가져와 셋팅할 때 사용함
     **/
    public Category(@NonNull String id, @NonNull String title, int position, boolean selected) {
        this.mId = id;
        this.mTitle = title;
        this.mPosition = position;
        this.mSelected = selected;
    }

    @NonNull
    public String getId() { return mId; }

    @NonNull
    public String getTitle() { return mTitle; }

    public int getPosition() { return mPosition; }

    public boolean isSelected() { return mSelected; }


    public boolean isEmpty(){
        return Strings.isNullOrEmpty(mTitle);
    }

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
        Category category = (Category) obj;
        return  Objects.equal(mId, category.mId) &&
                Objects.equal(mTitle, category.mTitle) &&
                Objects.equal(mPosition,category.mPosition);
    }

    @Override
    public int hashCode() { return Objects.hashCode(mId,mTitle,mPosition); }

    @NonNull
    @Override
    public String toString() {
        return mTitle+"\n"+ mPosition+"\n" + mSelected+"\n" ; }

}
