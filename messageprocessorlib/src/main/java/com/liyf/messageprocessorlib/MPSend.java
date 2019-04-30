package com.liyf.messageprocessorlib;

import android.os.Handler;
import android.os.Message;

public class MPSend {

    public static boolean Send(Handler handler,int RECEIVERMESSAGE,Object object){
        if (handler!=null){
            Message.obtain(handler,RECEIVERMESSAGE,object).sendToTarget();
            return true;
        }else{
            return false;
        }
    }
}
