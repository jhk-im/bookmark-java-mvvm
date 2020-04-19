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
    final ObservableField<String> bookmarkCategory = new ObservableField<>();
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
        // 뷰타입 구분자
        mViewType = type;
        setViewType();
        // 편집할 아이템의 아이디 값
        itemId.set(id);

        // 편집인 경우
        if(!isAddItem.get()){
            setEditItemInfo();
        }

    }

    // 카테고리 스피너 리스트 추가를 위해서 카테고리 모두 가져오기


    // 현재 프래그먼트가 추가 작업인지 편집작업인지를 구분한다.
    @Bindable
    public boolean isAddItem() {
        return isAddItem.get();
    }

    // 타이틀 제목 상황에맞게 셋팅
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



    /**
     * 네비게이터 실행 메소드
     **/
    // 아이템이 생성 or 편집 완료되었으니 액티비티 종료
    private void navigationAddNewItem(){
        if(mNavigator!=null){
            mNavigator.updateItem();
        }
    }




    /**
     * 저장 , 취소
     **/

    // 액티비티의 취소버튼 클릭
    public void cancelButtonOnClick(){
        // 뒤로가기
        if(mNavigator!=null){
            mNavigator.cancelAddItem();
        }
    }

    // 확인버튼 클릭
    public void okButtonOnClick(){
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
                checkBookmark(false);
                break;
        }
    }


    // 카테고리 업데이트
    private void updateCategory(){
        // 카테고리 제목 null 체크
        if(Objects.equals(categoryTitle.get(), "")){
            Toast.makeText(mContext, "카테고리 제목을 입력하세요.", Toast.LENGTH_SHORT).show();
            // 비어있는 값 저장안함
            return;
        }

        // 카테고리 중복체크
        for(String title : categories){
            if(Objects.equals(categoryTitle.get(), title)){
                Toast.makeText(mContext, "중복된 카테고리", Toast.LENGTH_SHORT).show();
                // 중복되면 저장안함
                return;
            }
        }
        Category updateCategory = new Category(mUpdateCategory.getId(),
                Objects.requireNonNull(categoryTitle.get()),
                mUpdateCategory.getPosition(),
                mUpdateCategory.isSelected());

        // 카테고리에 포함된 북마크 카테고리정보 변경
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

    // 아이템 생성 (카테고리와 북마크 구분)
    private void createItem(){
        if(isSelectBookmark.get()){
            //북마크 생성
            checkBookmark(true);
        }else{
            //카테고리 생성
            createCategory();
        }
    }

    // 카테고리 생성 저장
    private void createCategory(){

        // 카테고리 제목 null 체크
        if(Objects.equals(categoryTitle.get(), "")){
            Toast.makeText(mContext, "카테고리 제목을 입력하세요.", Toast.LENGTH_SHORT).show();
            // 비어있는 값 저장안함
            return;
        }

        // 카테고리 중복체크
        for(String title : categories){
            if(Objects.equals(categoryTitle.get(), title)){
                Toast.makeText(mContext, "중복된 카테고리", Toast.LENGTH_SHORT).show();
                // 중복되면 저장안함
                return;
            }
        }

        // 중복 아니면 저장진행
        Category newCategory = new Category(
                Objects.requireNonNull(categoryTitle.get()),
                categories.size(),
                false);
        mCategoriesRepository.saveCategory(newCategory);
        Toast.makeText(mContext, categoryTitle.get()+" 카테고리 생성", Toast.LENGTH_SHORT).show();
        navigationAddNewItem();
    }

    // 북마크생성
    // isAdd - 편집인지 추가인지 구분하가 위함
    private void checkBookmark(boolean isAdd){

        // 1. 북마크 제목 null 체크
        if(Objects.equals(bookmarkTitle.get(), "")){
            Toast.makeText(mContext, "북마크 제목을 입력하세요.", Toast.LENGTH_SHORT).show();
            // 비어있는 값 저장안함
            return;
        }
        // 2. 유효성 체크
        if(urlValidation(Objects.requireNonNull(bookmarkUrl.get()))){
            // 유효성체크 통과
            // 3.Jsoup 으로 입력한 url 이 유효한지 확인
            HtmlParser htmlParser = new HtmlParser();
            htmlParser.execute();
            htmlParser.isAdd = isAdd;
        }else{
            // 유효성 문제발견 시
            Toast.makeText(mContext, "잘못된 url 형식입니다.", Toast.LENGTH_SHORT).show();
        }

    }

    // 4. 북마크 저장
    private void saveBookmark(boolean parse,String baseUri,boolean isAdd){
        if(parse){
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
            sb.append("/favicon.ico");

            // 추가하는상황
            if(isAdd){
                // 선택된 카테고리로 리스트 가져와서 리스트 제일 마지막에 추가
                mBookmarksRepository.getBookmarks(Objects.requireNonNull(bookmarkCategory.get()),
                        new BookmarksDataSource.LoadBookmarksCallback() {
                            @Override
                            public void onBookmarksLoaded(List<Bookmark> bookmarks) {
                                // position 값을 카테고리안의 북마크 사이즈 크기로 지정
                                // -> 가장 마지막에 추가
                                addBookmark(bookmarks.size(),sb.toString());
                            }

                            @Override
                            public void onDataNotAvailable() {
                                // 해당 카테고리에 북마크가 없다면 position 0 으로 추가한다.
                                addBookmark(0,sb.toString());
                            }
                        });
                Toast.makeText(mContext, bookmarkCategory.get()+"에 저장", Toast.LENGTH_SHORT).show();
                navigationAddNewItem();
            }else{
                // 편집하는 상황
                if(!mUpdateBookmark.getCategory().equals(bookmarkTitle.get())){
                    // 카테고리를 변경한 경우
                    // 선택된 카테고리로 리스트 가져와서 리스트 제일 마지막에 추가
                    mBookmarksRepository.getBookmarks(Objects.requireNonNull(bookmarkCategory.get()),
                            new BookmarksDataSource.LoadBookmarksCallback() {
                                @Override
                                public void onBookmarksLoaded(List<Bookmark> bookmarks) {
                                    // position 값을 카테고리안의 북마크 사이즈 크기로 지정
                                    // -> 가장 마지막에 추가
                                    updateBookmark(true, sb.toString(),bookmarks.size());
                                }

                                @Override
                                public void onDataNotAvailable() {
                                    // 변경하지 않은 경우 -> 새로운 카테고리에 아이템이 없을경우
                                    updateBookmark(true, sb.toString(),0);
                                }
                            });
                }else{
                    // 변경하지 않은 경우
                    updateBookmark(false, sb.toString(),0);
                }
                navigationAddNewItem();
            }
        }else{
            Toast.makeText(mContext, "유효하지 않은 url 입니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 북마크 업데이트
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
                    mUpdateBookmark.getPosition(),faviconUrl);
            mBookmarksRepository.saveBookmark(updateBookmark);
        }

    }

    // 북마크 생성
    /**
     * 북마크생성
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
     * 입력한 url 의 형식이 올바른지 체크한다.
     * 1.앞글자가 http 로 시작하며 http:// or http:// 가 존재하는가
     * 2. http:// 가 중복되지는 않았는가?
     **/
    private boolean urlValidation(String url){
        // 1. https:// 혹은 http:// 가 포함되어있는가?
        if(!url.startsWith("http")){
            Log.e("test","잘못됨");
            return false;
        } else if(url.contains("https://") || url.contains("http://")){
            // 2. https:// 혹은 http:// 가 중복되어있는가?
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            String temp;
            if(url.contains("https://")){
                // https 형식
                sb.delete(0,8);
                temp = sb.toString();
                return !temp.contains("https://") && !temp.contains("http://");
            }else if(url.contains("http://")){
                // http 형식
                sb.delete(0,7);
                temp = sb.toString();
                return !temp.contains("https://") && !temp.contains("http://");
            }else{
                return true;
            }
        }
        else{
            return false;
        }
    }

    /**
     * Jsoup 을 활용해 url 유효성을 확인한다.
     * 1. 존재하는 사이트인가?
     * -> 응답이 있는 사이트인가 없는 사이트인가
     * boolean parse
     *
     * 2. 유효한 url 의 경우 baseUri 를 전달받아 확인한다.
     * String baseUri
     *
     * baseUri 와 parse 를 saveBookmark 메소드에 입력하여 저장을 진행한다.
     **/
    @SuppressLint("StaticFieldLeak")
    private class HtmlParser extends AsyncTask<Void,Void,Void> {
        boolean parse = false;
        String baseUri = "";

        boolean isAdd = false;

        @Override
        protected Void doInBackground(Void... voids) {
            // url 을 입력받아 파싱을 진행한다.
            // 1. url 의 유효성을 확인한다.
            try {
                parse = true;
                Document doc = Jsoup.connect(bookmarkUrl.get()).get();
                baseUri = doc.baseUri();
            } catch (IOException e) {
                parse = false;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // 북마크 저장
            // 파싱 성공 or 실패를 전달
            saveBookmark(parse,baseUri,isAdd);

        }
    }

}
