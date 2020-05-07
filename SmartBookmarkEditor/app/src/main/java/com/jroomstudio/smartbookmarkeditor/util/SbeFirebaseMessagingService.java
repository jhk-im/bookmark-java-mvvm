package com.jroomstudio.smartbookmarkeditor.util;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.data.notice.Notice;
import com.jroomstudio.smartbookmarkeditor.data.notice.NoticeLocalDataSource;
import com.jroomstudio.smartbookmarkeditor.data.notice.NoticeLocalDatabase;
import com.jroomstudio.smartbookmarkeditor.notice.NoticeActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * FirebaseMessagingService -> 메세지를 수신하기 위해 상속받는 서비스
 *
 * onMessageReceived()
 * -> 앱이 백그라운드 or OFF 상태일때 기기의 작업표시줄로 전송된다. 알림을 탭하면 기본적으로 앱 런처가 열린다.
 * -> 데이터 페이로드가 포함된 메시지의 경우 알림은 기기 작업표시줄로 표시되고 페이로드는 인텐트 부가 정보로 전송된다.
 * onDeletedMessages()
 * ->
 * onNewToken()
 * ->
 **/
public class SbeFirebaseMessagingService extends FirebaseMessagingService {

    // notifications  로컬 데이터베이스 소스
    private NoticeLocalDataSource noticeLocalDataSource;

    // notice 데이터베이스 생성
    @Override
    public void onCreate() {
        super.onCreate();
        //Log.e("notice service","onCreate");
        // 알림 메세지 저장하는 룸 데이터베이스 생성
        NoticeLocalDatabase database = NoticeLocalDatabase.getInstance(this);
        noticeLocalDataSource = NoticeLocalDataSource.
                getInstance(new AppExecutors(), database.notificationsDAO());
    }

    /**
     * 구글 토큰을 얻는 값
     * 아래 토큰은 앱이 설치된 디바이스에 대한 고유값으로 푸시를 보낼때 사용
     **/
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        //Log.e("Firebase", "FirebaseInstanceIDService : " + s);
    }

    /**
     * 포그라운드 -> 알림, 데이터 모두 onMessageReceived 로 전달된다.
     * 백그라운드 or Off -> 알림은 작업표시줄 , 데이터는 onMessageReceived 로  전송
     * 전달된 메세지는 수신 후 20초 이내에 처리해야한다.
     **/
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String title;
        String body;

        // 알림 도착한 날짜 지정
        long now = System.currentTimeMillis();
        Date d = new Date(now);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat  mFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String date = mFormat.format(d);

        /*
        if (remoteMessage.getNotification() != null) {
             Log.e("RemoteMessage", "Body: " + remoteMessage.getNotification().getBody());
             Log.e("RemoteMessage", "Title: " + remoteMessage.getNotification().getTitle());
             Log.e("RemoteMessage", "click: " + remoteMessage.getNotification().getClickAction());
             //body = remoteMessage.getNotification().getBody();
             //title = remoteMessage.getNotification().getTitle();
        }
        */
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        // 메세지가 어디로부터 온 것인지 표시한다.
        // ex ) /topics/notice
        //Log.e("RemoteMessage", "From: " + remoteMessage.getFrom());
        // 데이터 알림
        // 백그라운드일 경우 작업표시줄로 전달되고
        // 포그라운드 일 경우 이곳으로 전달된다.
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e("RemoteMessage", "Message data payload: " + remoteMessage.getData());

            title = remoteMessage.getData().get("title");
            body = remoteMessage.getData().get("body");

            // 알림저장
            Notice notice = new Notice(title,body,date);
            noticeLocalDataSource.saveNotice(notice);

            //Notification Chanel 생성
            setNotificationChanel(title,body);
        }

    }


    /**
     * 오레오 버전부터는 포그라운드에서 Notification Chanel 이 없으면 푸시가 생성되지 않음
     **/
    void setNotificationChanel(String title, String body){

        // click action 지정
        Intent intent = new Intent(this, NoticeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_ONE_SHOT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channel = "notice";
            String channel_nm = "sbe_notice";

            NotificationManager notificationChannel = (android.app.NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channelMessage = new NotificationChannel(channel, channel_nm,
                    android.app.NotificationManager.IMPORTANCE_DEFAULT);
            channelMessage.setDescription("notice");
            channelMessage.enableLights(true);
            channelMessage.enableVibration(true);
            channelMessage.setShowBadge(true);
            channelMessage.setVibrationPattern(new long[]{100, 200, 100, 200});
            notificationChannel.createNotificationChannel(channelMessage);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channel)
                            .setSmallIcon(R.drawable.logo)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setChannelId(channel)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(9999, notificationBuilder.build());


        } else {
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, "")
                            .setSmallIcon(R.drawable.logo)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(9999, notificationBuilder.build());

        }
    }

}
