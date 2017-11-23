package com.wind.androidaidl;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int MESSAGE_NEW_BOOK_ARRIVED=1;
    private static final String TAG="MainActivity";
    private IBookManager mRemoteBookManager;
    private  Handler mHander=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case MESSAGE_NEW_BOOK_ARRIVED:
                    Log.i(TAG,"receive new book:   "+msg.obj);
                    break;
                    default:
                        super.handleMessage(msg);
            }


        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent=new Intent(this,BookManagerService.class);
        bindService(intent,mConnection,BIND_AUTO_CREATE);
    }
    private ServiceConnection mConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IBookManager bookManager=IBookManager.Stub.asInterface(iBinder);
            try {
                mRemoteBookManager=bookManager;
                List<Book> list=bookManager.getBookList();
                Log.i(">>>>>>>",list.getClass().getCanonicalName());
                Log.i(">>>>>>>",list.get(1).getBookName() + list.get(0).getBookName());
                Book book=new Book(3,"Android开发艺术探索");
                bookManager.addBook(book);
                List<Book> newlist=bookManager.getBookList();
                Log.i(">>>>>>",newlist.get(2).getBookName());
                bookManager.registerListener(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }


        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mRemoteBookManager=null;
        }
    };
    private IOnNewBookArrivedListener mOnNewBookArrivedListener=new IOnNewBookArrivedListener.Stub(){
        @Override
        public void onNewBookArrived(Book newBook) throws RemoteException {
            mHander.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED,newBook).sendToTarget();
        }
    };
    @Override
    protected void onDestroy() {
        if (mRemoteBookManager!=null&&mRemoteBookManager.asBinder().isBinderAlive())
        {
            try {
                mRemoteBookManager.unRegisterListener(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
        super.onDestroy();
    }
}
