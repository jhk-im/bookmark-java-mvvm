package com.jroomstudio.smartbookmarkeditor.login;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.jroomstudio.smartbookmarkeditor.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 개인정보 처리방침, 오픈소스라이센스 보여주기
 **/
public class InformationActivity extends AppCompatActivity {



    // 뒤로가기 버튼
    private ImageView btnBack;
    private TextView tvPIPP,tvInfoTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_act);

        // 상태바 숨기기
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();


        //뒤로가기 버튼
        btnBack = (ImageView) findViewById(R.id.btn_info_back);
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        tvPIPP = (TextView) findViewById(R.id.tv_ppip);
        // 텍스트뷰 스크롤
        tvPIPP.setMovementMethod(new ScrollingMovementMethod());

        tvInfoTitle = (TextView) findViewById(R.id.tv_info_title);
        if(getIntent().getStringExtra("TYPE").equals("OSL")){
            tvInfoTitle.setText(R.string.open_source_license);
            // 오픈소스라이선스 txt 파일 에서 text 가져옴
            tvPIPP.setText(readTxtfile(getApplicationContext(),R.raw.osl));
        }
        if(getIntent().getStringExtra("TYPE").equals("PIPP")){
            tvInfoTitle.setText(R.string.pipp);
            // 개인정보처리방침 txt 파일 에서 text 가져옴
            tvPIPP.setText(readTxtfile(getApplicationContext(),R.raw.ppip));
        }

    }

    /**
     * @param resId res\raw\파일
     * @return txt내용
     */
    public String readTxtfile(Context context, int resId) {
        String result = "";
        InputStream txtResource = context.getResources().openRawResource(resId);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i;
        try {
            i = txtResource.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = txtResource.read();
            }
            result = new String(byteArrayOutputStream.toByteArray(), "UTF-8");
            txtResource.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.trim();
    }
}
