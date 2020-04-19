package com.jroomstudio.smartbookmarkeditor.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 네트워크 연결상태를 리턴한다.
 **/
public class NetworkStatus {

    // 인터넷 연결상태를 확인하여 boolean 값을 반환한다.
    public static boolean getConnectivityStatus(Context context){
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

       NetworkInfo networkInfo = manager.getActiveNetworkInfo();

       return networkInfo != null && networkInfo.isConnected();
    }

}
