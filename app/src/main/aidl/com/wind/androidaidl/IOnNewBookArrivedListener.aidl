// IOnNewBookArrivedListener.aidl
package com.wind.androidaidl;

// Declare any non-default types here with import statements
import com.wind.androidaidl.Book;
interface IOnNewBookArrivedListener {
   void  onNewBookArrived(in Book newBook);
}
