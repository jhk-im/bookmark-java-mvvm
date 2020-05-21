package com.jroomstudio.smartbookmarkeditor.notice;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.data.notice.NoticeLocalRepository;
import com.jroomstudio.smartbookmarkeditor.data.notice.NoticeLocalDatabase;
import com.jroomstudio.smartbookmarkeditor.util.AppExecutors;

public class NoticeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // dark 모드 상태 가져오기
        SharedPreferences spActStatus = getSharedPreferences("act_status", MODE_PRIVATE);
        SharedPreferences.Editor editor = spActStatus.edit();
        editor.apply();

        // 다크모드이면 다크모드로 테마변경
        if(spActStatus.getBoolean("dark_mode",true)){
            setTheme(R.style.DarkAppTheme);
        }
        setContentView(R.layout.notice_detail_act);

        // 툴바 셋팅 + 타이틀 셋팅
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setTitle(getIntent().getStringExtra("title"));
        ab.setDisplayHomeAsUpEnabled(true);

        // 내용, 날짜 셋팅
        TextView description, date;
        description = (TextView) findViewById(R.id.tv_description);
        description.setText(getIntent().getStringExtra("body"));
        date = (TextView) findViewById(R.id.tv_date);
        date.setText(getIntent().getStringExtra("date"));

        // 로컬 데이터베이스 소스 생성
        NoticeLocalDatabase database = NoticeLocalDatabase.getInstance(this);
        NoticeLocalRepository noticeLocalRepository = NoticeLocalRepository.
                getInstance(new AppExecutors(), database.notificationsDAO());

        noticeLocalRepository.updateRead(getIntent().getStringExtra("id"),true);

    }


    // 옵션메뉴 셀렉트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
