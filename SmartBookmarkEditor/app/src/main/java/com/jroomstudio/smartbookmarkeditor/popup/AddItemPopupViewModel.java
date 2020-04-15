package com.jroomstudio.smartbookmarkeditor.popup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksDataSource;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksRepository;
import com.jroomstudio.smartbookmarkeditor.data.category.Category;
import com.jroomstudio.smartbookmarkeditor.data.category.source.CategoriesRepository;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class AddItemPopupViewModel extends BaseObservable {

    // 카테고리 타이틀 입력 관찰
    public final ObservableField<String> categoryTitle = new ObservableField<>();
    // 북마크 타이틀 입력 관찰변수
    public final ObservableField<String> bookmarkTitle = new ObservableField<>();
    // 북마크 url 입력 관찰 변수
    public final ObservableField<String> bookmarkUrl = new ObservableField<>();
    // 북마크 카테고리 스피너 현재 아이템 관찰변수
    public final ObservableField<String> bookmarkCategory = new ObservableField<>();
    // 카테고리 or 북마크
    public final ObservableBoolean isSelectBookmark = new ObservableBoolean();

    // 카테고리 리스트
    public final ObservableList<String> categories = new ObservableArrayList<>();


    // 액티비티 네비게이터
    private PopupAddItemNavigator mNavigator;
    // 액티비티 시작시 네비게이터 셋팅
    void onActivityCreated(PopupAddItemNavigator navigator){ mNavigator = navigator; }
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
    public AddItemPopupViewModel(BookmarksRepository bookmarksRepository,
                                    CategoriesRepository categoriesRepository, Context context) {
        mBookmarksRepository = bookmarksRepository;
        mCategoriesRepository = categoriesRepository;
        mContext = context.getApplicationContext();
        isSelectBookmark.set(true);
    }


    // 아이템이 저장되었으니 종료
    private void navigationAddNewItem(){
        if(mNavigator!=null){
            mNavigator.addNewItem();
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
        createItem();
    }

    // 아이템 생성 (카테고리와 북마크 구분)
    private void createItem(){
        if(isSelectBookmark.get()){
            //북마크 생성
            createBookmark();
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
    private void createBookmark(){

        // 1. 북마크 제목 null 체크
        if(bookmarkTitle.get().equals("")){
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
        }else{
            // 유효성 문제발견 시
            Toast.makeText(mContext, "잘못된 url 형식입니다.", Toast.LENGTH_SHORT).show();
        }

    }

    // 4. 북마크 저장
    private void saveBookmark(boolean parse,String baseUri){
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
            Toast.makeText(mContext, "유효하지 않은 url 입니다.", Toast.LENGTH_SHORT).show();
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
            saveBookmark(parse,baseUri);
        }
    }

}
