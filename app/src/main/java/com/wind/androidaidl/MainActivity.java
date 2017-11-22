package com.wind.androidaidl;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity {

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
                List<Book> list=bookManager.getBookList();
                Log.i(">>>>>>>",list.getClass().getCanonicalName());
                Log.i(">>>>>>>",list.get(1).getBookName() + list.get(0).getBookName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
}
