package com.jroomstudio.smartbookmarkeditor.main;

import android.content.Context;

import com.jroomstudio.smartbookmarkeditor.CategoryViewModel;
import com.jroomstudio.smartbookmarkeditor.data.category.source.CategoriesRepository;

public class CategoryItemViewModel extends CategoryViewModel {

    public CategoryItemViewModel(Context context, CategoriesRepository categoriesRepository) {
        super(context, categoriesRepository);
    }

}

