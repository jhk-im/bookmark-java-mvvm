package com.jroomstudio.smartbookmarkeditor.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.information.InformationActivity;
import com.jroomstudio.smartbookmarkeditor.main.MainActivity;
import com.jroomstudio.smartbookmarkeditor.util.FacebookLoginCallback;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements LoginNavigator {

    /**
     * 버튼
     **/
    // x 버튼
    private ImageView btnClose;
    // 각각의 로그인버튼
    private ConstraintLayout btnGoogle,btnGusetUser,btnFacebook;
    // 개인정보처리방침, 이용약관 버튼
    private TextView btnPIPP;

    /**
     * 구글 로그인
     **/
    int GOOGLE_SIGN_IN = 1;
    GoogleSignInClient mGoogleSignInClient;

    // 액티비티 상태
    private  SharedPreferences spActStatus;

    /**
     * 페이스북 로그인
     **/
    private FacebookLoginCallback mFacebookLoginCallback;
    private CallbackManager mFacebookCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_act);
        // 액티비티 상태 가져오기
        spActStatus = getSharedPreferences("act_status", MODE_PRIVATE);
        SharedPreferences.Editor editor = spActStatus.edit();
        editor.apply();

        // 상태바 숨기기
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        // 버튼셋팅
        btnClose = (ImageView) findViewById(R.id.btn_login_close);
        btnGoogle = (ConstraintLayout) findViewById(R.id.btn_google_login);
        btnGusetUser = (ConstraintLayout) findViewById(R.id.btn_guest);
        btnFacebook = (ConstraintLayout) findViewById(R.id.btn_facebook_login);

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
            setupGoogleLogin();
        });
        // 페이스북로그인
        btnFacebook.setOnClickListener(v -> {
            setupFacebookLogin();
           // LoginManager.getInstance().logOut();
        });
        // 게스트유저
        btnGusetUser.setOnClickListener(v -> {
            onBackPressed();
        });
        // 개인정보처리방침
        btnPIPP.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, InformationActivity.class);
            intent.putExtra("TYPE","PIPP");
            startActivity(intent);
        });

    }

    /**
     * 페이스북 로그인 진행
     **/
    private void setupFacebookLogin(){
        mFacebookCallbackManager = CallbackManager.Factory.create();
        mFacebookLoginCallback = new FacebookLoginCallback();
        mFacebookLoginCallback.setNavigator(this);
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(LoginActivity.this,
                Arrays.asList("public_profile","email"));
        loginManager.registerCallback(mFacebookCallbackManager,mFacebookLoginCallback);
    }

    /**
     * 구글로그인 진행
     **/
    private void setupGoogleLogin(){
        // 구글 로그인 구성
        // 사용자의 ID, 이메일 주소 및 기본 프로필을 요청하도록 로그인을 구성한다.
        // ID 및 기본 프로필을 DEFAULT_SIGN_IN 에 포함되어 있다.
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
        // GoogleSignInClient 를 빌드한다.
        // getClient 에 activity 와 gso 입력
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent,GOOGLE_SIGN_IN);
    }
    //GoogleSignInAccount 객체는 로그인한 사용자에 대한 정보가 포함되어있다.
    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //Log.e("email",account.getEmail());
            //Log.e("photoUrl",account.getPhotoUrl()+"");
            //Log.e("name",account.getDisplayName());
            // 메인 액티비티로 이동하고 로그인 완료
            moveToMainActivity(
                    account.getId(),
                    account.getEmail(),
                    account.getDisplayName(),
                    String.valueOf(account.getPhotoUrl()));
            // 받아오는데 성공하면 로그아웃
            mGoogleSignInClient.signOut();

        }catch (ApiException e){
            // 에러 감지
            Toast.makeText(this, "로그인되지 않았습니다.", Toast.LENGTH_SHORT).show();
            Log.e("Error","signInResult:failed code="+ e.getStatusCode());
        }
    }

    /**
     * 1. 구글로그인
     * 2. 페이스북 로그인
     **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 1. 구글로그인
        if(requestCode == GOOGLE_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
        // 2. 페이스북 로그인
        if(requestCode != GOOGLE_SIGN_IN){
            mFacebookCallbackManager.onActivityResult(requestCode,resultCode,data);
        }
    }

    /**
     * 로그인 하여 얻게된 유저의 정보를 저장하고 로그인 완료한 후 메인액티비티로 이동한다.
     **/
    @Override
    public void moveToMainActivity(String id, String email, String name, String url) {
        // 게스트유저 false
        SharedPreferences.Editor editor = spActStatus.edit();
        editor.putBoolean("guest_user",false);
        editor.putString("user_id",id);
        editor.putString("user_email",email);
        editor.putString("user_name",name);
        editor.putString("user_photo_url",url);
        editor.apply();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        setResult(MainActivity.LOGIN_COMPLETE, intent);
        finish();
    }

}
