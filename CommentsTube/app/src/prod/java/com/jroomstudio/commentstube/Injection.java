package com.jroomstudio.commentstube;

public class Injection {

    public static TabsRepository provideTabsRepostiory(@NonNull Context context) {
        checkNotNull(context);
        AppExecutors executors = new AppExecutors();
        AppLocalDatabase database = AppLocalDatabase.getInstance(context,executors);
        return TabsRepository.getInstance(RemoteTabsRemoteDataSource.getInstance(),
                TabsLocalDataSource.getInstance(executors,database.tabsDao()));
    }

}