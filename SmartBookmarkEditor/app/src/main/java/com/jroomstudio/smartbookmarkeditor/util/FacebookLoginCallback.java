package com.jroomstudio.smartbookmarkeditor.util;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.jroomstudio.smartbookmarkeditor.login.LoginNavigator;

import org.json.JSONException;

public class FacebookLoginCallback implements FacebookCallback<LoginResult> {

    private LoginNavigator mNavigator;
    private String mId, mEmail, mName, mUrl;

    public void setNavigator(LoginNavigator loginNavigator){
        mNavigator = loginNavigator;
    }

    // 로그인 성공
    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.e("Callback ::","onSuccess");
        requestMe(loginResult.getAccessToken());
    }

    // 사용자 정보 요청
    private void requestMe(AccessToken token) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(token,
                (object, response) -> {
                    //Log.e("result",object.toString());
                    try{
                        mId = object.getString("id");
                        //Log.e("id",mId);
                        mEmail = object.getString("email");
                        //Log.e("email",mEmail);
                        mName = object.getString("name");
                        //Log.e("name",mName);
                        setUserData();
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();

    }

    // 로그인 창 닫을 때
    @Override
    public void onCancel() {
        Log.e("Callback :: ", "onCancel");
    }

    // 로그인 실패
    @Override
    public void onError(FacebookException error) {
        Log.e("Callback :: ", "onError : " + error.getMessage());
    }

    void setUserData(){
        // facebook 프로필 이미지  추가
        try{
            Profile profile = Profile.getCurrentProfile();
            mUrl = profile.getProfilePictureUri(200,200).toString();
            //Log.e("url",mUrl);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(mNavigator != null){
            LoginManager.getInstance().logOut();
            mNavigator.moveToMainActivity(mId,mEmail,mName,mUrl,1);
        }
    }
}