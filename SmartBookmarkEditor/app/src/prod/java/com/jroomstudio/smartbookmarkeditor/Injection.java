package com.jroomstudio.smartbookmarkeditor;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.local.BookmarksLocalDatabase;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.local.BookmarksLocalRepository;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.remote.BookmarksRemoteRepository;
import com.jroomstudio.smartbookmarkeditor.data.category.source.local.CategoriesLocalDatabase;
import com.jroomstudio.smartbookmarkeditor.data.category.source.local.CategoriesLocalRepository;
import com.jroomstudio.smartbookmarkeditor.data.member.MemberRemoteRepository;
import com.jroomstudio.smartbookmarkeditor.util.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;


public class Injection {

    // 북마크 로컬 데이터베이스
    public static BookmarksLocalRepository provideBookmarksRepository(@NonNull Context context){
        checkNotNull(context);
        // 로컬 데이터 베이스 인스턴스 생성
        BookmarksLocalDatabase database = BookmarksLocalDatabase.getInstance(context);
        return BookmarksLocalRepository.getInstance(new AppExecutors(),database.bookmarksDAO());
    }

    // 카테고리 로컬 데이터베이스
    public static CategoriesLocalRepository provideCategoriesRepository(@NonNull Context context){
        checkNotNull(context);
        // 로컬 데이터베이스 인스턴스 생성
        CategoriesLocalDatabase database = CategoriesLocalDatabase.getInstance(context);
        return CategoriesLocalRepository.getInstance(new AppExecutors(), database.categoriesDAO());
    }

    // 멤버 원격 데이터베이스
    public static MemberRemoteRepository provideMemberRepository(
            @NonNull SharedPreferences sharedPreferences){
        checkNotNull(sharedPreferences);
        return MemberRemoteRepository.getInstance(new AppExecutors(),sharedPreferences);
    }

    // 북마크 원격 데이터베이스
    public static BookmarksRemoteRepository provideRemoteBookmarksRepository(
            @NonNull SharedPreferences sharedPreferences){
        checkNotNull(sharedPreferences);
        return BookmarksRemoteRepository.getInstance(new AppExecutors(), sharedPreferences);
    }

}