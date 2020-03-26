package com.jroomstudio.commentstube;

import android.content.Context;

import androidx.annotation.NonNull;

import com.jroomstudio.commentstube.data.FakeTabsRemoteDataSource;
import com.jroomstudio.commentstube.data.source.TabsRepository;
import com.jroomstudio.commentstube.data.source.local.AppLocalDatabase;
import com.jroomstudio.commentstube.data.source.local.TabsLocalDataSource;
import com.jroomstudio.commentstube.util.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 컴파일 시 모의 구현을 주입할 수 있다.
 * 클래스의 가짜 인스턴스를 사용하여 종속성을 격리하고
 * 테스트를 완벽하게 실행할 수 있어서 유용하다.
**/

public class Injection {

    public static TabsRepository provideTabsRepostiory(@NonNull Context context) {
        checkNotNull(context);
        AppLocalDatabase database = AppLocalDatabase.getInstance(context);
        return TabsRepository.getInstance(FakeTabsRemoteDataSource.getInstance(),
                TabsLocalDataSource.getInstance(new AppExecutors(),database.tabsDao()));
    }

}
