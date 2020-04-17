package com.jroomstudio.smartbookmarkeditor.popup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.databinding.EditItemPopupFragBinding;

import static com.google.common.base.Preconditions.checkNotNull;

public class EditItemPopupFragment extends Fragment {

    public static final String ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID";

    // 뷰모델
    private EditItemPopupViewModel mViewModel;

    // 프래그먼트 데이터 바인딩
    private EditItemPopupFragBinding mDataBinding;

    // 프래그먼트 인스턴스 생성
    public static EditItemPopupFragment newInstance() { return new EditItemPopupFragment(); }

    // 비어있는 생성자
    public EditItemPopupFragment() {}

    // 프래그먼트 뷰모델 셋팅
    public void setViewModel(EditItemPopupViewModel viewModel){
        mViewModel = checkNotNull(viewModel);
    }

    //뷰연결
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.edit_item_popup_frag,container,false);
        if(mDataBinding == null){
            mDataBinding = EditItemPopupFragBinding.bind(root);
        }
        mDataBinding.setViewmodel(mViewModel);
        return mDataBinding.getRoot();
    }
}
