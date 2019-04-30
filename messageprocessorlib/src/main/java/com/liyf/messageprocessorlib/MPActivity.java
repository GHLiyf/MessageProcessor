package com.liyf.messageprocessorlib;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public abstract class MPActivity extends Activity implements Handler.Callback {

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

    public static boolean SendMessage(Handler handler,int RECEIVERMESSAGE,Object object){
        return MPSend.Send(handler,RECEIVERMESSAGE,object);
    }
}
