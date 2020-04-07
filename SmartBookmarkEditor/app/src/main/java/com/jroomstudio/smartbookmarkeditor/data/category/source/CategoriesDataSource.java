package com.jroomstudio.smartbookmarkeditor.data.category.source;

import androidx.annotation.NonNull;

import com.jroomstudio.smartbookmarkeditor.data.category.Category;

import java.util.List;

/**
 * Category 데이터에 액세스하기 위한 진입점
**/
public interface CategoriesDataSource {

    /**
     * categories 데이터 베이스에서 category 리스트 전체를 가져올 때 callback 한다.
     * - getCategories() 호출할 때 2가지 메소드를 오버라이딩 하게된다.
     * onCategoriesLoaded(List<Category> categories);
     * - 데이터베이스에서 리스트 로드에 성공하면 해당 메소드에에 리스트를 입력하여 callback
     * onDataNotAvailable() -> 특정 이유로 로드에 실패했을 경우 callback
     **/
    interface LoadCategoriesCallback {
        void onCategoriesLoaded(List<Category> categories);
        void onDataNotAvailable();
    }

    /**
     * 데이터베이스에서 Category 객체를 get 할 때 callback 한다.
     * onCategoryLoaded(Category category);
     * - 로드에 성공할 경우 꺼내온 객체를 입력하여 callback
     * onDataNotAvailable() -> 특정 이유로 로드에 실패했을 경우 callback
     **/
    interface GetCategoryCallback {
        void onCategoryLoaded(Category category);
        void onDataNotAvailable();
    }

    // Categories 를 refresh
    void refreshCategories();

    // Categories 에서 리스트 가져오기
    void getCategories(@NonNull LoadCategoriesCallback callback);

    // categories 에서 입력된 id로 category 객체 가져오기
    void getCategory(@NonNull String id,@NonNull GetCategoryCallback callback);

    // Category 객체를 데이터베이스에 저장
    void saveCategory(@NonNull Category category);

    // 모든 카테고리 제거
    void deleteAllCategories();

    // 입력된 id 의 category 찾아서 제거
    void deleteCategory(@NonNull String id);

    // 입력된 카테고리 객체의 포지션값 변경
    void updatePosition(@NonNull Category category, int position);

    // 입력된 카테고리 id인 객체의 포지션값 변경
    void updatePosition(@NonNull String id, int position);


}
