package com.jroomstudio.smartbookmarkeditor.login;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.SignInButton;
import com.jroomstudio.smartbookmarkeditor.R;

public class TestLoginActivity extends AppCompatActivity {

    SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_login_act);

    }
}
