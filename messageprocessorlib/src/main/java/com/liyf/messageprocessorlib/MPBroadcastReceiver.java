package com.liyf.messageprocessorlib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

public abstract class MPBroadcastReceiver extends BroadcastReceiver implements Handler.Callback {


    public static Handler RECEIVERMESSAGE;
    private Handler handler = new Handler(this);




    @Override
    public void onReceive(Context context, Intent intent) {
        if (RECEIVERMESSAGE == null){
            RECEIVERMESSAGE = handler;
        }

    }



    @Override
    public boolean handleMessage(Message m) {
        ReceiveMessage(m.what, m.obj);
        return false;
    }

    public abstract void ReceiveMessage(int what, Object obj);

    public static boolean SendMessage(Handler handler, int messageType, Object object) {
        return MPSend.Send(handler, messageType, object);
    }


}
