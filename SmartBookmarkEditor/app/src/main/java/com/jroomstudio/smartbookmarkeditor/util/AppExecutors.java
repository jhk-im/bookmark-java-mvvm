package com.jroomstudio.smartbookmarkeditor.util;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 전체 애플리케이션을 위한 실행기 풀 ??
 * 스레드가 사용하는 자원에 대한 새로운 차원의 제어를 가능하게 한다.
 * - 데이터베이스 액세스 시 메인쓰레드를 사용하면 에러발생
 * - 로컬 액세스 쓰레드 , 네트워크 액세스 쓰레드, 메인 쓰레드 를 구분지어서 사용하도록 구현.
 * */

public class AppExecutors {

    private static final int THREAD_COUNT = 2;

    private final Executor diskIO;
    private final Executor networkIO;
    private final Executor mainThread;

    @VisibleForTesting
    public AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    public AppExecutors(){
        this(new DiskIOThreadExecutor(), Executors.newFixedThreadPool(THREAD_COUNT),
                new MainThreadExecutor());
    }

    public Executor getDiskIO() {
        return diskIO;
    }

    public Executor getNetworkIO() {
        return networkIO;
    }

    public Executor getMainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

}
