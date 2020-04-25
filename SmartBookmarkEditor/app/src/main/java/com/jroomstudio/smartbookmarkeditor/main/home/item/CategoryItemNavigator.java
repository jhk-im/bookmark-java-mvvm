package com.jroomstudio.smartbookmarkeditor.main.home.item;

import com.jroomstudio.smartbookmarkeditor.data.category.Category;

/**
 * 메인 액티비티 리사이클러뷰의 카테고리목록에서 카테고리를 가져올 수 있다.
 **/
public interface CategoryItemNavigator {
    void selectedCategory(Category category);
}
