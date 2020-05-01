package com.jroomstudio.smartbookmarkeditor.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.jroomstudio.smartbookmarkeditor.R;

public class LoginActivity extends AppCompatActivity {

    /**
     * 버튼
     **/
    // x 버튼
    private ImageView btnClose;
    // 각각의 로그인버튼
    private ConstraintLayout btnGoogle, btnFacebook, btnNaver;
    // 개인정보처리방침, 이용약관 버튼
    private TextView btnPIPP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_act);
        // 액티비티 상태 가져오기
        SharedPreferences spActStatus = getSharedPreferences("act_status", MODE_PRIVATE);
        SharedPreferences.Editor editor = spActStatus.edit();
        editor.apply();
        //spActStatus.getBoolean("guest_user",true);

        // 상태바 숨기기
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        // 버튼셋팅
        btnClose = (ImageView) findViewById(R.id.btn_login_close);
        btnGoogle = (ConstraintLayout) findViewById(R.id.btn_google_login);
        btnFacebook = (ConstraintLayout) findViewById(R.id.btn_facebook_login);
        btnNaver = (ConstraintLayout) findViewById(R.id.btn_naver_login);
        // 밑줄있는 개인정보 처리방침 버튼
        btnPIPP = (TextView) findViewById(R.id.btn_pipp);
        btnPIPP.setPaintFlags(btnPIPP.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        // 모든 버튼 리스너 셋팅
        setupButtonListener();



    }

    // 각각의 버튼리스너 셋팅
    private void setupButtonListener() {
        // 닫기 버튼
        btnClose.setOnClickListener(v -> {
            finish();
        });
        // 구글로그인
        btnGoogle.setOnClickListener(v -> {
            Toast.makeText(this, "google login", Toast.LENGTH_SHORT).show();
        });
        // 페이스북로그인
        btnFacebook.setOnClickListener(v -> {
            Toast.makeText(this, "facebook login", Toast.LENGTH_SHORT).show();
        });
        // 네이버로그인
        btnNaver.setOnClickListener(v -> {
            Toast.makeText(this, "naver login", Toast.LENGTH_SHORT).show();
        });
        // 개인정보처리방침
        btnPIPP.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this,InformationActivity.class);
            intent.putExtra("TYPE","PIPP");
            startActivity(intent);
        });

    }
}
