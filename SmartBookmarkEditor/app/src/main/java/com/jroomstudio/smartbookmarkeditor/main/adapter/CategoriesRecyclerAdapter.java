package com.jroomstudio.smartbookmarkeditor.main.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
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
import com.jroomstudio.smartbookmarkeditor.databinding.MainFragBinding;

import java.util.List;


public class CategoriesRecyclerAdapter
        extends RecyclerView.Adapter<CategoriesRecyclerAdapter.ItemViewHolder> {

    // 카테고리 리스트 멤버변수
    private List<Category> mCategories;

    // 카테고리 원격과 로컬 데이터 소스 액세스
    private CategoriesRepository mCategoriesRepository;

    // 메인 프래그먼트 데이터 바인딩
    private MainFragBinding mMainFragBinding;

    // 카테고리 아이템 데이터 바인딩
    private MainCategoryItemBinding mCategoryItemBinding;

    /**
     * 어댑터 생성자
     **/
    public CategoriesRecyclerAdapter(List<Category> categories,
                                     CategoriesRepository categoriesRepository,
                                     MainFragBinding mainFragBinding){
        setCategories(categories);
        mCategoriesRepository = categoriesRepository;
        mMainFragBinding = mainFragBinding;
        Log.e("adapter","생성자");
    }

    // 각 아이템의 view 추가
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e("adapter","onCreateViewHolder");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mCategoryItemBinding = MainCategoryItemBinding.inflate(inflater,parent,false);
        View view = mCategoryItemBinding.getRoot();
        return new ItemViewHolder(view);
    }

    // 포지션 입력하여 아이템 구분하여 값 지정 (text, img 등)
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Log.e("adapter","onBindViewHolder");
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

    // 각 아이템의 text  title 값 지정
    // onBind 랑 뷰홀더에서 데이터바인딩 사용하면 꼬임
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        Button btnCategory;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            btnCategory = itemView.findViewById(R.id.button_category);
        }
        @SuppressLint("ResourceAsColor")
        public void onBind(Category category){
            //Log.e("adapter","onBind");
            btnCategory.setText(category.getTitle());
            btnCategory.setSelected(category.isSelected());
        }
    }

}
