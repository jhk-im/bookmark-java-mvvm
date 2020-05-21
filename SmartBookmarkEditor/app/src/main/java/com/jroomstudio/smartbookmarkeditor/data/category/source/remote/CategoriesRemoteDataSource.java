package com.jroomstudio.smartbookmarkeditor.data.category.source.remote;

import androidx.annotation.NonNull;

import com.jroomstudio.smartbookmarkeditor.data.category.Category;
import com.jroomstudio.smartbookmarkeditor.data.member.JsonWebToken;

import java.util.List;

public interface CategoriesRemoteDataSource {

    interface RefreshTokenCallback{
        void onRefreshTokenCallback(JsonWebToken token);
        void refhreshTokenFailed();
    }

    interface LoadCategoriesCallback{
        void onCategoriesLoaded(List<Category> categories);
        void onDataNotAvailable();
    }

    interface GetCategoryCallback {
        void onCategoryLoaded(Category category);
        void onDataNotAvailable();
    }
    interface UpdateCallback{
        void onCompletedUpdate();
        void onFailedUpdate();
    }

    // 토큰만료시 재발급
    void refreshToken(@NonNull RefreshTokenCallback callback);

    // 카테고리 리스트를 데이터베이스에서 가져온다.
    void getCategories(@NonNull Category category, @NonNull LoadCategoriesCallback callback);

    // 카테고리 저장
    void saveCategory(@NonNull Category category,
                      @NonNull UpdateCallback callback);

    // 카테고리 삭제
    void deleteCategory(@NonNull String id,
                        @NonNull UpdateCallback callback);

    // 카테고리 변경
    void updateCategory(@NonNull Category category,
                        @NonNull GetCategoryCallback callback);

    // 카테고리 리스트 업데이트
    void updateCategories(@NonNull List<Category> categories,
                          @NonNull UpdateCallback callback);



}
