package com.jroomstudio.commentstube;

import android.content.Context;

import androidx.annotation.NonNull;

import com.jroomstudio.commentstube.data.source.TabsRepository;
import com.jroomstudio.commentstube.data.source.local.AppLocalDatabase;
import com.jroomstudio.commentstube.data.source.local.TabsLocalDataSource;
import com.jroomstudio.commentstube.data.source.remote.TabsRemoteDataSource;
import com.jroomstudio.commentstube.util.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;

public class Injection {

    public static TabsRepository provideTabsRepository(@NonNull Context context) {
        checkNotNull(context);
        AppLocalDatabase database = AppLocalDatabase.getInstance(context);
        return TabsRepository.getInstance(
                TabsLocalDataSource.getInstance(new AppExecutors(),database.tabsDao()),
                TabsRemoteDataSource.getInstance());
    }

}