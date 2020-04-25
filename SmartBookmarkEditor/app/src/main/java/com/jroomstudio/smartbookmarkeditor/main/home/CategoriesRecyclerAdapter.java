package com.jroomstudio.smartbookmarkeditor.main.home;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.data.category.Category;
import com.jroomstudio.smartbookmarkeditor.data.category.source.CategoriesRepository;
import com.jroomstudio.smartbookmarkeditor.databinding.MainCategoryItemBinding;
import com.jroomstudio.smartbookmarkeditor.main.MainActivity;
import com.jroomstudio.smartbookmarkeditor.main.home.item.CategoryItemNavigator;
import com.jroomstudio.smartbookmarkeditor.main.home.item.CategoryItemViewModel;

import java.util.List;

import javax.annotation.Nullable;


public class CategoriesRecyclerAdapter
        extends RecyclerView.Adapter<CategoriesRecyclerAdapter.ItemViewHolder>{

    // 카테고리 리스트 멤버변수
    private List<Category> mCategories;

    // 카테고리 원격과 로컬 데이터 소스 액세스
    private CategoriesRepository mCategoriesRepository;

    // 메인프래그먼트 뷰모델
    private MainHomeViewModel mMainHomeViewModel;

    // 카테고리 아이템 데이터 바인딩
    private MainCategoryItemBinding mCategoryItemBinding;

    // 메인 액티비티 네비게이터
    @Nullable private CategoryItemNavigator mItemNavigator;

    /**
     * 어댑터 생성자
     **/
    public CategoriesRecyclerAdapter(List<Category> categories,
                                     CategoriesRepository categoriesRepository,
                                     MainHomeViewModel mainHomeViewModel, MainActivity itemNavigator){
        setCategories(categories);
        mCategoriesRepository = categoriesRepository;
        mMainHomeViewModel = mainHomeViewModel;
        mItemNavigator = itemNavigator;
    }

    // 각 아이템의 view 추가
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mCategoryItemBinding = MainCategoryItemBinding.inflate(inflater,parent,false);
        View view = mCategoryItemBinding.getRoot();
        return new ItemViewHolder(view,parent);
    }

    // 포지션 입력하여 아이템 구분하여 값 지정 (text, img 등)
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
         holder.onBind(mCategories.get(position));
    }

    // 멤버변수 리스트 사이즈만큼 반복실행
    @Override
    public int getItemCount() { return mCategories.size(); }

    // 카테고리 멤버리스트 갱신
    private void setCategories(List<Category> categories){
        mCategories = categories;
        notifyDataSetChanged();
    }

    // 옵저버블에서 변화감지 후 리스트 갱신
    public void replaceCategories(List<Category> categories){
        setCategories(categories);
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        Button btnCategory;
        // 카테고리 아이템뷰의 뷰모델
        CategoryItemViewModel mCategoryItemViewModel;
        public ItemViewHolder(@NonNull View itemView ,ViewGroup viewGroup) {
            super(itemView);
            btnCategory = itemView.findViewById(R.id.button_category);

            // 카테고리 아이템 뷰모델 생성
            mCategoryItemViewModel = new CategoryItemViewModel(
                    viewGroup.getContext().getApplicationContext(),
                    mCategoriesRepository
            );
            // 뷰모델에 네비게이터 셋팅
            mCategoryItemViewModel.setNavigator(mItemNavigator);
            // 뷰모델과 뷰를 연결
            mCategoryItemBinding.setViewmodel(mCategoryItemViewModel);
        }
        @SuppressLint("ResourceAsColor")
        public void onBind(Category category){
            // 뷰모델에 관찰할 카테고리 아이템 셋팅
            mCategoryItemViewModel.setCategory(category);

            // 버튼 selected 설정
            btnCategory.setSelected(category.isSelected());

            // 롱클릭 이벤트 -> 롱클릭으로 선택된 카테고리 편집 팝업 띄우기
            btnCategory.setOnLongClickListener(v -> {
                mCategoryItemViewModel.categoryClicked();
                mMainHomeViewModel.editLongClickCategory(category);
                return false;
            });

        }

    }

}
