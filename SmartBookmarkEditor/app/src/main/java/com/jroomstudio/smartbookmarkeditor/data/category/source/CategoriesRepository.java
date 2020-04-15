package com.jroomstudio.smartbookmarkeditor.data.category.source;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksDataSource;
import com.jroomstudio.smartbookmarkeditor.data.category.Category;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 로컬 데이터베이스와 서버 데이터베이스 간의 동기화를 구현한다.
 * - 비회원은 로컬데이터를 사용한다.
 * - 회원은 로컬데이터와 원격데이터를 동기화하여 사용한다.
 **/
public class CategoriesRepository implements CategoriesDataSource {

    private static CategoriesRepository INSTANCE = null;

    /**
     * 해당 클래스가 getInstance 로 인스턴스가 생성될 때
     * CategoriesDataSource 를 상속받는 CategoriesLocalDataSource 인스턴스를 입력받아 셋팅한다.
     **/
    private final CategoriesDataSource mLocalDataSource;

    /**
     * 해당 클래스가 getInstance 로 인스턴스가 생성될 때
     * CategoriesDataSource 를 상속받는 CategoriesRemoteDataSource 인스턴스를 입력받아 셋팅된다.
     **/
    private final CategoriesDataSource mRemoteDataSource;

    /**
     * 이 변수에 패키지 로컬 visibility(가시성)이 있으므로 테스트에서 액세스할 수 있다.
     **/
    Map<String, Category> mCachedCategories;

    /**
     * 다음에 데이터를 요청할 때 강제로 업데이트 하도록 캐시를 유효하지 않은 것으로 표시
     * 이 변수는 패키지 local visibility 에 있으므로 테스트에서 액세스 할 수 있다.
     **/
    boolean mCacheDirty = false;

    // 다이렉트 인스턴스 방지
    private CategoriesRepository(@NonNull CategoriesDataSource localDataSource,
                                @NonNull CategoriesDataSource remoteDataSource) {
        mLocalDataSource = checkNotNull(localDataSource);
        mRemoteDataSource = checkNotNull(remoteDataSource);
    }

    /**
     * 싱글 인스턴스를 리턴한다.
     *
     * @param localDataSource 디바이스 저장소 데이터 소스
     * @param remoteDataSource 백엔드 데이터 소스
     **/
    public static CategoriesRepository getInstance(CategoriesDataSource localDataSource,
                                                   CategoriesDataSource remoteDataSource) {
        if(INSTANCE == null){
            INSTANCE = new CategoriesRepository(localDataSource,remoteDataSource);
        }
        return INSTANCE;
    }

    /**
     * {@link #getInstance(CategoriesDataSource, CategoriesDataSource)}
     * 호출될 때 새 인스턴스를 작성하도록 강제하는 데 사용된다.
     **/
    public static void destroyInstance() { INSTANCE = null; }

    /**
     * 입력받은 Category 리스트로 캐시메모리 refresh
     *
     * 캐시 메모리
     * - Map<String, Category>
     **/
    private void refreshCache(List<Category> categories){
        if(mCachedCategories == null){
            mCachedCategories = new LinkedHashMap<>();
        }
        mCachedCategories.clear();
        for(Category category : categories){
            mCachedCategories.put(category.getId(), category);
        }
        // 캐시가 refresh 되면 곧바로 응답해도 되기때문에 false 로 변경
        mCacheDirty = false;
    }

    /**
     * - Category 의 id 를 입력하여 캐시 메모리 에서 Category 객체를 찾아 반환한다.
     * - Test 시 사용된다.
     *
     * 캐시메모리
     * - Map<String, Category>
     **/
    @Nullable
    private Category getCategoryWithId(@NonNull String id) {
        checkNotNull(id);
        if(mCachedCategories == null || mCachedCategories.isEmpty()){
            return null;
        }else{
            return mCachedCategories.get(id);
        }
    }

    // 원격으로부터 데이터를 전송받아 로컬을 refresh 하는 경우
    private void refreshLocalDataSource(List<Category> categories){
        mLocalDataSource.deleteAllCategories();
        for(Category category : categories){
            mLocalDataSource.saveCategory(category);
        }
    }

    // 로컬에 문제가 있는경우 원격에서 해야할때
    private void getCategoriesFromRemoteDataSource(@NonNull final LoadCategoriesCallback callback){
        mRemoteDataSource.getCategories(new LoadCategoriesCallback() {
            @Override
            public void onCategoriesLoaded(List<Category> categories) {
                refreshCache(categories);
                refreshLocalDataSource(categories);
                callback.onCategoriesLoaded(new ArrayList<>(mCachedCategories.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });

    }


    /*
     * CategoriesDataSource 오버라이드 메소드 구현
     */

    // category 데이터베이스에서 데이터가 변경되면 캐시메모리를 refresh 해야한다.
    @Override
    public void refreshCategories() {
        // true 일 경우 캐시에서 리스트를 전달하지 않고 데이터베이스에서 가져와 refresh 후 전달
        mCacheDirty = true;
    }

    /**
     * - 저장소 테이블에서 모든 Category 객체의 정보를 가져온다.
     * - 캐시, 로컬 데이터소스(SQLite) 또는 원격 데이터 소스 중 먼저 사용 가능한 작업을 가져온다.
     *
     * {@link BookmarksDataSource.LoadBookmarksCallback#onDataNotAvailable()}
     * 모든 데이터 소스가 데이터를 가져오지 못하면 실행된다.
     **/
    @Override
    public void getCategories(@NonNull LoadCategoriesCallback callback) {
        checkNotNull(callback);
        // Map<String, Bookmark> 이 null 이 아니고 mCacheDirty 가 false 일때는 캐시메모리로 즉시 응답
        // 즉, remote 나 local 로 부터 데이터를 받아오는데 성공 한 후
        // 캐시 메모리의 강제 업데이트가 필요 없는 경우는 캐시메모리로 응답한다.
        /*
        if (mCachedCategories != null && !mCacheDirty) {
            callback.onCategoriesLoaded(new ArrayList<>(mCachedCategories.values()));
            return;
        }
        */
        // mCacheDirty 가 true 이면 데이터가 변경되어 refresh 해야하는 상황
        /*
        if(mCacheDirty) {
            getCategoriesFromRemoteDataSource(callback);
        }else{
        }
        */
        // LocalDataSource 로 부터 데이터를 가져온다.
        mLocalDataSource.getCategories(new LoadCategoriesCallback() {
            @Override
            public void onCategoriesLoaded(List<Category> categories) {
                // 로드성공
                // 받아온 데이터는 캐쉬메모리에 refresh
                refreshCache(categories);
                callback.onCategoriesLoaded(new ArrayList<>(mCachedCategories.values()));
                //callback.onCategoriesLoaded(categories);
            }
            @Override
            public void onDataNotAvailable() {
                // 로컬이 비어있을때 원격에서 확인한다.
                // getCategoriesFromRemoteDataSource(callback);
            }
        });
    }


    /**
     * - 로컬 데이터 저장소 에 액세스 한다
     * - 로컬 테이블이 비어있거나 없으면 네트워크 데이터 소스를 사용한다.
     * - 샘플을 단순화하기 위해 수행된다.
     *
     * {@link BookmarksDataSource.GetBookmarkCallback#onDataNotAvailable()}
     * 모든 데이터 소스가 데이터를 가져오지 못하면 실행된다.
     **/
    @Override
    public void getCategory(@NonNull String id, @NonNull GetCategoryCallback callback) {
        checkNotNull(id);
        checkNotNull(callback);

        // 캐시메모리에서 찾기
        Category cachedCategory = getCategoryWithId(id);

        // 캐시에서 해당 id의 Category 가 있을 경우 즉시응답

        if(cachedCategory != null){
            callback.onCategoryLoaded(cachedCategory);
            return;
        }

        // 캐시에 없는경우 로컬에서 Category 객체를 가져온다.
        mLocalDataSource.getCategory(id, new GetCategoryCallback() {
            @Override
            public void onCategoryLoaded(Category category) {
                // 로드성공
                // 캐시메모리 업데이트
                if(mCachedCategories == null){
                    mCachedCategories = new LinkedHashMap<>();
                }
                // 캐시메모리에도 추가
                mCachedCategories.put(category.getId(),category);
                callback.onCategoryLoaded(category);
            }

            // 데이터가 없을 때
            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
                /*
                // 원격에서 찾는다.
                mRemoteDataSource.getCategory(id, new GetCategoryCallback() {
                    @Override
                    public void onCategoryLoaded(Category category) {
                        // 로드성공
                        // 캐시메모리 업데이트
                        if(mCachedCategories == null){
                            mCachedCategories = new LinkedHashMap<>();
                        }
                        // 캐시메모리에도 추가
                        mCachedCategories.put(category.getId(),category);
                        callback.onCategoryLoaded(category);
                    }

                    @Override
                    public void onDataNotAvailable() { onDataNotAvailable(); }
                });
                */
            }
        });
    }

    // 로컬과 원격 데이터베이스에 모두 Category 를 각각 저장한다.
    @Override
    public void saveCategory(@NonNull Category category) {
        checkNotNull(category);
        // 로컬, 원격
        mLocalDataSource.saveCategory(category);
        //mRemoteDataSource.saveCategory(category);

        // 캐시메모리 업데이트
        if (mCachedCategories == null){
            mCachedCategories = new LinkedHashMap<>();
        }
        mCachedCategories.put(category.getId(), category);
    }

    // 모든 카테고리 삭제
    @Override
    public void deleteAllCategories() {
        // 로컬, 원격
        mLocalDataSource.deleteAllCategories();
        //mRemoteDataSource.deleteAllCategories();

        // 캐시메모리 업데이트
        if (mCachedCategories == null){
            mCachedCategories = new LinkedHashMap<>();
        }
        mCachedCategories.clear();
    }

    // category 삭제
    @Override
    public void deleteCategory(@NonNull String id) {
        // 로컬, 원격
        mLocalDataSource.deleteCategory(checkNotNull(id));
        //mRemoteDataSource.deleteCategory(checkNotNull(id));
        // 캐시메모리
        mCachedCategories.remove(id);
    }

    @Override
    public void updatePosition(@NonNull Category category, int position) {
        checkNotNull(category);
        checkNotNull(position);
        // 로컬, 원격
        mLocalDataSource.updatePosition(category,position);
        //mRemoteDataSource.updatePosition(category,position);
        Category updateCategory =
                new Category(category.getId(),category.getTitle(),position,category.isSelected());

        // 캐시메모리 업데이트
        if (mCachedCategories == null){
            mCachedCategories = new LinkedHashMap<>();
        }
        mCachedCategories.put(category.getId(),updateCategory);
    }

    /**
     * id 를 입력받아 캐시 메모리에서 category 객체를 가져와 updatePosition(category,int) 을 호출한다.
     * - test 에서 활용
     **/
    @Override
    public void updatePosition(@NonNull String id, int position) {
        checkNotNull(id);
        checkNotNull(position);
        updatePosition(getCategoryWithId(id),position);
    }

    // 로컬, 원격에서 입력된 Category 의 selected(true/false) 로 변경
    @Override
    public void selectedCategory(@NonNull Category category, boolean selected) {
        checkNotNull(category);
        mLocalDataSource.selectedCategory(category,selected);
        //mRemoteDataSource.selectedCategory(category,selected);

        Category selectedCategory =
                new Category(category.getId(),category.getTitle(),category.getPosition(),selected);

        if(mCachedCategories == null){
            mCachedCategories = new LinkedHashMap<>();
        }
        mCachedCategories.put(category.getId(),selectedCategory);
    }

    // 입력된 id로 category 객체 찾아서 selected true 로 변경
    @Override
    public void selectedCategory(@NonNull String id,boolean selected) {
        checkNotNull(id);
        selectedCategory(getCategoryWithId(id),selected);
    }
}
