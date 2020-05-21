package com.jroomstudio.smartbookmarkeditor.popup;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.jroomstudio.smartbookmarkeditor.BR;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.local.BookmarksLocalDataSource;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.local.BookmarksLocalRepository;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.remote.BookmarksRemoteRepository;
import com.jroomstudio.smartbookmarkeditor.data.category.Category;
import com.jroomstudio.smartbookmarkeditor.data.category.source.local.CategoriesLocalDataSource;
import com.jroomstudio.smartbookmarkeditor.data.category.source.local.CategoriesLocalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditAddItemPopupViewModel extends BaseObservable {

    /**
     * 뷰 관찰 변수
     **/
    // 카테고리 타이틀 입력 관찰
    public final ObservableField<String> categoryTitle = new ObservableField<>();
    // 북마크 타이틀 입력 관찰변수
    public final ObservableField<String> bookmarkTitle = new ObservableField<>();
    // 북마크 url 입력 관찰 변수
    public final ObservableField<String> bookmarkUrl = new ObservableField<>();
    // 북마크 카테고리 스피너 현재 선택된 카테고리 관찰변수
    public final ObservableField<String> bookmarkCategory = new ObservableField<>();
    // 카테고리 or 북마크 구분하는 관찰변수
    public final ObservableBoolean isSelectBookmark = new ObservableBoolean();
    // 스피너 카테고리 리스트 관찰변수
    public final ObservableList<String> categories = new ObservableArrayList<>();
    // 추가 or 편집(카테고리) or 편집(북마크) 타이틀제목 표시 관찰변수
    public final ObservableField<String> viewTitle = new ObservableField<>();
    // 삭제시 안내문구
    public final ObservableField<String> deleteQuestions = new ObservableField<>();

    // 편집과 추가 를 구분
    private boolean mIsAddItem;

    // 뷰형식 구분자
    private String mViewType;
    // 편집할 아이템의 아이디
    private String mEditItemId;
    // 편집할 카테고리 객체
    private Category mUpdateCategory;
    // 카테고리 편집할 경우 같이 변동할 북마크 리스트
    private List<Bookmark> mUpdateBookmarks = new ArrayList<Bookmark>();
    // 편집할 북마크 객체
    private Bookmark mUpdateBookmark;

    // 데이터 로딩바 표시
    private boolean mIsDataLoadingBar;

    // 아이템 삭제시 구분자
    private boolean mIsDeleteItem;
    // 아이템 삭제시 타입 구분자 ( 북마크 or 카테고리 )
    private String mDeleteItemType;

    // 액티비티 네비게이터
    private EditAddItemPopupNavigator mNavigator;
    // 액티비티 시작시 네비게이터 셋팅
    void onActivityCreated(EditAddItemPopupNavigator navigator){ mNavigator = navigator; }
    // 액티비티 종료시 네비게이터 종료
    void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mNavigator = null;
    }

    // 북마크 로컬 데이터 소스
    private BookmarksLocalRepository mBookmarksLocalRepository;
    // 카테고리 로컬 데이터 소스
    private CategoriesLocalRepository mCategoriesRepository;

    // 북마크 원격 데이터 소스
    private BookmarksRemoteRepository mBookmarksRemoteRepository;

    // To avoid leaks, this must be an Application Context.
    private Context mContext;
    // 액티비티 상태저장 Shared Preferences
    private SharedPreferences spActStatus;

    /**
     * ViewModel 생성자
     * @param bookmarksLocalRepository - 북마크 로컬, 원격 데이터 액세스
     * @param categoriesRepository - 카테고리 로컬, 원격 데이터 액세스
     * @param context - 응용프로그램 context 를 강제로 사용함
     **/
    public EditAddItemPopupViewModel(BookmarksLocalRepository bookmarksLocalRepository,
                                     CategoriesLocalRepository categoriesRepository,
                                     BookmarksRemoteRepository bookmarksRemoteRepository,
                                     Context context
                                     , String type, String editItemId, String deleteItemType,
                                     SharedPreferences sharedPreferences) {
        mBookmarksLocalRepository = bookmarksLocalRepository;
        mCategoriesRepository = categoriesRepository;
        mBookmarksRemoteRepository = bookmarksRemoteRepository;
        mContext = context.getApplicationContext();
        spActStatus = sharedPreferences;
        // 게스트 유저
        // 로딩바 false
        mIsDataLoadingBar = false;
        // 뷰타입 구분자
        mViewType = type;
        // 편집할 아이템의 아이디 값
        mEditItemId = editItemId;
        // 삭제할 아이템의 타입
        mDeleteItemType = deleteItemType;
        //타이틀설정
        setViewType();
        // 편집인 경우 각 아이템의 필요한 데이터 셋팅
        setEditItemInfo();

    }

    // 현재 프래그먼트가 추가 작업인지 편집작업인지를 구분한다.
    @Bindable
    public boolean isAddItem() { return mIsAddItem; }

    // 현재 프래그 먼트가 삭제작업인지 아닌지를 구분한다.
    @Bindable
    public boolean isDeleteItem() { return mIsDeleteItem; }

    // 데이터 로딩중인지 아닌지를 구분한다.
    @Bindable
    public boolean isDataLoadingBar() { return mIsDataLoadingBar; }

    // 타이틀 제목 상황에맞게 셋팅 ( 추가, 카테고리편집, 북마크편집 )
    private void setViewType(){
        switch (mViewType) {
            case EditAddItemPopupActivity.ADD_ITEM :
                if(categories.size()==0){
                    isSelectBookmark.set(false);
                }else{
                    isSelectBookmark.set(true);
                }
                mIsAddItem = true;
                mIsDeleteItem = false;
                viewTitle.set("아이템 추가");
                break;
            case EditAddItemPopupActivity.EDIT_CATEGORY :
                isSelectBookmark.set(false);
                mIsAddItem = false;
                mIsDeleteItem = false;
                viewTitle.set("카테고리 편집");
                break;
            case EditAddItemPopupActivity.EDIT_BOOKMARK :
                isSelectBookmark.set(true);
                mIsAddItem = false;
                mIsDeleteItem = false;
                viewTitle.set("북마크 편집");
                break;
            case EditAddItemPopupActivity.DELETE_ITEM :
                isSelectBookmark.set(false);
                mIsAddItem = false;
                mIsDeleteItem = true;
                // 카테고리
                if(mDeleteItemType.equals(EditAddItemPopupActivity.EDIT_CATEGORY)){
                    viewTitle.set("카테고리 삭제");
                }
                // 북마크
                if(mDeleteItemType.equals(EditAddItemPopupActivity.EDIT_BOOKMARK)){
                    viewTitle.set("북마크 삭제");
                }
                break;
        }
    }

    // 편집시 카테고리 혹은 북마크 객체 생성하여 상황에맞게 셋팅
    private void setEditItemInfo(){
        // 카테고리 편집 or 카테고리 삭제
        if(mViewType.equals(EditAddItemPopupActivity.EDIT_CATEGORY) ||
        mViewType.equals(EditAddItemPopupActivity.DELETE_ITEM)
                && mDeleteItemType.equals(EditAddItemPopupActivity.EDIT_CATEGORY)){
            if(!spActStatus.getBoolean("login_status",false)){
                // 게스트 유저
                setEditCategoryLocalData();
            }else{
                // 회원 유저
                //getEditRemoteCategory();
            }
        }
        // 북마크 편집 or 북마크 삭제
        else if (mViewType.equals(EditAddItemPopupActivity.EDIT_BOOKMARK) ||
                mViewType.equals(EditAddItemPopupActivity.DELETE_ITEM)
                        && mDeleteItemType.equals(EditAddItemPopupActivity.EDIT_BOOKMARK)){
            if(!spActStatus.getBoolean("login_status",false)){
                // 게스트 유저
                setEditBookmarkLocalData();
            }else{
                // 회원 유저
                //getEditRemoteBookmark();
            }
        }
    }

    // 아이템이 생성 or 편집 or 삭제 완료되어 팝업 액티비티 종료
    private void navigationAddNewItem(){
        if(mNavigator!=null){
            mNavigator.updateItem();
        }
    }

    // 취소버튼 클릭
    public void cancelButtonOnClick(){
        // 뒤로가기
        if(mNavigator!=null){
            mNavigator.cancelAddItem();
        }
    }

    // 확인버튼 클릭
    public void okButtonOnClick(){
        // 로딩바 활성화
        mIsDataLoadingBar = true;
        notifyChange(); // For the @Bindable properties
        switch (mViewType) {

            // 아이템 추가
            case EditAddItemPopupActivity.ADD_ITEM :
                createItem();
                break;

            // 카테고리 업데이트
            case EditAddItemPopupActivity.EDIT_CATEGORY :
                createCategory();
                break;

            // 북마크 업데이트
            case EditAddItemPopupActivity.EDIT_BOOKMARK :
                createBookmark();
                break;

            // 아이템 삭제
            case EditAddItemPopupActivity.DELETE_ITEM :
                deleteItem();
                break;
        }
    }

    /**
     * editTextNullCheck
     * - 북마크 타이틀, 북마크 url, 카테고리 타이틀 입력값 null 체크
     * @param inputText - 각각 입력창에 입력된 text
     * @param etType - 타입 구분
     **/
    private boolean editTextNullCheck(String inputText, String etType){

        // edit text null 체크
        if(Objects.equals(inputText, "")){
            Toast.makeText(mContext, etType+" 입력창이 비워져있습니다.", Toast.LENGTH_SHORT).show();
            // 비어있는 값 저장안함
            // 로딩바 비활성화
            mIsDataLoadingBar = false;
            return true;
        }

        return false;
    }

    /**
     * categoryOverlapCheck()
     * - 카테고리의 중복을 체크한다.
     **/
    private boolean categoryOverlapCheck(){
        for(String title : categories){
            if(Objects.equals(categoryTitle.get(), title)){
                Toast.makeText(mContext, "중복된 카테고리", Toast.LENGTH_SHORT).show();
                // 중복되면 저장안함
                // 로딩바 비활성화
                mIsDataLoadingBar = false;
                notifyChange(); // For the @Bindable properties
                return true;
            }
        }
        return false;
    }

    /**
     *  createItem()
     *  - 아이템 추가 작업
     *  - 카테고리와 북마크를 구분하여 실행한다.
     **/
    private void createItem(){
        if(isSelectBookmark.get()){
            //북마크 생성가능 확인
            createBookmark();
        }else{
            //카테고리 생성
            createCategory();
        }
    }

    /**
     * checkBookmark()
     * - 북마크 생성 가능 확인
     * 1. 북마크 입력 null 체크
     * 2. url 입력 null 체크
     * 3. url 유효성 체크
     * 4. htmlParser 생성하여 jsoup 으로 url 응답확인
     **/
    private void createBookmark(){

        // 북마크 edit text null 체크 확인
        if(editTextNullCheck(bookmarkTitle.get(), "북마크")){ return; }
        // URL edit text null 체크 확인
        if(editTextNullCheck(bookmarkUrl.get(), "URL")){return;}

        // 편집하는 상황
        if(!mIsAddItem){
            // 아무것도 변경되지 않았다면
            if(mUpdateBookmark.getUrl().equals(bookmarkUrl.get()) &&
                    mUpdateBookmark.getTitle().equals(bookmarkTitle.get()) &&
                    mUpdateBookmark.getCategory().equals(bookmarkCategory.get())){
                Toast.makeText(mContext, "변경된 부분이 없습니다.", Toast.LENGTH_SHORT).show();
                mIsDataLoadingBar = false;
                notifyChange(); // For the @Bindable properties
                return;
            }
            // 타이틀이나 카테고리만 변경했을 때
            if(!mUpdateBookmark.getTitle().equals(bookmarkTitle.get()) ||
                    !mUpdateBookmark.getCategory().equals(bookmarkCategory.get())){
                // url 검사 진행하지않고 바로 저장
                saveLocalBookmark(mUpdateBookmark.getFaviconUrl());
                mIsDataLoadingBar = false;
                notifyChange(); // For the @Bindable properties
                return;
            }
        }

        // 1. 입력한 url 의 형식을 검사한다.
        String regex = "^((http|https)://){1}([a-zA-Z0-9]+[.]{1})?([a-zA-Z0-9]+){1}[.]{1}[a-z]+([/]{1}[a-zA-Z0-9]*)*";
        boolean match = Objects.requireNonNull(bookmarkUrl.get()).matches(regex);
        //Log.e("regex1", match+"");

        if(match){
            // 2. 검사를 통과한 url 주소를 http , 도메인, 경로를 각각 추출한다.
            Pattern urlPattern = Pattern.compile("^(https?):\\/\\/([^:\\/\\s]+)((\\/[^\\s\\/]+)*)");
            Matcher mc = urlPattern.matcher(bookmarkUrl.get());
            String faviconUrl;
            if(mc.matches()){
                // http 와 도메인에 favicon.ico 를 입력하여 url 완성
                faviconUrl = mc.group(1) + "://" + mc.group(2)+ "/favicon.ico";
                saveLocalBookmark(faviconUrl);
            }
        }else{
            Toast.makeText(mContext, "URL 주소를 확인해주세요.", Toast.LENGTH_SHORT).show();
            mIsDataLoadingBar = false;
            notifyChange(); // For the @Bindable properties
        }

    }


    // 카테고리 생성 저장
    /**
     * createCategory()
     * - 카테고리 생성하고 저장한다.
     * 1. 카테고리 입력 null 체크
     * 2. 카테고리 중복체크
     * 3. 추가 or 업데이트
     **/
    private void createCategory(){

        // 카테고리 edit text null 체크 확인
        if(editTextNullCheck(categoryTitle.get(), "카테고리")){ return; }

        // 카테고리 중복체크
        if(categoryOverlapCheck()){ return; }

        //새로 추가하는경우
        if(mIsAddItem){
            // 중복 아니면 저장진행
            Category newCategory = new Category(
                    Objects.requireNonNull(categoryTitle.get()),
                    categories.size(),
                    false);
            if(!spActStatus.getBoolean("login_status",false)){
                // 게스트 유저
                mCategoriesRepository.saveCategory(newCategory);
            }else{
                mBookmarksRemoteRepository.saveCategory(newCategory);
            }
            Toast.makeText(mContext, categoryTitle.get()+" 카테고리 생성", Toast.LENGTH_SHORT).show();
            navigationAddNewItem();
        }else{
            // 업데이트 하는 경우
            Category updateCategory = new Category(mUpdateCategory.getId(),
                    Objects.requireNonNull(categoryTitle.get()),
                    mUpdateCategory.getPosition(),
                    mUpdateCategory.isSelected());

            // 카테고리에 포함된 북마크 카테고리명 변경
            for(Bookmark bookmark : mUpdateBookmarks){
                Bookmark updateBookmark = new Bookmark(bookmark.getId(),
                        bookmark.getTitle(),
                        bookmark.getUrl(),
                        bookmark.getAction(),
                        Objects.requireNonNull(categoryTitle.get()),
                        bookmark.getPosition(),
                        bookmark.getFaviconUrl());
                mBookmarksLocalRepository.saveBookmark(updateBookmark);
            }
            // 카테고리 업데이트
            mCategoriesRepository.saveCategory(updateCategory);
            Toast.makeText(mContext, categoryTitle.get()+" 카테고리명 업데이트", Toast.LENGTH_SHORT).show();
            navigationAddNewItem();
        }
    }

    // 삭제
    private void deleteItem(){
        // 카테고리 삭제
        if(mDeleteItemType.equals(EditAddItemPopupActivity.EDIT_CATEGORY)){
            // 해당 카테고리에 북마크가 존재하면
            if(mUpdateBookmarks.size() != 0){
                // 카테고리 안에있는 모든 북마크도 삭제
                mBookmarksLocalRepository.deleteAllInCategory(mUpdateCategory.getTitle());
            }
            mCategoriesRepository.deleteCategory(mUpdateCategory.getId());
            Toast.makeText(mContext, mUpdateCategory.getTitle()+" 카테고리 삭제", Toast.LENGTH_SHORT).show();
            navigationAddNewItem();
        }
        // 북마크 삭제
        if(mDeleteItemType.equals(EditAddItemPopupActivity.EDIT_BOOKMARK)){
            mBookmarksLocalRepository.deleteBookmark(mUpdateBookmark.getId());
            Toast.makeText(mContext, mUpdateBookmark.getTitle()+" 북마크 삭제", Toast.LENGTH_SHORT).show();
            navigationAddNewItem();
        }
    }

    /**
     * 원격데이터베이스--------------------------------------------------
     **/

    private void createRemoteItem(){
        if(isSelectBookmark.get()){
            //북마크 생성

        }else{
            //카테고리 생성

        }
    }

    /**
     * 로컬데이터베이스 -------------------------------------------------
     **/
    // 편집할 카테고리의 로컬 데이터 셋팅
    private void setEditCategoryLocalData(){
        // -> 생성시 전달받은 id 로 카테고리 객체 가져오기
        mCategoriesRepository.getCategory(mEditItemId,
                new CategoriesLocalDataSource.GetCategoryCallback() {
                    @Override
                    public void onCategoryLoaded(Category category) {
                        categoryTitle.set(category.getTitle());
                        mUpdateCategory = category;
                        notifyPropertyChanged(BR._all);
                    }
                    @Override
                    public void onDataNotAvailable() {
                        // 데이터 가져오는데 실패
                    }
                });

        // -> 변경할 카테고리의 북마크 리스트 가져오기
        mBookmarksLocalRepository.getBookmarks(Objects.requireNonNull(categoryTitle.get()),
                new BookmarksLocalDataSource.LoadBookmarksCallback() {
                    @Override
                    public void onBookmarksLoaded(List<Bookmark> bookmarks) {
                        mUpdateBookmarks.addAll(bookmarks);
                    }

                    @Override
                    public void onDataNotAvailable() {
                    }
                });
    }
    // 편집할 북마크의 로컬 데이터 셋팅
    private void setEditBookmarkLocalData(){
        // -> 생성시 전달받은 id 로 북마크 가져오기
        mBookmarksLocalRepository.getBookmark(mEditItemId,
                new BookmarksLocalDataSource.GetBookmarkCallback() {
                    @Override
                    public void onBookmarkLoaded(Bookmark bookmark) {
                        bookmarkTitle.set(bookmark.getTitle());
                        bookmarkUrl.set(bookmark.getUrl());
                        // 업데이트할 북마크
                        mUpdateBookmark = bookmark;
                        notifyPropertyChanged(BR._all);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        // 실패
                    }
                });
    }

    /**
     * saveBookmark
     *  - setFaviconUrlAndSaveBookmark 에서 파비콘 url 생성후 입력받는다.
     * - 북마크 객체를 업데이트 혹은 새로저장한다.
     * @param faviconUrl - 사이트 파비콘 이미지 url
     **/
    private void saveLocalBookmark(String faviconUrl){
        mBookmarksLocalRepository.getBookmarks(Objects.requireNonNull(bookmarkCategory.get()),
                new BookmarksLocalDataSource.LoadBookmarksCallback() {
                    @Override
                    public void onBookmarksLoaded(List<Bookmark> bookmarks) {
                        // 1. 아이템 추가
                        if(mIsAddItem){
                            // 1-1 해당 카테고리에 북마크가 있는경우
                            // position 값을 카테고리안의 북마크 사이즈 크기로 지정
                            // -> 해당 카테고리 전체 사이즈, 파비콘 url
                            addLocalBookmark(bookmarks.size(),faviconUrl);
                        }else{
                            // 2. 아이템 편집
                            if(mUpdateBookmark.getCategory().equals(bookmarkCategory.get())){
                                // 2-1. 변경하지 않은 경우 원래 포지션그대로 입력
                                updateLocalBookmark(false, faviconUrl,
                                        mUpdateBookmark.getPosition());
                            }else{
                                //  2-2. 카테고리 편집시 변경 한 경우 해당 카테고리에 북마크가 존재할때
                                // 이전카테고리 + 이동할 카테고리 포지션값 재배열
                                updateLocalBookmarksInCategory(bookmarks,mUpdateBookmark.getCategory(),faviconUrl);
                            }
                        }
                    }

                    @Override
                    public void onDataNotAvailable() {
                        // 1-2. 카테고리 추가시 해당 카테고리에 북마크가 없는경우
                        if(mIsAddItem){
                            // 해당 카테고리에 북마크가 없다면 position 0 으로 추가한다.
                            addLocalBookmark(0,faviconUrl);
                        } else {
                            // 2-3. 카테고리 편집시 해당 카테고리에 북마크가 없다면  0으로 추가
                            // 이전 카테고리 포지션값 재배열
                            updateLocalBookmarksInCategory(null,mUpdateBookmark.getCategory(),faviconUrl);
                        }
                    }
                });
        Toast.makeText(mContext, bookmarkCategory.get()+"에 저장", Toast.LENGTH_SHORT).show();
        navigationAddNewItem();
    }

    /**
     * updateBookmarksInCategory
     * - 북마크 카테고리 변경 시 이전 카테고리와 이동할 카테고리의 포지션값을 재 정비한다.
     * @param bookmarks - 이동 할 카테고리
     * @param previousCategory - 이동하기 전 카테고리명
     * @param favicon - favicon url
     **/
    private void updateLocalBookmarksInCategory(List<Bookmark> bookmarks, String previousCategory,String favicon){

        // 이동할 곳에 북마크가 존재한다면
        if(bookmarks != null){
            int count = 0;
            for(Bookmark bookmark : bookmarks){
                //Log.e("이동",bookmark.getTitle()+ count);
                mBookmarksLocalRepository.updatePosition(bookmark,count);
                count++;
            }
            updateLocalBookmark(true, favicon, bookmarks.size());
        }else{
            // 이동하는 곳에 북마크가 존재하지 않으면
            updateLocalBookmark(true, favicon,0);
        }

        // 이전 카테고리 검사
        mBookmarksLocalRepository.getBookmarks(previousCategory,
                new BookmarksLocalDataSource.LoadBookmarksCallback() {
                    @Override
                    public void onBookmarksLoaded(List<Bookmark> bookmarks) {
                        // 이전카테고리 업데이트
                        int count = 0;
                        for(Bookmark bookmark : bookmarks){
                            mBookmarksLocalRepository.updatePosition(bookmark,count);
                            //Log.e("이전",bookmark.getTitle()+count);
                            count++;
                        }
                    }

                    @Override
                    public void onDataNotAvailable() {
                        // 없음
                        // 이전카테고리 업데이트 필요없음
                        Log.e("test","이전카테고리 업데이트 필요없음 ");
                    }
                });

    }


    /**
     * 북마크 업데이트
     * - 북마크 편집시 사용하는 메소드
     * @param changeCategory - 카테고리가 변경된 북마크인지 여부
     * @param faviconUrl - 파비콘 url
     * @param position - 카테고리 상의 position 값
     **/
    private void updateLocalBookmark(boolean changeCategory, String faviconUrl, int position){

        if(changeCategory){
            // 카테고리 변경하는 경우
            Bookmark updateBookmark = new Bookmark(mUpdateBookmark.getId(),
                    Objects.requireNonNull(bookmarkTitle.get()),
                    Objects.requireNonNull(bookmarkUrl.get()),
                    mUpdateBookmark.getAction(),
                    Objects.requireNonNull(bookmarkCategory.get()),
                    position,faviconUrl);
            mBookmarksLocalRepository.saveBookmark(updateBookmark);
        }else{
            // 카테고리 변경하지 않은 경우
            Bookmark updateBookmark = new Bookmark(mUpdateBookmark.getId(),
                    Objects.requireNonNull(bookmarkTitle.get()),
                    Objects.requireNonNull(bookmarkUrl.get()),
                    mUpdateBookmark.getAction(),
                    mUpdateBookmark.getCategory(),
                    position,faviconUrl);
            mBookmarksLocalRepository.saveBookmark(updateBookmark);
        }

    }

    /**
     * 북마크생성
     * - 북마크 최초 생성 시 사용하는 메소드
     * @param position - 카테고리 상의 position 값
     * @param faviconUrl - 파비콘 url
     **/
    private void addLocalBookmark(int position, String faviconUrl){
        Bookmark bookmark = new Bookmark(
                Objects.requireNonNull(bookmarkTitle.get()),
                Objects.requireNonNull(bookmarkUrl.get()),
                "WEB_VIEW",
                Objects.requireNonNull(bookmarkCategory.get()),
                position,
                faviconUrl);
        mBookmarksLocalRepository.saveBookmark(bookmark);
    }

}
