package com.jroomstudio.commentstube.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jroomstudio.commentstube.databinding.MainSubFragBinding;

public class SubFragment extends Fragment {

    // 해당 프래그먼트의 뷰모델
    private SubViewModel mSubViewModel;

    // 데이터 바인딩
    private MainSubFragBinding mainSubFragBinding;

    // Requires empty public constructor
    public SubFragment(){}

    /**
     * 프래그먼트 인스턴스를 생성하기위한 메소드
     * */
    public static SubFragment newInstance() { return new SubFragment(); }

    // 데이터바인딩으로 뷰 표시
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainSubFragBinding = MainSubFragBinding.inflate(inflater,container,false);
        mainSubFragBinding.setView(this);
        mainSubFragBinding.setViewmodel(mSubViewModel);
        setHasOptionsMenu(true);

        View root = mainSubFragBinding.getRoot();

        return root;
    }

    // 액티비티가 만들어질때
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 메인 액티비티에서 호출한다.
     * 현재 뷰 페이저에 표시되는 프래그먼트와 뷰모델을 매치시킨다.
     * */
    public void setSubViewModel(SubViewModel viewModel) { mSubViewModel = viewModel; }


}
