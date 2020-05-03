package com.jroomstudio.smartbookmarkeditor.login;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.Profile;
import com.facebook.login.LoginResult;

import org.json.JSONException;

public class FacebookLoginCallback implements FacebookCallback<LoginResult> {

    private LoginNavigator mNavigator;

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
                    Log.e("result",object.toString());
                    try{
                        String id = object.getString("id");
                        String email = object.getString("email");
                        String name = object.getString("name");
                        Profile profile = Profile.getCurrentProfile();
                        String url = profile.getProfilePictureUri(200,200).toString();
                        if(mNavigator != null){
                            mNavigator.moveToMainActivity(id,email,name,url);
                        }
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
}