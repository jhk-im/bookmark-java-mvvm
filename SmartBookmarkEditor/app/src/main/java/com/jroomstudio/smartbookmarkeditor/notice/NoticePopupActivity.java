package com.jroomstudio.smartbookmarkeditor.notice;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.data.notice.NoticeLocalDataSource;
import com.jroomstudio.smartbookmarkeditor.data.notice.NoticeLocalDatabase;
import com.jroomstudio.smartbookmarkeditor.util.AppExecutors;

public class NoticePopupActivity extends AppCompatActivity {

    // 단일 삭제, 전체삭제 구분
    public static final String ALL_DELETE = "ALL_DELETE";
    public static final String ITEM_DELETE = "ITEM_DELETE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // dark 모드 상태 가져오기
        SharedPreferences spActStatus = getSharedPreferences("act_status", MODE_PRIVATE);
        SharedPreferences.Editor editor = spActStatus.edit();
        editor.apply();
        // 다크모드이면 다크모드로 테마변경
        if(spActStatus.getBoolean("dark_mode",true)){
            setTheme(R.style.DarkAppPopup);
        }
        // 상태바 제거하고 전체화면 모드로
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.notice_popup_act);

        // 로컬 데이터베이스 소스 생성
        NoticeLocalDatabase database = NoticeLocalDatabase.getInstance(this);
        NoticeLocalDataSource noticeLocalDataSource = NoticeLocalDataSource.
                getInstance(new AppExecutors(), database.notificationsDAO());

        TextView deleteQuestion;
        // 안내문구 셋팅
        deleteQuestion = (TextView) findViewById(R.id.tv_delete_questions);
        if(getIntent().getStringExtra("delete_type").equals(ALL_DELETE)) {
            deleteQuestion.setText(R.string.edit_all_delete_questions);
        }else{
            deleteQuestion.setText(R.string.edit_delete_questions);
        }

        Button btnOk,btnCancel;
        // 확인버튼 클릭 시
        btnOk = (Button)findViewById(R.id.btn_delete_ok);
        btnOk.setOnClickListener(v -> {
            if(getIntent().getStringExtra("delete_type").equals(ALL_DELETE)){
                // 전체삭제
                noticeLocalDataSource.deleteAllNotifications();
                finish();
            }else{
                // 단일객체 삭제
                noticeLocalDataSource.deleteNotice(getIntent().getStringExtra("delete_id"));
                finish();
            }
        });

        // 취소버튼
        btnCancel = (Button) findViewById(R.id.btn_delete_cancel);
        btnCancel.setOnClickListener(v -> onBackPressed());

    }
}
