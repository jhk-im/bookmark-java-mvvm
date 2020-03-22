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
    @Nullable
    @ColumnInfo(name = "number")
    private final String mNumber;

    /**
     * - Tab 아이템에 표시 될 tab 의 이름
     * - null 값을 허용함
     * - Column 명은 name
     * - 변수타입 String
     * */
    @Nullable
    @ColumnInfo(name = "name")
    private final String mName;

    /**
     * - Tab 이 사용되기 위해 선택 되었는지 여부
     * - Column 명은 used
     * - 변수타입 boolean
     * */
    @ColumnInfo(name = "used")
    private final boolean mUsed;


    /**
     * Tab 객체가 처음 생성될 때 사용하는 생성자
     * @param number tab 의 리스트 순번
     * @param name tab 이름
     * Ignore - 포함하고 싶지 않은 필드 앞에 붙인다?
     * */
    @Ignore
    public Tab(@Nullable String number,
               @Nullable String name) {
        this(number,name, UUID.randomUUID().toString(),false);
    }


    /**
     * Tab 리스트에 이미 Id가 있는 경우 값을 가져와 셋팅
     * @param id 데이터베이스 내부에서 tab 아이템 구분
     * @param number tab 리스트에서의 순번
     * @param name tab 이름
     * @param used tab 이 사용되는지 안사용되는지 여부
     * */
    public Tab(@NonNull String id,
               @Nullable String number,
               @Nullable String name,
               boolean used) {
        this.mId = id;
        this.mNumber = number;
        this.mName = name;
        this.mUsed = used;
    }

    @NonNull
    public String getId() { return mId; }

    public String getNumber() { return mNumber; }

    public String getName() { return mName; }

    public boolean isUsed() { return mUsed; }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Tab tab = (Tab) obj;
        return  Objects.equal(mId,tab.mId) &&
                Objects.equal(mNumber,tab.mNumber)&&
                Objects.equal(mName,tab.mName) &&
                Objects.equal(mUsed,tab.mUsed);
    }

    @Override
    public int hashCode() { return Objects.hashCode(mId,mNumber,mName,mUsed); }

    @Override
    public String toString() { return "Tab whit name" + mName; }
}
