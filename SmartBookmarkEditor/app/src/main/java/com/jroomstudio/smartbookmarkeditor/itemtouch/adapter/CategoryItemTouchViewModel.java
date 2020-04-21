package com.jroomstudio.smartbookmarkeditor.itemtouch.adapter;

import android.content.Context;

import androidx.annotation.Nullable;

import com.jroomstudio.smartbookmarkeditor.CategoryViewModel;
import com.jroomstudio.smartbookmarkeditor.data.category.source.CategoriesRepository;
import com.jroomstudio.smartbookmarkeditor.main.CategoryItemNavigator;
import com.jroomstudio.smartbookmarkeditor.main.MainFragment;

import java.lang.ref.WeakReference;

/**
 * ({@link MainFragment})의 카테고리 리사이클러뷰에서 각각의 카테고리의 리스너 역할
 **/
public class CategoryItemTouchViewModel extends CategoryViewModel {

    /**
     * 약한 참조 (Weak Reference)
     * - 가비지컬렉터가 발생하면 무조건 수거된다.
     * - 가비지컬렉터의 실행주기와 일치하며 이를 이용하여 짧은 주기에 자주 사용되는 객체를 캐시할 때 유용하다.
     **/
    @Nullable
    private WeakReference<CategoryItemNavigator> mNavigator;

    public CategoryItemTouchViewModel(Context context, CategoriesRepository categoriesRepository) {
        super(context, categoriesRepository);
    }

    public void setNavigator(CategoryItemNavigator navigator) {
        mNavigator = new WeakReference<>(navigator);
    }

}

