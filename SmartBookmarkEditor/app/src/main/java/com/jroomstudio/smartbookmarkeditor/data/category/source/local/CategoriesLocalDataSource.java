package com.jroomstudio.smartbookmarkeditor.data.category.source.local;


import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.jroomstudio.smartbookmarkeditor.data.category.Category;
import com.jroomstudio.smartbookmarkeditor.data.category.source.CategoriesDataSource;
import com.jroomstudio.smartbookmarkeditor.util.AppExecutors;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 룸 데이터베이스의 categories 테이블에서 액세스하는 과정을 구체적으로 구현한다.
 **/
public class CategoriesLocalDataSource implements CategoriesDataSource {

    /**
     * - CategoriesLocalDataSource  의 INSTANCE
     * - volatile 은 Java 변수를 Main memory 에 저장하겠다는 것을 명시하는 것
     **/
    private static volatile CategoriesLocalDataSource  INSTANCE;

    /**
     * - categories 테이블 데이터에 액세스하는 인터페이스
     * - 클래스 인스턴스 생성시 입력받아 셋팅
     **/
    private CategoriesDAO mCategoriesDAO;

    /**
     * - 데이터베이스 작업 시 사용되는 쓰레드를 관리하는 Executor 프레임워크
     * - 클래스 인스턴스 생성시 입력받아 셋팅
     **/
    private AppExecutors mAppExecutors;

    // 다이렉트 인스턴스 방지
    private CategoriesLocalDataSource(@NonNull AppExecutors appExecutors,
                                     @NonNull CategoriesDAO categoriesDAO){
        mAppExecutors = appExecutors;
        mCategoriesDAO = categoriesDAO;
    }

    /**
     * 싱글 인스턴스를 리턴한다.
     * - 클래스 인스턴스 생성 메소드
     * - getInstance 메소드에 AppExecutors, BookmarksDAO 를 입력받는다.
     * - INSTANCE null 체크
     * - null 이면 new 키워드로 private 생성자로 인스턴스를 생성한다.
     * - 생성된 인스턴스를 반환한다.
     *
     * @param appExecutors 데이터 액세스를 실행 할 쓰레드를 관리하는 인스턴스
     * @param categoriesDAO 데이터 베이스에서 쿼리문으로 제어하는 인터페이스 인스턴스
     **/
    public static CategoriesLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                       @NonNull CategoriesDAO categoriesDAO){
        if(INSTANCE == null){
            synchronized (CategoriesLocalDataSource.class) {
                if(INSTANCE == null){
                    INSTANCE = new CategoriesLocalDataSource(appExecutors, categoriesDAO);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * INSTANCE 를 null 로
     **/
    @VisibleForTesting
    static void clearInstance() { INSTANCE = null; }

    /*
     * CategoriesDataSource 오버라이드 메소드 구현
     */

    @Override
    public void refreshCategories() {
        // {@link CategoriesRepository} 에서 처리하므로 이곳에서는 필요하지 않다.
    }

    // 로컬 데이터베이스에서 categories 리스트 가져오기
    @Override
    public void getCategories(@NonNull LoadCategoriesCallback callback) {
        Runnable runnable = () -> {
            final List<Category> categories = mCategoriesDAO.getAllCategories();
            mAppExecutors.getMainThread().execute(() -> {
                if(categories.isEmpty()){
                    // 새 테이블이거나 비어있는경우
                    callback.onDataNotAvailable();
                }else{
                    // 데이터 로드에 성공하여 리스트를 담아 콜백
                    callback.onCategoriesLoaded(categories);
                }
            });
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    // 로컬 데이터베이스에서 category 객체 가져오기
    @Override
    public void getCategory(@NonNull String id, @NonNull GetCategoryCallback callback) {
        Runnable runnable = () -> {
            final Category category = mCategoriesDAO.getCategoryById(id);
            mAppExecutors.getMainThread().execute(() -> {
                if(category != null){
                    // id 에 해당하는 아이템이 있는 경우
                    callback.onCategoryLoaded(category);
                }else{
                    // 아이템이 없으면
                    callback.onDataNotAvailable();
                }
            });
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    // category insert
    // 중복된 id 는 업데이트
    @Override
    public void saveCategory(@NonNull Category category) {
        checkNotNull(category);
        Runnable runnable = () -> {
            mCategoriesDAO.insertCategory(category);
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    // 모든 category 삭제
    @Override
    public void deleteAllCategories() {
        Runnable runnable = () -> {
            mCategoriesDAO.deleteAllCategories();
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    // id로 category 찾아서 삭제
    @Override
    public void deleteCategory(@NonNull String id) {
        Runnable deleteRunnable = () -> mCategoriesDAO.deleteCategoryById(id);
        mAppExecutors.getDiskIO().execute(deleteRunnable);
    }

    // 리스트상의 포지션 변경
    @Override
    public void updatePosition(@NonNull Category category, int position) {
        Runnable runnable = () -> {
            mCategoriesDAO.updatePosition(category.getId(),position);
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    @Override
    public void updatePosition(@NonNull String id, int position) {
        // {@link CategoriesRepository} 에서 처리하므로 이곳에서는 필요하지 않다.
    }

    //  객체 selected 여부 변경
    @Override
    public void selectedCategory(@NonNull Category category, boolean selected) {
        Runnable runnable = () -> {
          mCategoriesDAO.updateSelected(category.getId(), selected);
        };
    }

    @Override
    public void selectedCategory(@NonNull String id, boolean selected) {
        // {@link CategoriesRepository} 에서 처리하므로 이곳에서는 필요하지 않다.
    }

}
