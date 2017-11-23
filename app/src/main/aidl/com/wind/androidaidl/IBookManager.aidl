// IBookManager.aidl
package com.wind.androidaidl;

// Declare any non-default types here with import statements
import com.wind.androidaidl.IOnNewBookArrivedListener;
import com.wind.androidaidl.Book;
interface IBookManager {
    List <Book> getBookList();
    void addBook(in Book book);
    void registerListener(IOnNewBookArrivedListener listener);
    void unRegisterListener(IOnNewBookArrivedListener listener);
}
