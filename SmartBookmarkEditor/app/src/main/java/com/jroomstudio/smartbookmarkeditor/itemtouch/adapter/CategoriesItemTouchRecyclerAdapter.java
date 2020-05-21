package com.jroomstudio.smartbookmarkeditor.itemtouch.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.data.category.Category;
import com.jroomstudio.smartbookmarkeditor.data.category.source.local.CategoriesLocalRepository;
import com.jroomstudio.smartbookmarkeditor.itemtouch.ItemTouchEditViewModel;
import com.jroomstudio.smartbookmarkeditor.util.ItemTouchHelperListener;

import java.util.List;


public class CategoriesItemTouchRecyclerAdapter
        extends RecyclerView.Adapter<CategoriesItemTouchRecyclerAdapter.ItemViewHolder>
        implements ItemTouchHelperListener {

    // 카테고리 리스트 멤버변수
    private List<Category> mCategories;

    // 카테고리 원격과 로컬 데이터 소스 액세스
    private CategoriesLocalRepository mCategoriesRepository;

    // 메인프래그먼트 뷰모델
    private ItemTouchEditViewModel mViewModel;


    // item move 상태 구분
    private boolean isItemMove = false;
    /**
     * 어댑터 생성자
     **/
    public CategoriesItemTouchRecyclerAdapter(List<Category> categories,
                                              CategoriesLocalRepository categoriesRepository,
                                              ItemTouchEditViewModel viewModel){
        setCategories(categories);
        mCategoriesRepository = categoriesRepository;
        mViewModel = viewModel;
    }

    // 각 아이템의 view 추가
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e("test","category on create holder");
        Context context = parent.getContext();
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_touch_category_item, parent, false);
        return new ItemViewHolder(view);
    }

    // 포지션 입력하여 아이템 구분하여 값 지정 (text, img 등)
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
         holder.onBind(mCategories.get(position));
    }

    // 멤버변수 리스트 사이즈만큼 반복실행
    @Override
    public int getItemCount() { return mCategories != null ? mCategories.size() : 0; }

    // 카테고리 멤버리스트 갱신
    private void setCategories(List<Category> categories){
        mCategories = categories;
        notifyDataSetChanged();
    }

    // 옵저버블에서 변화감지 후 리스트 갱신
    public void replaceCategories(List<Category> categories){
        setCategories(categories);
    }

    // 움직이고 있는지 아닌지 반환
    public boolean isMove() {
        return isItemMove;
    }

    // 아이템 무브
    @Override
    public boolean onItemMove(int from_position, int to_position) {
        // 움직이고 있을 때에는 view model 에서 갱신하지 못하도록함
        isItemMove = true;
        // 이동할 객체 가져와 생성
        Category category = mCategories.get(from_position);
        // 이동할 객체 삭제
        mCategories.remove(from_position);
        // 새로생성한 객체 저장
        mCategories.add(to_position,category);
        // 알림
        notifyItemMoved(from_position, to_position);
        return true;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        Button btnCategory;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            btnCategory = itemView.findViewById(R.id.button_category);
        }

        public void onBind(Category category){
            // 버튼 selected 설정
            btnCategory.setSelected(category.isSelected());
            // 선택되지 않은 카테고리는 클릭 못함
            btnCategory.setClickable(category.isSelected());
            btnCategory.setText(category.getTitle());
        }

    }

}
