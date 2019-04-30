package com.liyf.messageprocessorlib;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

public abstract  class MPAppCompatActivity extends AppCompatActivity implements Handler.Callback {
    public static Handler RECEIVERMESSAGE;
    private Handler handler = new Handler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RECEIVERMESSAGE = handler;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (RECEIVERMESSAGE!=null){
            RECEIVERMESSAGE = null;
        }
    }

    @Override
    public boolean handleMessage(Message m) {
        ReceiveMessage(m.what,m.obj);
        return false;
    }

    public abstract void ReceiveMessage(int what, Object obj);

    public static boolean SendMessage(Handler handler,int messageType,Object object){
        return MPSend.Send(handler,messageType,object);
    }
}
