package com.jroomstudio.commentstube.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


import com.google.common.base.Objects;

import java.util.UUID;


/**
 * Tab 불변 모델 클래스
 * - 메인에 표시될 Tab 의 상태를 Room 데이터베이스에 저장하고 관리할 모델 클래스
 * */

// Room 데이터 베이스에 tab 객체의 정보를 저장할 테이블 네임
@Entity(tableName = "tabs")
public final class Tab {

    /**
     * - tabs 테이블에 TAB 이 저장 될 때 구분지을 프라이머리 키
     * - null 값을 허용하지 않음
     * - Column 명은 entryId
     * - 변수타입 String
    * */
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "entryId")
    private final String mId;

    /**
     * - Tab 리스트에 해당 Tab 의 순번
     * - null 값을 허용함
     * - Column 명은 number
     * - 변수타입 String
     * */
    /*    @NonNull
    @ColumnInfo(name = "number")
    private final String mNumber;*/

    /**
     * - Tab 아이템에 표시 될 tab 의 이름
     * - null 값을 허용함
     * - Column 명은 name
     * - 변수타입 String
     * */
    @NonNull
    @ColumnInfo(name = "name")
    private final String mName;

    /**
     * - Tab 이 표현하는 view 프래그먼트가 어떤 형태인지 구분
     * -
     * */
    @NonNull
    @ColumnInfo(name = "viewType")
    private final String mViewType;

    /**
     * - Tab 이 사용되기 위해 선택 되었는지 여부
     * - Column 명은 used
     * - 변수타입 boolean
     * */
    @ColumnInfo(name = "used")
    private final boolean mUsed;


    /**
     * Tab 객체가 처음 생성될 때 사용하는 생성자
     * @param name tab 이름
     * @param viewType 현재 탭의 프래그먼트 타입
     * Ignore - 포함하고 싶지 않은 필드 앞에 붙인다?
     * */
    @Ignore
    public Tab(@NonNull String name,
               @NonNull String viewType) {
        this(UUID.randomUUID().toString(),name,viewType,true);
    }


    /**
     * Tab 리스트에 이미 Id가 있는 경우 값을 가져와 셋팅
     * @param id 데이터베이스 내부에서 tab 아이템 구분
     * @param name tab 이름
     * @param used tab 이 사용되는지 안사용되는지 여부
     * @param viewType 현재 탭의 프래그먼트 타입
     * */
    public Tab(@NonNull String id,
               @NonNull String name,
               @NonNull String viewType,
               boolean used) {
        this.mId = id;
        this.mName = name;
        this.mUsed = used;
        this.mViewType = viewType;
    }

    @NonNull
    public String getId() { return mId; }

    @NonNull
    public String getName() { return mName; }

    @NonNull
    public String getViewType() { return mViewType; }

    public boolean isUsed() { return mUsed; }


    @Override
    public boolean equals(@Nullable Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Tab tab = (Tab) obj;
        return  Objects.equal(mId,tab.mId) &&
                Objects.equal(mName,tab.mName) &&
                Objects.equal(mViewType,tab.mViewType) &&
                Objects.equal(mUsed,tab.mUsed);
    }

    @Override
    public int hashCode() { return Objects.hashCode(mId,mName,mViewType,mUsed); }

    @Override
    public String toString() { return "Tab whit name" + mName; }

}
