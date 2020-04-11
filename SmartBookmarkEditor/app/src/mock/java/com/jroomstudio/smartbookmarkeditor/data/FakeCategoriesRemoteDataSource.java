package com.jroomstudio.smartbookmarkeditor.data;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.google.common.collect.Lists;
import com.jroomstudio.smartbookmarkeditor.data.category.Category;
import com.jroomstudio.smartbookmarkeditor.data.category.source.CategoriesDataSource;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 쉬운 테스트를 위해 데이터에 정적으로 액세스 할 수 있는 원격 데이터 소스 구현
 **/
public class FakeCategoriesRemoteDataSource implements CategoriesDataSource {

    private static FakeCategoriesRemoteDataSource INSTANCE;

    /**
     * Category 객체를 캐시 메모리에 저장
     * key - String name
     * value - category
     **/
    private static final Map<String, Category> CATEGORY_SERVICE_DATA = new LinkedHashMap<>();

    // 다이렉트 인스턴스 방지
    private FakeCategoriesRemoteDataSource() {}

    /**
     * 싱글 인스턴스 생성 메소드
     * - INSTANCE null 체크
     * - null 이면 new 키워드로 private 생성자로 인스턴스를 생성한다.
     * - 생성된 인스턴스를 반환한다.
     **/
    public static FakeCategoriesRemoteDataSource getInstance() {
        if(INSTANCE == null){
            INSTANCE = new FakeCategoriesRemoteDataSource();
        }
        return INSTANCE;
    }

    /**
     * TEST 용 Tab 객체를 생성하고 Map<String(id), Category> 에 추가
     **/
    @VisibleForTesting
    public void addCategories(Category... categories){
        if(categories != null){
            for(Category category : categories){
                CATEGORY_SERVICE_DATA.put(category.getId(), category);
            }
        }
    }


    /*
     * CategoriesDataSource 오버라이드 메소드 구현
     */

    @Override
    public void refreshCategories() {
        // {@link CategoriesRepository} 에서 처리하므로 이곳에서는 필요하지 않다.
    }

    // 리스트 콜백
    @Override
    public void getCategories(@NonNull LoadCategoriesCallback callback) {
        callback.onCategoriesLoaded(Lists.newArrayList(CATEGORY_SERVICE_DATA.values()));
    }

    // 객체 콜백
    @Override
    public void getCategory(@NonNull String id, @NonNull GetCategoryCallback callback) {
        Category category = CATEGORY_SERVICE_DATA.get(id);
        callback.onCategoryLoaded(category);
    }

    // 객체저장
    @Override
    public void saveCategory(@NonNull Category category) {
       CATEGORY_SERVICE_DATA.put(category.getId(),category);
    }

    // 리스트삭제
    @Override
    public void deleteAllCategories() {
        CATEGORY_SERVICE_DATA.clear();
    }

    // id로 객체삭제
    @Override
    public void deleteCategory(@NonNull String id) {
        CATEGORY_SERVICE_DATA.remove(id);
    }

    // 입력받은 객체 포지션값 업데이트
    @Override
    public void updatePosition(@NonNull Category category, int position) {
        Category updateCategory =
                new Category(category.getId(),category.getTitle(),position,category.isSelected());
        CATEGORY_SERVICE_DATA.put(category.getId(),updateCategory);
    }

    @Override
    public void updatePosition(@NonNull String id, int position) {
        // {@link CategoriesRepository} 에서 처리하므로 이곳에서는 필요하지 않다.
    }

    @Override
    public void selectedCategory(@NonNull Category category, boolean selected) {
        Category updateCategory =
                new Category(category.getId(),category.getTitle(),category.getPosition(),selected);
        CATEGORY_SERVICE_DATA.put(category.getId(),updateCategory);
    }

    @Override
    public void selectedCategory(@NonNull String id, boolean selected) {
        // {@link CategoriesRepository} 에서 처리하므로 이곳에서는 필요하지 않다.
    }

}
