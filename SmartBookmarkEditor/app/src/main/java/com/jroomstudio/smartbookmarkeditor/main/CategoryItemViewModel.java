package com.jroomstudio.smartbookmarkeditor.main;

import android.content.Context;

import com.jroomstudio.smartbookmarkeditor.CategoryViewModel;
import com.jroomstudio.smartbookmarkeditor.data.category.source.CategoriesRepository;

public class CategoryItemViewModel extends CategoryViewModel {

    public CategoryItemViewModel(Context context, CategoriesRepository categoriesRepository) {
        super(context, categoriesRepository);
    }

    /**
     * 아이템 클릭하면 데이터 바인딩 라이브러리 호출
     **/
    public void categoryClicked(){
        String id = getCategoryId();
        if(id == null){
            // 카테고리 정보가 로드되기전에 클릭발생
            return;
        }

    }

}

