package com.jroomstudio.smartbookmarkeditor.util;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static androidx.core.util.Preconditions.checkNotNull;

/**
 * 액티비티에 Fragment UI 추가하기
 * - 두가지 메소드가 있으며 아래의 파라미터만 다르고 나머지는 동일하다.
 * frameId - 프래그먼트 레이아웃의 R.id
 * tag - 임의로 지정한 final String 값
 *
 * frameId 를 파라미터로 가지고 있는 메소드는 실제 프래그먼트 생성시 호출
 * tag 를 파라미터로 가지고있는 메소드는 프래그먼트의 viewModel 생성시 호출
 *
 **/
public class ActivityUtils {

    // 실제 프래그먼트 생성시
    @SuppressLint("RestrictedApi") // NonNull annotation 을 사용하니 자동으로 생성되었음
    public static void addFragmentToActivity (@NonNull FragmentManager fragmentManager,
                                              @NonNull Fragment fragment, int frameId) {
        checkNotNull(fragmentManager);
        checkNotNull(fragment);
        // beginTransaction() 으로 FragmentTransaction 시작
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 해당 Fragment 를 Activity 의 View Group 에 추가
        transaction.add(frameId, fragment);
        // Fragment 관련된 작업 완료 알림
        transaction.commit();
    }

    // 프래그먼트의 뷰모델홀더 생성시
    @SuppressLint("RestrictedApi") // NonNull annotation 을 사용하니 자동으로 생성되었음
    public static void addFragmentToActivity (@NonNull FragmentManager fragmentManager,
                                              @NonNull Fragment fragment, String tag) {
        checkNotNull(fragmentManager);
        checkNotNull(fragment);
        // beginTransaction() 으로 FragmentTransaction 시작
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 해당 Fragment 를 Activity 의 View Group 에 추가
        transaction.add(fragment, tag);
        // Fragment 관련된 작업 완료 알림
        transaction.commit();
    }

}
