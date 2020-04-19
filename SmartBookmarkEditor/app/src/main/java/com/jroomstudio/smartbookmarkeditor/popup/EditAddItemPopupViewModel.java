package com.jroomstudio.smartbookmarkeditor.popup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
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
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksDataSource;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksRepository;
import com.jroomstudio.smartbookmarkeditor.data.category.Category;
import com.jroomstudio.smartbookmarkeditor.data.category.source.CategoriesDataSource;
import com.jroomstudio.smartbookmarkeditor.data.category.source.CategoriesRepository;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    // 카테고리 or 북마크
    public final ObservableBoolean isSelectBookmark = new ObservableBoolean();
    // 편집과 추가 를 구분한다.
    public final ObservableBoolean isAddItem = new ObservableBoolean();

    // 카테고리 리스트
    public final ObservableList<String> categories = new ObservableArrayList<>();

    // 뷰형식 구분
    // 추가 or 편집(카테고리) or 편집(북마크)
    public final ObservableField<String> viewTitle = new ObservableField<>();
    private String mViewType;

    // 편집할 아이템의 아이디
    private final ObservableField<String> itemId = new ObservableField<>();
    private Category mUpdateCategory;
    private List<Bookmark> mUpdateBookmarks = new ArrayList<Bookmark>();
    private Bookmark mUpdateBookmark;

    // 데이터 로딩바 표시
    private boolean mIsDataLoadingBar;

    // 액티비티 네비게이터
    private EditAddItemPopupNavigator mNavigator;
    // 액티비티 시작시 네비게이터 셋팅
    void onActivityCreated(EditAddItemPopupNavigator navigator){ mNavigator = navigator; }
    // 액티비티 종료시 네비게이터 종료
    void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mNavigator = null;
    }

    // 북마크 데이터 소스
    private BookmarksRepository mBookmarksRepository;
    // 카테고리 데이터 소스
    private CategoriesRepository mCategoriesRepository;
    // To avoid leaks, this must be an Application Context.
    private Context mContext;


    /**
     * ViewModel 생성자
     * @param bookmarksRepository - 북마크 로컬, 원격 데이터 액세스
     * @param categoriesRepository - 카테고리 로컬, 원격 데이터 액세스
     * @param context - 응용프로그램 context 를 강제로 사용함
     **/
    public EditAddItemPopupViewModel(BookmarksRepository bookmarksRepository,
                                     CategoriesRepository categoriesRepository, Context context
                                     ,String type, String id) {
        mBookmarksRepository = bookmarksRepository;
        mCategoriesRepository = categoriesRepository;
        mContext = context.getApplicationContext();
        mIsDataLoadingBar = false;
        // 뷰타입 구분자
        mViewType = type;
        // 편집할 아이템의 아이디 값
        itemId.set(id);
        //타이틀설정
        setViewType();
        // 편집인 경우 각 아이템의 필요한 데이터 셋팅
        setEditItemInfo();
    }

    // 현재 프래그먼트가 추가 작업인지 편집작업인지를 구분한다.
    @Bindable
    public boolean isAddItem() { return isAddItem.get(); }

    // 데이터 로딩중인지 아닌지를 구분한다.
    @Bindable
    public boolean isDataLoadingBar() { return mIsDataLoadingBar; }

    // 타이틀 제목 상황에맞게 셋팅 ( 추가, 카테고리편집, 북마크편집 )
    private void setViewType(){
        switch (mViewType) {
            case EditAddItemPopupActivity.ADD_ITEM :
                isSelectBookmark.set(true);
                isAddItem.set(true);
                viewTitle.set("아이템 추가");
                break;
            case EditAddItemPopupActivity.EDIT_CATEGORY :
                isSelectBookmark.set(false);
                isAddItem.set(false);
                viewTitle.set("카테고리 편집");
                break;
            case EditAddItemPopupActivity.EDIT_BOOKMARK :
                isSelectBookmark.set(true);
                isAddItem.set(false);
                viewTitle.set("북마크 편집");
                break;
        }
    }

    // 편집시 카테고리 혹은 북마크 객체 생성하여 상황에맞게 셋팅
    private void setEditItemInfo(){

        switch (mViewType){

            // 1. 카테고리 편집 셋팅
            case EditAddItemPopupActivity.EDIT_CATEGORY :
                // -> 생성시 전달받은 id 로 카테고리 객체 가져오기
                mCategoriesRepository.getCategory(Objects.requireNonNull(itemId.get()),
                        new CategoriesDataSource.GetCategoryCallback() {
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
                mBookmarksRepository.getBookmarks(Objects.requireNonNull(categoryTitle.get()),
                        new BookmarksDataSource.LoadBookmarksCallback() {
                            @Override
                            public void onBookmarksLoaded(List<Bookmark> bookmarks) {
                                mUpdateBookmarks.addAll(bookmarks);
                            }

                            @Override
                            public void onDataNotAvailable() {
                            }
                        });
                break;

            // 2. 북마크 편집 셋팅
            case EditAddItemPopupActivity.EDIT_BOOKMARK :
                // -> 생성시 전달받은 id 로 북마크 가져오기
                mBookmarksRepository.getBookmark(Objects.requireNonNull(itemId.get()),
                        new BookmarksDataSource.GetBookmarkCallback() {
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
                break;
        }

    }

    // 아이템이 생성 or 편집 완료되었으니 팝업 액티비티 종료
    private void navigationAddNewItem(){
        if(mNavigator!=null){
            mNavigator.updateItem();
        }
    }

    // 액티비티의 취소버튼 클릭
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
                updateCategory();
                break;
                // 북마크 업데이트
            case EditAddItemPopupActivity.EDIT_BOOKMARK :
                checkBookmark();
                break;
        }
    }

    /**
     *  createItem()
     *  - 아이템 추가 작업
     *  - 카테고리와 북마크를 구분하여 실행한다.
     **/
    private void createItem(){
        if(isSelectBookmark.get()){
            //북마크 생성가능 확인
            checkBookmark();
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
    private void checkBookmark(){

        // 정규표현식 구현
        /*
        String regex = "^((http|https)://)?(www.)?([a-zA-Z0-9]+)\\\\.[a-z]+([a-zA-z0-9.?#]+)?";
        Pattern p = Pattern.compile(regex);
        Log.e("regex test", p.matcher(bookmarkUrl.get()).matches() +"");
        */

        // 북마크 edit text null 체크 확인
        if(editTextNullCheck(bookmarkTitle.get(), "북마크")){ return; }
        // URL edit text null 체크 확인
        if(editTextNullCheck(bookmarkUrl.get(), "URL")){return;}

        // 편집하는 상황
        if(!isAddItem.get()){
            // 아무것도 변경되지 않았다면
            if(mUpdateBookmark.getUrl().equals(bookmarkUrl.get()) &&
                    mUpdateBookmark.getTitle().equals(bookmarkTitle.get()) &&
                    mUpdateBookmark.getCategory().equals(bookmarkCategory.get())){
                Toast.makeText(mContext, "변경된 부분이 없습니다.", Toast.LENGTH_SHORT).show();
                mIsDataLoadingBar = false;
                notifyChange(); // For the @Bindable properties
                return;
            }
        }

        // url 유효성 체크
        if(urlValidationCheck(Objects.requireNonNull(bookmarkUrl.get()))){
            // 파비콘 url 생성후 저장
            setFaviconUrlAndSaveBookmark(true,bookmarkUrl.get());
            /*
            // 인터넷 연결확인
            if(NetworkStatus.getConnectivityStatus(mContext)){
                // -> 연결이 되어있으면
                // Jsoup 으로 입력한 url 응답 확인
                HtmlParser htmlParser = new HtmlParser();
                htmlParser.execute();
            }else{
                // -> 연결이 안되어있으면
                // 로딩바 비활성화
                mIsDataLoadingBar = false;
                notifyChange(); // For the @Bindable properties
                Toast.makeText(mContext, "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
            */
        }
    }


    // 카테고리 생성 저장
    /**
     * createCategory()
     * - 카테고리 생성하고 저장한다.
     * 1. 카테고리 입력 null 체크
     * 2. 카테고리 중복체크
     * 3. 저장
     **/
    private void createCategory(){

        // 카테고리 edit text null 체크 확인
        if(editTextNullCheck(categoryTitle.get(), "카테고리")){ return; }

        // 카테고리 중복체크
        if(categoryOverlapCheck()){ return; }

        // 중복 아니면 저장진행
        Category newCategory = new Category(
                Objects.requireNonNull(categoryTitle.get()),
                categories.size(),
                false);
        mCategoriesRepository.saveCategory(newCategory);
        Toast.makeText(mContext, categoryTitle.get()+" 카테고리 생성", Toast.LENGTH_SHORT).show();
        navigationAddNewItem();
    }


    /**
     * updateCategory()
     * - 이미 생성된 카테고리명을 변경한다.
     * 1. 카테고리 입력 null 체크
     * 2. 카테고리 중복체크
     * 3. 카테고리에 포함된 북마크의 카테고리명 변경
     * 4. 카테고리 업데이트
     **/
    private void updateCategory(){

        // 카테고리 edit text null 체크 확인
        if(editTextNullCheck(categoryTitle.get(), "카테고리")){ return; }

        // 카테고리 중복체크
        if(categoryOverlapCheck()){ return; }

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
            mBookmarksRepository.saveBookmark(updateBookmark);
        }
        // 카테고리 업데이트
        mCategoriesRepository.saveCategory(updateCategory);
        Toast.makeText(mContext, categoryTitle.get()+" 카테고리명 업데이트", Toast.LENGTH_SHORT).show();
        navigationAddNewItem();

    }


    /**
     * urlValidationCheck
     * - 응답보내기 전 입력된 url 의 입력 형식을 체크한다.
     * - 북마크 추가, 편집시 호출된다.
     * @param url - 입력된 url 주소
     **/
    private boolean urlValidationCheck(String url){
        // 1. http 로 시작하는가
        if(url.startsWith("http")){
            //https:// 혹은 http:// 가 중복 확인
            if(url.contains("https://") || url.contains("http://")){
                StringBuilder sb = new StringBuilder();
                sb.append(url);
                String temp;
                if(url.contains("https://")){
                    // https 형식 중복확인
                    sb.delete(0,8);
                    temp = sb.toString();
                    if(!temp.contains("https://") && !temp.contains("http://")){
                        return true;
                    }else{
                        // 로딩바 비활성화
                        mIsDataLoadingBar = false;
                        notifyChange(); // For the @Bindable properties
                        Toast.makeText(mContext,"잘못된 url 형식입니다.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else if(url.contains("http://")){
                    // http 형식 중복확인
                    sb.delete(0,7);
                    temp = sb.toString();
                    if(!temp.contains("https://") && !temp.contains("http://")){
                        return true;
                    }else{
                        // 로딩바 비활성화
                        mIsDataLoadingBar = false;
                        notifyChange(); // For the @Bindable properties
                        Toast.makeText(mContext,"잘못된 url 형식입니다.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                return true;
            }else{
                // 로딩바 비활성화
                mIsDataLoadingBar = false;
                notifyChange(); // For the @Bindable properties
                Toast.makeText(mContext,"잘못된 url 형식입니다.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            // 로딩바 비활성화
            mIsDataLoadingBar = false;
            notifyChange(); // For the @Bindable properties
            Toast.makeText(mContext,"잘못된 url 형식입니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * setFaviconUrlAndSaveBookmark
     * - 입력형식 체크 완료후 htmlParse 로 응답 요청을 보낸 후 결과를 받아 작업한다.
     * - htmlParse 객체의 onPostExecute() 에서 호출된다.
     * 1. 성공한 경우 전달받은 baseUri 를 사용하여 favicon url 을 만들어 저장한다.
     * 2. 북마크 추가, 북마크 편집( 카테고리 변동x ), 북마크 편집( 카테고리 변동 o) 을 구분하여 저장한다.
     * @param urlCheck - htmlParse 객체를 통해 전달받은 응답 성공 실패 여부
     * @param baseUri - 응답 성공 시 전달받은 base uri
     **/
    private void setFaviconUrlAndSaveBookmark(boolean urlCheck, String baseUri){

        // 응답에 성공한 url 일경우만 진행
        if(urlCheck){
            //favicon url 설정
            StringBuilder sb = new StringBuilder();
            int count=0;
            // uri for 문으로 검사하여 기본 uri 만 추출하기
            for(int i=0; i<baseUri.length(); i++){
                if(baseUri.charAt(i)=='/'){
                    count++;
                    if(count==3){
                        break;
                    }
                }
                sb.append(baseUri.charAt(i));
            }
            // 파비콘 url 추출
            sb.append("/favicon.ico");
            // 북마크 저장
            saveBookmark(sb.toString());
        }
        else{
            // 응답실패
            // 로딩바 비활성화
            mIsDataLoadingBar = false;
            notifyChange(); // For the @Bindable properties
            Toast.makeText(mContext, "유효하지 않은 url 입니다.", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * saveBookmark
     *  - setFaviconUrlAndSaveBookmark 에서 파비콘 url 생성후 입력받는다.
     * - 북마크 객체를 업데이트 혹은 새로저장한다.
     * @param faviconUrl - 사이트 파비콘 이미지 url
     **/
    private void saveBookmark(String faviconUrl){
        mBookmarksRepository.refreshBookmarks();
        mBookmarksRepository.getBookmarks(Objects.requireNonNull(bookmarkCategory.get()),
                new BookmarksDataSource.LoadBookmarksCallback() {
                    @Override
                    public void onBookmarksLoaded(List<Bookmark> bookmarks) {
                        // 1. 아이템 추가
                        if(isAddItem.get()){
                            // 1-1 해당 카테고리에 북마크가 있는경우
                            // position 값을 카테고리안의 북마크 사이즈 크기로 지정
                            // -> 해당 카테고리 전체 사이즈, 파비콘 url
                            addBookmark(bookmarks.size(),faviconUrl);
                        }else{
                            // 2. 아이템 편집
                            if(mUpdateBookmark.getCategory().equals(bookmarkCategory.get())){
                                // 2-1. 변경하지 않은 경우 원래 포지션그대로 입력
                                updateBookmark(false, faviconUrl,
                                        mUpdateBookmark.getPosition());
                            }else{
                                //  2-2. 카테고리 편집시 변경 한 경우 해당 카테고리에 북마크가 존재할때
                                // 이전카테고리 + 이동할 카테고리 포지션값 재배열
                                updateBookmarksInCategory(bookmarks,mUpdateBookmark.getCategory(),faviconUrl);
                            }
                        }
                    }

                    @Override
                    public void onDataNotAvailable() {
                        // 1-2. 카테고리 추가시 해당 카테고리에 북마크가 없는경우
                        if(isAddItem.get()){
                            // 해당 카테고리에 북마크가 없다면 position 0 으로 추가한다.
                            addBookmark(0,faviconUrl);
                        } else {
                            // 2-3. 카테고리 편집시 해당 카테고리에 북마크가 없다면  0으로 추가
                            // 이전 카테고리 포지션값 재배열
                            updateBookmarksInCategory(null,mUpdateBookmark.getCategory(),faviconUrl);
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
    private void updateBookmarksInCategory(List<Bookmark> bookmarks, String previousCategory,String favicon){

        // 이동할 곳에 북마크가 존재한다면
        if(bookmarks != null){
            int count = 0;
            for(Bookmark bookmark : bookmarks){
                //Log.e("이동",bookmark.getTitle()+ count);
                mBookmarksRepository.updatePosition(bookmark,count);
                count++;
            }
            updateBookmark(true, favicon, bookmarks.size());
        }else{
            // 이동하는 곳에 북마크가 존재하지 않으면
            updateBookmark(true, favicon,0);
        }

        // 이전 카테고리 검사
        mBookmarksRepository.refreshBookmarks();
        mBookmarksRepository.getBookmarks(previousCategory,
                new BookmarksDataSource.LoadBookmarksCallback() {
                    @Override
                    public void onBookmarksLoaded(List<Bookmark> bookmarks) {
                        // 이전카테고리 업데이트
                        int count = 0;
                        for(Bookmark bookmark : bookmarks){
                            mBookmarksRepository.updatePosition(bookmark,count);
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
     * 북마크생성
     * - 북마크 최초 생성 시 사용하는 메소드
     * @param position - 카테고리 상의 position 값
     * @param faviconUrl - 파비콘 url
     **/
    private void addBookmark(int position, String faviconUrl){
        Bookmark bookmark = new Bookmark(
                Objects.requireNonNull(bookmarkTitle.get()),
                Objects.requireNonNull(bookmarkUrl.get()),
                "WEB_VIEW",
                Objects.requireNonNull(bookmarkCategory.get()),
                position,
                faviconUrl);
        mBookmarksRepository.saveBookmark(bookmark);
    }

    /**
     * 북마크 업데이트
     * - 북마크 편집시 사용하는 메소드
     * @param changeCategory - 카테고리가 변경된 북마크인지 여부
     * @param faviconUrl - 파비콘 url
     * @param position - 카테고리 상의 position 값
     **/
    private void updateBookmark(boolean changeCategory, String faviconUrl, int position){

        if(changeCategory){
            // 카테고리 변경하는 경우
            Bookmark updateBookmark = new Bookmark(mUpdateBookmark.getId(),
                    Objects.requireNonNull(bookmarkTitle.get()),
                    Objects.requireNonNull(bookmarkUrl.get()),
                    mUpdateBookmark.getAction(),
                    Objects.requireNonNull(bookmarkCategory.get()),
                    position,faviconUrl);
            mBookmarksRepository.saveBookmark(updateBookmark);
        }else{
            // 카테고리 변경하지 않은 경우
            Bookmark updateBookmark = new Bookmark(mUpdateBookmark.getId(),
                    Objects.requireNonNull(bookmarkTitle.get()),
                    Objects.requireNonNull(bookmarkUrl.get()),
                    mUpdateBookmark.getAction(),
                    mUpdateBookmark.getCategory(),
                    position,faviconUrl);
            mBookmarksRepository.saveBookmark(updateBookmark);
        }

    }


    /**
     * Jsoup 을 활용해 url 유효성을 확인하는 AsyncTask 객체
     * 1. 존재하는 사이트인가?
     * boolean urlCheck
     * -> 응답이 있는 사이트인가 없는 사이트인가
     *
     * 2. 유효한 url 의 경우 baseUri 를 전달받아 확인한다.
     * String baseUri
     *
     * onPostExecute()
     * - doInBackground 작업이 끝난 후 실행된다.
     * - setFaviconUrlAndSaveBookmark()
     *   -> urlCheck 와 baseUri 를 입력하여 결과를 알려준다.
     **/
    @SuppressLint("StaticFieldLeak")
    private class HtmlParser extends AsyncTask<Void,Void,Void> {
        String baseUri = "";
        boolean urlCheck = false;



        @Override
        protected Void doInBackground(Void... voids) {
            // url 을 입력받아 파싱을 진행한다.
            // 1. url 의 유효성을 확인한다.

            try {
                // timeout 체크
                Connection.Response response = Jsoup.connect(bookmarkUrl.get())
                        .timeout(2000)
                        .execute();
                // Log.e("latency",response.statusMessage());
                int statusCode = response.statusCode();
                // 제한시간 안에 응답이 오면
                if(statusCode == 200){
                    Document doc = Jsoup.connect(bookmarkUrl.get())
                            .get();
                    baseUri = doc.baseUri();
                    urlCheck = true;
                }

            } catch (IOException e) {
                urlCheck = false;
                isCancelled();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // 북마크 파비콘 url 추출
            // 파싱 성공 or 실패를 전달
            setFaviconUrlAndSaveBookmark(urlCheck,baseUri);
        }
    }

}
