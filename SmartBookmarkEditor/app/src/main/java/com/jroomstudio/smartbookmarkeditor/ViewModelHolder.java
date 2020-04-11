package com.jroomstudio.smartbookmarkeditor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * UI 가 없는 Fragment
 * - ViewModel 을 유지하는데 사용된다.
 *
 * 프래그먼트 생성시 레이아웃의 R.id 값을 받아서 생성하게 된다.
 * 입력된 id를 저장하기 때문에 한번 생성된 프래그먼트를 id로 각각의 프래그먼트를 구분짓거나
 * 재생성 하지않고 빠르게 접근하여 ui 변경 작업을 자유롭게 진행할 수 있다.
 * findFragmentById 를 통해 접근할 수 있다.
 *
 * MVVM 패턴에서 view (fragment) 와 viewModel 을 분리하여 구현하는데
 * viewModel 은 프래그먼트 처럼 id 등을 저장하여 각각의 viewModel 을 구분짓거나 재활용하는 기능이 필요하다.
 * 이를 프래그먼트를 상속받은 ViewModelHolder 로 구현하였다.
 *
 * viewModel 은 생성시 UI가 없고 담당해야하는 프래그먼트와 중복될 수 없기 때문에
 * 임의로 String 값을 지정하여 TAG 로 저장한다.
 * 이때 생성한 TAG 값으로 각각의 viewModel 을 구분하고 재활용한다.
 * findFragmentByTag 를 통해 접근할 수 있다.
 *
 * ID 는 int
 * TAG 는 String 이기 때문에
 * int 입력시 id로
 * string 입력시 tag 로 자동 지정된다.
 **/
// 생성시 제네릭을 지정 -> VM = 프래그먼트의 viewModel
public class ViewModelHolder<VM> extends Fragment {

    // 입력된 ViewModel 인스턴스
    private VM mViewModel;

    // 다이렉트 인스턴스 생성 방지
    public ViewModelHolder(){}

    // ViewModel 을 입력받아 ViewModelHolder 를 생성하여 반환한다.
    // ViewModelHolder 는 프래그먼트이다.
    // 실제 프래그먼트 생성시 함께 생성되기 때문에 Tag 값을 잘 구분지어서 생성하게되면
    // view(Fragment) 와 viewModel(ViewModelHolder) 이 각각 id 와 tag 값으로 접근할 수 있기 때문에
    // 서로간에 동기화가 이루어 질 수 있다.
    public static <M> ViewModelHolder createViewModelHolder(@NonNull M viewModel){
        ViewModelHolder<M> viewModelHolder = new ViewModelHolder<>();
        viewModelHolder.setViewModel(viewModel);
        return viewModelHolder;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Activity 가 destroy 되고 recreate 될 때 fragment 가 destroy, recreate 되지 않는다.
        // 즉, 프래그먼트의 onCreate 가 다시 호출되지 않는다.
        setRetainInstance(true);
    }

    // 뷰모델이 이미 생성되었다면 getViewModel 을 통해 재활용
    @Nullable
    public VM getViewModel() { return mViewModel; }

    // 멤버변수에 지정하여 저장
    public void setViewModel(@NonNull VM viewModel) { mViewModel = viewModel; }
}
