package com.jroomstudio.smartbookmarkeditor.data.notice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.common.base.Objects;

import java.util.UUID;

/**
 * Notice 불변 모델 클래스
 * - notice 의 상태를 room 데이터베이스에 저장하고 관리할 모델클래스
**/
@Entity(tableName = "notifications")
public class Notice {

    /**
     * notice 프라이머리 키
     **/
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private final String mId;

    /**
     * notice 의 제목
     **/
    @Nullable
    @ColumnInfo(name = "title")
    private final String mTitle;

    /**
     * notice 의 본문
     **/
    @Nullable
    @ColumnInfo(name = "description")
    private final String mDescription;

    /**
     * notice 받은 날짜
     **/
    @NonNull
    @ColumnInfo(name = "date")
    private final String mDate;

    /**
     * notice 읽었는지 여부
     **/
    @ColumnInfo(name = "read")
    private final boolean mRead;


    /**
     * (새로운 카테고리 생성)
     *
     **/
    @Ignore
    public Notice(@NonNull String title, String description, String date){
        this(UUID.randomUUID().toString(),title,description,date,false);
    }

    /**
     * 생성자
     **/
    public Notice(@NonNull String id, String title, String description,String date, boolean read){
        this.mId = id;
        this.mTitle = title;
        this.mDescription = description;
        this.mDate = date;
        this.mRead = read;
    }

    @NonNull
    public String getId() { return mId; }

    public String getTitle() { return mTitle; }

    public String getDescription() { return mDescription; }

    public String getDate() { return mDate; }

    public boolean isRead() { return mRead; }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Notice notice = (Notice) obj;
        return  Objects.equal(mId, notice.mId) &&
                Objects.equal(mTitle, notice.mTitle) &&
                Objects.equal(mDescription, notice.mDescription) &&
                Objects.equal(mDate, notice.mDate) &&
                Objects.equal(mRead,notice.mRead);
    }

    @Override
    public int hashCode() { return Objects.hashCode(mId,mTitle,mDescription,mDate,mRead); }

    @NonNull
    @Override
    public String toString() {
        return mTitle+"\n"+ mDescription+"\n" + mRead+"\n" +mDate+"\n" ;
    }

}
