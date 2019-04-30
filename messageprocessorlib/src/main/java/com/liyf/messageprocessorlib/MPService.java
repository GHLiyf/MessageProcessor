package com.liyf.messageprocessorlib;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public abstract class MPService extends Service implements Handler.Callback {

    public static Handler RECEIVERMESSAGE;
    private Handler handler = new Handler(this);


    @androidx.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RECEIVERMESSAGE = handler;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (RECEIVERMESSAGE != null) {
            RECEIVERMESSAGE = null;
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
