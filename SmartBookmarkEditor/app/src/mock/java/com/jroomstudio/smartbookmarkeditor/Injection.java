package com.jroomstudio.smartbookmarkeditor;


import android.content.Context;

import androidx.annotation.NonNull;

import com.jroomstudio.smartbookmarkeditor.data.FakeBookmarksRemoteDataSource;
import com.jroomstudio.smartbookmarkeditor.data.FakeCategoriesRemoteDataSource;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksRepository;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.local.BookmarksLocalDataSource;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.local.BookmarksLocalDatabase;
import com.jroomstudio.smartbookmarkeditor.data.category.source.CategoriesRepository;
import com.jroomstudio.smartbookmarkeditor.data.category.source.local.CategoriesLocalDataSource;
import com.jroomstudio.smartbookmarkeditor.data.category.source.local.CategoriesLocalDatabase;
import com.jroomstudio.smartbookmarkeditor.util.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 컴파일 시 BookmarksDataSource 에 대한 모의 구현을 주입할 수 있다.
 * {@link FakeBookmarksRemoteDataSource} , {@link FakeCategoriesRemoteDataSource}
 * 클래스의 가짜 인스턴스를 사용하여 종속성을 격리하고 테스트를 완벽하게 실행할 수 있어서 유용하다.
 **/
public class Injection {

    public static BookmarksRepository provideBookmarksRepository(@NonNull Context context){
        checkNotNull(context);

        // 로컬 데이터 베이스 인스턴스 생성
        BookmarksLocalDatabase database = BookmarksLocalDatabase.getInstance(context);

        /*
        BookmarksRepository 인스턴스 생성
          -> BookmarksLocalDataSource 인스턴스 생성
            -> AppExecutors 생성후 입력 , 북마크 데이터베이스의 DAO 입력
          -> FakeBookmarksRemoteDataSource 인스턴스 생성
        생성된 BookmarksRepository 반환
        */
        return BookmarksRepository.getInstance(
          BookmarksLocalDataSource.getInstance(new AppExecutors(),database.bookmarksDAO()),
                FakeBookmarksRemoteDataSource.getInstance());
    }

    public static CategoriesRepository provideCategoriesRepository(@NonNull Context context){
        checkNotNull(context);

        // 로컬 데이터베이스 인스턴스 생성
        CategoriesLocalDatabase database = CategoriesLocalDatabase.getInstance(context);

        /*
        CategoriesRepository 인스턴스 생성
           -> CategoriesLocalDataSource 인스턴스 생성
             -> AppExecutors 생성 후 입력 , 카테고리 데이터베이스의 DAO 입력
           -> FakeCategoriesRemoteDataSource 인스턴스생성
        생성된 CategoriesRepository 반환
        */
        return CategoriesRepository.getInstance(
                CategoriesLocalDataSource.getInstance(new AppExecutors(), database.categoriesDAO()),
                FakeCategoriesRemoteDataSource.getInstance()
        );
    }


}
