package com.jroomstudio.smartbookmarkeditor;

import android.content.Context;

import androidx.annotation.NonNull;

import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksRepository;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.local.BookmarksLocalDataSource;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.local.BookmarksLocalDatabase;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.remote.BookmarksRemoteDataSource;
import com.jroomstudio.smartbookmarkeditor.data.category.source.CategoriesRepository;
import com.jroomstudio.smartbookmarkeditor.data.category.source.local.CategoriesLocalDataSource;
import com.jroomstudio.smartbookmarkeditor.data.category.source.local.CategoriesLocalDatabase;
import com.jroomstudio.smartbookmarkeditor.data.category.source.remote.CategoriesRemoteDataSource;
import com.jroomstudio.smartbookmarkeditor.data.member.source.MemberRepository;
import com.jroomstudio.smartbookmarkeditor.data.member.source.remote.MemberRemoteDataSource;
import com.jroomstudio.smartbookmarkeditor.util.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;


public class Injection {

    public static BookmarksRepository provideBookmarksRepository(@NonNull Context context){
        checkNotNull(context);

        // 로컬 데이터 베이스 인스턴스 생성
        BookmarksLocalDatabase database = BookmarksLocalDatabase.getInstance(context);

        return BookmarksRepository.getInstance(
                BookmarksLocalDataSource.getInstance(new AppExecutors(),database.bookmarksDAO()),
                BookmarksRemoteDataSource.getInstance());
    }

    public static CategoriesRepository provideCategoriesRepository(@NonNull Context context){
        checkNotNull(context);

        // 로컬 데이터베이스 인스턴스 생성
        CategoriesLocalDatabase database = CategoriesLocalDatabase.getInstance(context);

        return CategoriesRepository.getInstance(
                CategoriesLocalDataSource.getInstance(new AppExecutors(), database.categoriesDAO()),
                CategoriesRemoteDataSource.getInstance()
        );
    }

    public static MemberRepository provideMemberRepository(@NonNull Context context){
        checkNotNull(context);

        // 로컬 데이터베이스 생성
        // MemberDatabase database = MemberDatabase.getInstance(context);

        return MemberRepository.getInstance(
                MemberRemoteDataSource.getInstance(new AppExecutors())
        );
    }

}