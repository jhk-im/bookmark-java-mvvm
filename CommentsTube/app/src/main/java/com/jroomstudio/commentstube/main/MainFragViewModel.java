package com.jroomstudio.commentstube.main;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

public class MainFragViewModel extends BaseObservable {

    // Observable 필드는 자동으로 뷰를 업데이트 한다.
    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    // 테스트용 텍스트뷰
    public final ObservableField<String> tvTest = new ObservableField<>();

    //스낵바
    final ObservableField<String> snackBarText = new ObservableField<>();

    private Context mContext;

    public MainFragViewModel(Context context) { mContext = context.getApplicationContext(); }

    public String getSnackBarText() {
        return snackBarText.get();
    }



}
