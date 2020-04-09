package com.jroomstudio.smartbookmarkeditor.data.category.source.remote;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.common.collect.Lists;
import com.jroomstudio.smartbookmarkeditor.data.category.Category;
import com.jroomstudio.smartbookmarkeditor.data.category.source.CategoriesDataSource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 네트워크 지연시간 시뮬레이션을 테스트하는 데이터소스 구현
 **/
public class CategoriesRemoteDataSource implements CategoriesDataSource {

    // INSTANCE
    private static CategoriesRemoteDataSource INSTANCE;

    /**
     * - 네트워크 지연시간 시뮬레이션에 사용될 Mills int 변수
     * - 클래스에 존재하는 단하나의 상수 static final int
     **/
    private static final int SERVICE_LATENCY_IN_MILLIS = 2000;

    /**
     * 회원 / 비회원 구분
     * true 경우에만 원격 데이터베이스에 데이터를 요청한다.
     **/
    boolean mLoginUser = false;

    /**
     * - test 로 생성된 Tab 객체를 캐시 메모리에 저장
     * key - String name
     * value - Category
     **/
    private static final Map<String, Category> CATEGORY_SERVICE_DATA;

    // Test 용 데이터
    private static void addCategory(String title, int position){
        Category category = new Category(title,position);
        CATEGORY_SERVICE_DATA.put(category.getId(), category);
    }
    static {
        CATEGORY_SERVICE_DATA = new LinkedHashMap<>();
        addCategory("Bookmark",0);
        addCategory("Community",1);
        addCategory("Sports",2);
    }

    // 다이렉트 인스턴스 방지
    private CategoriesRemoteDataSource(){}

    /**
     * - CategoriesRemoteDataSource 인스턴스 생성 메소드
     * - INSTANCE null 체크
     * - null 이면 new 키워드로 private 생성자로 인스턴스를 생성한다.
     * - 생성된 인스턴스를 반환한다.
     **/
    public static CategoriesRemoteDataSource getInstance() {
        if(INSTANCE == null){
            INSTANCE = new CategoriesRemoteDataSource();
        }
        return INSTANCE;
    }

    /*
     * CategoriesDataSource 오버라이드 메소드 구현
     */

    @Override
    public void refreshCategories() {
        // {@link CategoriesRepository} 에서 처리하므로 이곳에서는 필요하지 않다.
    }

    // category 리스트를 가져와 callback
    @Override
    public void getCategories(@NonNull LoadCategoriesCallback callback) {
        // 실행을 지연시켜 네트워크를 시뮬레이션 한다.
        // Handler -> android.os
        Handler handler = new Handler();
        // Handler.postDelayed(Runnable, delayMillis)
        handler.postDelayed(() -> {
            callback.onCategoriesLoaded(Lists.newArrayList(CATEGORY_SERVICE_DATA.values()));
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    // category 객체를 찾아 callback
    @Override
    public void getCategory(@NonNull String id, @NonNull GetCategoryCallback callback) {
        final Category category = CATEGORY_SERVICE_DATA.get(id);
        // 실행을 지연시켜 네트워크를 시뮬레이션 한다.
        // Handler -> android.os
        Handler handler = new Handler();
        // Handler.postDelayed(Runnable, delayMillis)
        handler.postDelayed(() -> {
            callback.onCategoryLoaded(category);
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    // category 저장
    @Override
    public void saveCategory(@NonNull Category category) {
        CATEGORY_SERVICE_DATA.put(category.getId(), category);
    }

    // 모든 카테고리 삭제
    @Override
    public void deleteAllCategories() {
        CATEGORY_SERVICE_DATA.clear();
    }

    // 입력된 id인 category 삭제
    @Override
    public void deleteCategory(@NonNull String id) {
        CATEGORY_SERVICE_DATA.remove(id);
    }

    // position 값 업데이트
    @Override
    public void updatePosition(@NonNull Category category, int position) {
        Category updateCategory = new Category(category.getId(),category.getTitle(),position);
        CATEGORY_SERVICE_DATA.put(category.getId(), updateCategory);
    }

    @Override
    public void updatePosition(@NonNull String id, int position) {
        // {@link CategoriesRepository} 에서 처리하므로 이곳에서는 필요하지 않다.
    }
}
