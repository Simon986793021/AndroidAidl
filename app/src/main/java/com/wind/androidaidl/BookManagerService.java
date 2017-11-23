package com.wind.androidaidl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhangcong on 2017/11/22.
 */

public class BookManagerService extends Service {


    private CopyOnWriteArrayList<Book> mBookList= new CopyOnWriteArrayList<Book>();
    private CopyOnWriteArrayList<IOnNewBookArrivedListener> mListenerList=new CopyOnWriteArrayList<>();
    private AtomicBoolean mIsServiceDestoryed=new AtomicBoolean(false);
    private Binder mBinder=new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            if (!mListenerList.contains(listener)){
                mListenerList.add(listener);
            }
            else {
                Log.i(">>>>>>>>","already exists");
            }
            Log.i(">>>>>>>>","registerListener :    "+mListenerList.size() +mListenerList.get(0).getClass().getName());
        }

        @Override
        public void unRegisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            if (mListenerList.contains(listener))
            {
                mListenerList.remove(listener);
            }
            Log.i(">>>>>>>>","unRegisterListener:   "+mListenerList.size());
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1,"Android"));
        mBookList.add(new Book(2,"IOS"));
        new Thread(new ServiceWorker()).start();

    }
    private  void onNewBookArrived(Book book) throws RemoteException {
        mBookList.add(book);
        for (int i=0;i<mListenerList.size();i++)
        {
            IOnNewBookArrivedListener listener=mListenerList.get(i);
            Log.i("BookManagerService","notify listener: "+listener);
            listener.onNewBookArrived(book);
        }
    }
    private class ServiceWorker implements Runnable{

        @Override
        public void run() {
            while (!mIsServiceDestoryed.get())
            {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId=mBookList.size()+1;
                Book newBook=new Book(bookId,"new Book"+bookId);
                try {
                    onNewBookArrived(newBook);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestoryed.set(true);
        super.onDestroy();

    }
}
