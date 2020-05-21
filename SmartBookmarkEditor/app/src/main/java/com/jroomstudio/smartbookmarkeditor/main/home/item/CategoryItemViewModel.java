package com.jroomstudio.smartbookmarkeditor.main.home.item;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.jroomstudio.smartbookmarkeditor.CategoryViewModel;
import com.jroomstudio.smartbookmarkeditor.data.category.Category;
import com.jroomstudio.smartbookmarkeditor.data.category.source.local.CategoriesLocalRepository;
import com.jroomstudio.smartbookmarkeditor.main.home.MainHomeFragment;

import java.lang.ref.WeakReference;

/**
 * ({@link MainHomeFragment})의 카테고리 리사이클러뷰에서 각각의 카테고리의 리스너 역할
 **/
public class CategoryItemViewModel extends CategoryViewModel {

    /**
     * 약한 참조 (Weak Reference)
     * - 가비지컬렉터가 발생하면 무조건 수거된다.
     * - 가비지컬렉터의 실행주기와 일치하며 이를 이용하여 짧은 주기에 자주 사용되는 객체를 캐시할 때 유용하다.
     **/
    @Nullable
    private WeakReference<CategoryItemNavigator> mNavigator;

    public CategoryItemViewModel(Context context,
                                 CategoriesLocalRepository categoriesRepository) {
        super(context, categoriesRepository);
    }

    public void setNavigator(CategoryItemNavigator navigator) {
        mNavigator = new WeakReference<>(navigator);
    }


    /**
     * 아이템이 선택되면 데이터바인딩 라이브러리에서 호출한다.
     **/
    public void categoryClicked(){
        Category category = getCategory();
        if(category == null){
            return;
        }
        // 이미 선택된 카테고리는 클릭이벤트를 진행하지 않는다.
        if(category.isSelected()){
            return;
        }

        // 메인액티비티를 통해 메인 프래그먼트 뷰모델에서 다시 로드하도록 알림
        if(mNavigator != null && mNavigator.get() != null){
            mNavigator.get().selectedCategory(category);
        }
    }



}

