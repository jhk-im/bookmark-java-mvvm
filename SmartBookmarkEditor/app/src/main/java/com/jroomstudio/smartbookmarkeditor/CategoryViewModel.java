package com.jroomstudio.smartbookmarkeditor;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;

import com.jroomstudio.smartbookmarkeditor.data.category.Category;
import com.jroomstudio.smartbookmarkeditor.data.category.source.CategoriesDataSource;
import com.jroomstudio.smartbookmarkeditor.data.category.source.CategoriesRepository;

/**
 * 카테고리 단일 객체를 아이템 뷰모델에서 표시하기위해 상속받아 구현하는 추상클래스
 * 아이템이 여러개로 늘어나기 때문에 추상 클래스를 상속받아 구현한다.
 **/
public abstract class CategoryViewModel extends BaseObservable
        implements CategoriesDataSource.GetCategoryCallback {

    // 카테고리 아이템의 title 관찰 변수
    public final ObservableField<String> title = new ObservableField<>();

    // 카테고리 단일객체 관찰
    private final ObservableField<Category> mCategoryObservable = new ObservableField<>();

    // 카테고리 원격, 로컬 데이터 소스 멤버변수
    private final CategoriesRepository mCategoriesRepository;

    // context 멤버변수
    private final Context mContext;

    // 카테고리 뷰모델 생성자
    public CategoryViewModel(Context context, CategoriesRepository categoriesRepository){
        mContext = context;
        mCategoriesRepository = categoriesRepository;

        // 노툴된 관찰 가능 항목은 mCategoriesObservable 관찰 가능 항목에 따라 다르다.
        mCategoryObservable.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Category category = mCategoryObservable.get();
                if(category != null){
                    title.set(category.getTitle());
                }
            }
        });
    }

    // ID 로 카테고리 단일객체 찾기
    public void start(String id){
        if(id != null){
            mCategoriesRepository.getCategory(id,this);
        }
    }

    // 객체 관찰가능 변수에 카테고리객체 추가
    public void setCategory(Category category){ mCategoryObservable.set(category); }

    // 객체 널 체크
    @Bindable
    public boolean isDataAvailable() { return mCategoryObservable.get() != null; }


    // GetCategoryCallback -> 데이터 로드 성공
    @Override
    public void onCategoryLoaded(Category category) {
        mCategoryObservable.set(category);
        notifyChange();
    }

    // GetCategoryCallback -> 데이터 없음
    @Override
    public void onDataNotAvailable() { mCategoryObservable.set(null); }

    // 객체삭제
    public void deleteCategory() {
        if(mCategoryObservable.get() != null){
            mCategoriesRepository.deleteCategory(mCategoryObservable.get().getId());
        }
    }

    // 객체 refresh
    public void onRefresh(){
        if(mCategoryObservable.get() != null){
            start(mCategoryObservable.get().getId());
        }
    }

    // 객체 id 반환
    @Nullable
    protected String getCategoryId() { return mCategoryObservable.get().getId(); }



}
