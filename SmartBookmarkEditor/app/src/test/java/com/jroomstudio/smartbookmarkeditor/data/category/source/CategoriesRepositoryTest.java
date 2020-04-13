package com.jroomstudio.smartbookmarkeditor.data.category.source;

import com.google.common.collect.Lists;
import com.jroomstudio.smartbookmarkeditor.data.category.Category;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * 카테고리 캐시를 사용하여 로컬과 원격 저장소 단위테스트
 */
public class CategoriesRepositoryTest {

    private final static String TITLE1 = "Best";
    private final static String TITLE2 = "Community";
    private final static String TITLE3 = "Study";

    private static List<Category> CATEGORIES =
            Lists.newArrayList(new Category("Best",0,true),
                    new Category("Community",1,false),
                    new Category("Study",2,false));

    private CategoriesRepository mCategoriesRepository;

    /**
     * 주석설명
     * Mock (org.mockito.Mock)
     * -> Mock 객체를 만드는데 사용된다.
     * Captor (org.mockito.Captor)
     * -> verification(확인) 단계에서 argument 로 사용된 객체를 capture 한다.
     * Test (org.junit.Test)
     * -> 테스트 코드임을 표시한다.
     * Before (org.junit.Before)
     * -> Test 주석을 시작하기 전 사전에 진행해야 할 정의에 해당한다.
     * -> Test 주석이 시작되기 전 항상 호출된다.
     * After (org.junit.After)
     * -> Test 주석을 실행한 후 호출된다.
     * -> 테스트가 모두 끝난 후 임시로 사용된 객체를 리셋한다.
     **/

    @Mock
    private CategoriesDataSource mLocalDataSource;

    @Mock
    private CategoriesDataSource mRemoteDataSource;

    @Mock
    private CategoriesDataSource.LoadCategoriesCallback loadCategoriesCallback;

    @Mock
    private CategoriesDataSource.GetCategoryCallback getCategoryCallback;

    /**
     * ArgumentCaptor
     * - 특정 메소드에 사용되는 argument(전달인자 or 입력값)를 capture(저장) 한다.
     * - getValue() 메소드를 통해 나중에 다시 사용할 수 있다.
     **/
    @Captor
    private ArgumentCaptor<CategoriesDataSource.LoadCategoriesCallback> loadCategoriesCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<CategoriesDataSource.GetCategoryCallback> getCategoryCallbackArgumentCaptor;

    // Test 전 셋팅
    @Before
    public void setupCategoriesRepository() {
        // Mock 객체 초기화
        MockitoAnnotations.initMocks(this);
        // Mock 이 아닌 실제 객체의 인스턴스를 생성한다.
        mCategoriesRepository = CategoriesRepository.getInstance(mLocalDataSource,mRemoteDataSource);
    }

    // Mock 객체가 아닌 실제 객체의 인스턴스가 생성되었기 때문에 test 종료 후 인스턴스를 제거한다.
    @After
    public void destroyRepositoryInstance() { CategoriesRepository.destroyInstance(); }

    // test - 원격과 로컬 모두 콜백할 때 문제없이 호출되는가?
    @Test
    public void getCategories_repositoryCachesAfterFirstAPICall(){
        // 2번에 걸쳐 원격과 로컬에 데이터 요청
        towBookmarksLoadCallsToRepository(loadCategoriesCallback);
        // verify() 로 원격 데이터소스가 조회되었는지 확인
        verify(mRemoteDataSource).getCategories(any(CategoriesDataSource.LoadCategoriesCallback.class));
    }

    // 로컬 getCategories() test
    @Test
    public void getCategories_requestAllCategoriesFromLocalDataSource(){
        // categories 요청
        mCategoriesRepository.getCategories(loadCategoriesCallback);
        // 로컬에서 조회되는지 확인
        verify(mLocalDataSource).getCategories(any(CategoriesDataSource.LoadCategoriesCallback.class));
    }

    // 로컬 saveCategory() test
    @Test
    public void saveCategory_savesCategoryToServiceAPI(){
        // 객체생성후 저장
        Category category = new Category(TITLE1,0,true);
        mCategoriesRepository.saveCategory(category);
        // 원격 소스 -> 데이터 로드되는지 확인
        verify(mRemoteDataSource).saveCategory(category);
        // 로컬소스  -> 데이터 로드되는지 확인
        verify(mLocalDataSource).saveCategory(category);

        // junit assertThat()
        // mCachedBookmarks Map 에 size 1 인지 확인한다.
        // 추가되었으면 문제없이 test 완료
        assertThat(mCategoriesRepository.mCachedCategories.size(), is(1));
    }

    // 로컬 updatePosition(Category,position) test
    @Test
    public void updatePosition_ServiceAPIUpdatesCache(){
        // 객체생성후 저장
        Category category = new Category(TITLE1,0,true);
        mCategoriesRepository.saveCategory(category);

        // 포지션값 변경
        mCategoriesRepository.updatePosition(category,1);
        // 원격 소스 데이터로드 확인
        verify(mRemoteDataSource).updatePosition(category,1);
        // 로컬 소스 데이터로드 확인
        verify(mLocalDataSource).updatePosition(category,1);
        // junit assertThat()
        // mCachedBookmarks 에 추가된 bookmark 의 position 값이 업데이트 되었는지 확인
        assertThat(mCategoriesRepository.mCachedCategories.get(category.getId()).getPosition(), is(1));
    }


    // 로컬 updatePosition(Category,position) test
    @Test
    public void updatePositionId_ServiceAPIUpdatesCache(){
        // 객체생성후 저장
        Category category = new Category(TITLE1,0,true);
        mCategoriesRepository.saveCategory(category);

        // id 로 변경
        mCategoriesRepository.updatePosition(category.getId(),1);
        // 원격 소스 데이터로드 확인
        verify(mRemoteDataSource).updatePosition(category,1);
        // 로컬 소스 데이터로드 확인
        verify(mLocalDataSource).updatePosition(category,1);
        // junit assertThat()
        // mCachedBookmarks 에 추가된 bookmark 의 position 값이 업데이트 되었는지 확인
        assertThat(mCategoriesRepository.mCachedCategories.get(category.getId()).getPosition(), is(1));
    }

    // 로컬 selectedCategory(id,boolean selected) test
    @Test
    public void selectedCategoryId_ServiceAPIUpdateCache() {
        // 객체 생성 후 저장
        Category category = new Category(TITLE1,0,true);
        mCategoriesRepository.saveCategory(category);

        // selected false 로 변경
        mCategoriesRepository.selectedCategory(category.getId(),false);
        // 원격 소스 데이터로드 확인
        verify(mRemoteDataSource).selectedCategory(category,false);
        // 로컬 소스 데이터로드 확인
        verify(mLocalDataSource).selectedCategory(category,false);
        // false 로 변경되었는지 확인
        assertThat(mCategoriesRepository.mCachedCategories.get(category.getId()).isSelected(), is(false));

        // selected true 로 변경
        mCategoriesRepository.selectedCategory(category.getId(),true);
        // 원격 소스 데이터로드 확인
        verify(mRemoteDataSource).selectedCategory(category,true);
        // 로컬 소스 데이터로드 확인
        verify(mLocalDataSource).selectedCategory(category,true);
        // true 로 변경되었는지 확인
        assertThat(mCategoriesRepository.mCachedCategories.get(category.getId()).isSelected(), is(true));

    }

    // 로컬 selectedCategory(category,boolean selected) test
    @Test
    public void selectedCategory_ServiceAPIUpdateCache() {
        // 객체 생성 후 저장
        Category category = new Category(TITLE1,0,true);
        mCategoriesRepository.saveCategory(category);

        // selected false 로 변경
        mCategoriesRepository.selectedCategory(category,false);
        // 원격 소스 데이터로드 확인
        verify(mRemoteDataSource).selectedCategory(category,false);
        // 로컬 소스 데이터로드 확인
        verify(mLocalDataSource).selectedCategory(category,false);
        // false 로 변경되었는지 확인
        assertThat(mCategoriesRepository.mCachedCategories.get(category.getId()).isSelected(), is(false));

        // selected true 로 변경
        mCategoriesRepository.selectedCategory(category,true);
        // 원격 소스 데이터로드 확인
        verify(mRemoteDataSource).selectedCategory(category,true);
        // 로컬 소스 데이터로드 확인
        verify(mLocalDataSource).selectedCategory(category,true);
        // true 로 변경되었는지 확인
        assertThat(mCategoriesRepository.mCachedCategories.get(category.getId()).isSelected(), is(true));

    }


    // f로컬 getCategory() test
    @Test
    public void getCategory_requestSingleCategoryFromLocal(){
        // getCategory()
        mCategoriesRepository.getCategory(TITLE1,getCategoryCallback);
        // verify() -> 조회확인
        // eq()  import static org.mockito.Matchers.eq;
        // -> 주어진 값과 같은 객체 인수
        // any() -> callback 되는지 확인
        verify(mLocalDataSource).getCategory(eq(TITLE1),
                any(CategoriesDataSource.GetCategoryCallback.class));
    }

    // 로컬 deleteAllCategories() test
    @Test
    public void deleteAllCategories_ToServiceAPIUpdatesCache() {
        // 객체생성후 저장
        Category category1 = new Category(TITLE1,0,true);
        mCategoriesRepository.saveCategory(category1);
        Category category2 = new Category(TITLE2,1,false);
        mCategoriesRepository.saveCategory(category2);
        Category category3 = new Category(TITLE3,2,false);
        mCategoriesRepository.saveCategory(category3);

        // deleteAllBookmark() 실행
        mCategoriesRepository.deleteAllCategories();
        // 데이터 조회확인
        verify(mRemoteDataSource).deleteAllCategories();
        verify(mLocalDataSource).deleteAllCategories();
        // junit assertThat()
        // mCachedBookmarks Map 에 size 0 인지 확인한다.
        // 모두 삭제되었으면 문제없이 test 완료
        assertThat(mCategoriesRepository.mCachedCategories.size(), is(0));

    }

    // 로컬 deleteCategory(id)
    @Test
    public void deleteCategoryId_ToServiceAPIUpdatesCache() {
        // 객체생성후 저장
        Category category1 = new Category(TITLE1,0,true);
        mCategoriesRepository.saveCategory(category1);
        // 저장된 객체 존재하는지 확인
        assertThat(mCategoriesRepository.mCachedCategories.containsKey(category1.getId()), is(true));

        // id로 아이디 삭제하기
        mCategoriesRepository.deleteCategory(category1.getId());
        // 데이터 조회확인
        verify(mRemoteDataSource).deleteCategory(category1.getId());
        verify(mLocalDataSource).deleteCategory(category1.getId());

        // 삭제확인
        assertThat(mCategoriesRepository.mCachedCategories.containsKey(category1.getId()), is(false));
    }

    // 로컬 DirtyCache test
    @Test
    public void getCategoriesWithDirtyCache_RetrievedFromRemote(){
        // DirtyCached 가 false 인 경우
        // 캐시메모리 refresh 가 완료된 경우
        mCategoriesRepository.refreshCategories();
        // getCategories() 요청
        mCategoriesRepository.getCategories(loadCategoriesCallback);

        // DirtyCache false 로 원격데이터 조회하기
        setCategoriesAvailable(mRemoteDataSource, CATEGORIES);

        // 로컬이 아닌 원격 데이터에서 소스가 반환되는지 확인
        // never() (org.mockito.Mockito.never)
        // -> 호출된적 없음을 확인한다.
        verify(mLocalDataSource, never()).getCategories(loadCategoriesCallback);
        // 로드 콜백에 임시로 만든 리스트가 전달되었는지 확인
        verify(loadCategoriesCallback).onCategoriesLoaded(CATEGORIES);
    }


    private void setCategoriesAvailable(CategoriesDataSource dataSource, List<Category> categories){
        // 데이터소스 로드 되는지 확인
        verify(dataSource).getCategories(loadCategoriesCallbackArgumentCaptor.capture());
        // 캡처에임의의 리스트 실어보냄
        loadCategoriesCallbackArgumentCaptor.getValue().onCategoriesLoaded(categories);
    }

    /**
     * 데이터 베이스에 두번의 호출을 발행
     * verify (org.mockito.Mockito.verify)
     * -> mock 객체는 자신의 모든 행동을 기록한다.
     * -> verify() 를 이용해 특정 조건으로 실행되었는지 검증할 수 있다.
     **/
    private void towBookmarksLoadCallsToRepository(CategoriesRepository.LoadCategoriesCallback callback){

        // 1. 최상위 클래스에 작업을 요청
        mCategoriesRepository.getCategories(callback); // 첫번째 api call

        // 2.verify() 로 로컬 데이터소스가 조회되었는지 확인
        verify(mLocalDataSource).getCategories(loadCategoriesCallbackArgumentCaptor.capture());

        // 3. localArgumentCaptor 의 getValue() 에서
        // 데이터가 없다는 콜백인 onDataNotAvailable() 메소드를 리턴
        loadCategoriesCallbackArgumentCaptor.getValue().onDataNotAvailable();

        // 데이터가 없으면 원격 데이터를 조회하게된다.
        // 4. verify() 로 원격 데이터소스가 조회되었는지 확인
        verify(mRemoteDataSource).getCategories(loadCategoriesCallbackArgumentCaptor.capture());

        // 5. 데이터가 캐시 되도록 임의의 Bookmark 객체 리스트를 담아서
        // 데이터가 있을때 인자값에 넣어 전달하는 콜백메소드 리턴
        loadCategoriesCallbackArgumentCaptor.getValue().onCategoriesLoaded(CATEGORIES);

        // 6. 다시한번 최상위 클래스의 getBookmarks 에 LoadBookmarksCallback 클래스 입력
        mCategoriesRepository.getCategories(callback);// 두번째 api call

    }
}
