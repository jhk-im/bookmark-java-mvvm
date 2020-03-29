package com.jroomstudio.commentstube.main;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;

public class SubViewModel extends BaseObservable {

    // 테스트용 텍스트뷰
    public ObservableField<String> tvTest = new ObservableField<>();

    private Context mContext;

    public SubViewModel(Context context) { mContext = context.getApplicationContext(); }

}
