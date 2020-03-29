package com.jroomstudio.commentstube.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;

import com.jroomstudio.commentstube.R;
import com.jroomstudio.commentstube.ScrollChildSwipeRefreshLayout;
import com.jroomstudio.commentstube.databinding.MainFragBinding;
import com.jroomstudio.commentstube.util.SnackBarUtils;

public class MainFragment extends Fragment {

    // 셋팅 될 프래그먼트 객체와 연결될 View Model
    private MainFragViewModel mMainFragViewModel;

    // 스낵바 관찰
    private Observable.OnPropertyChangedCallback mSnackBarCallback;


    /**
     * 해당 프래그먼트 xml 내부에 데이터 바인딩을 구현할 때
     * 자동으로 생성되는 데이터바인딩 객체
     * -> 해당 객채를 활용하면 find.viewById 없이 뷰 아이템에 접근 가능하다.
     * */
    private MainFragBinding mMainFragBinding;

    // Requires empty public constructor
    public MainFragment(){}

   /**
    * 프래그먼트 인스턴스를 생성하기위한 메소드
    * */
    public static MainFragment newInstance() { return new MainFragment(); }

    // 데이터바인딩으로 뷰 표시
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMainFragBinding = MainFragBinding.inflate(inflater,container,false);
        mMainFragBinding.setView(this);
        mMainFragBinding.setViewmodel(mMainFragViewModel);
        setHasOptionsMenu(true);
        //mMainFragBinding.tvPage.setText(String.valueOf(page));
        View root = mMainFragBinding.getRoot();
        return root;
    }

    // 액티비티가 만들어 질 때
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupSnackBar();
        setupRefreshLayout();

        //스낵바 테스트
        mMainFragViewModel.snackBarText.set("프래그먼트 셋팅");
    }

    // DESTROY
    @Override
    public void onDestroy() {
        if (mSnackBarCallback != null) {
            mMainFragViewModel.snackBarText.removeOnPropertyChangedCallback(mSnackBarCallback);
        }
        super.onDestroy();
    }

    /**
     * 메인 액티비티에서 호출한다.
     * 현재 뷰 페이저에 표시되는 프래그먼트와 뷰모델을 매치시킨다.
    * */
    public void setMainViewModel(MainFragViewModel viewModel) { mMainFragViewModel = viewModel; }

    // 스낵바 셋팅
    public void setupSnackBar() {
        mSnackBarCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                SnackBarUtils.showSnackbar(getView(), mMainFragViewModel.getSnackBarText());
            }
        };
        mMainFragViewModel.snackBarText.addOnPropertyChangedCallback(mSnackBarCallback);
    }

    //리프레쉬
    private void setupRefreshLayout() {
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout = mMainFragBinding.refreshLayout;
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.

    }

}
