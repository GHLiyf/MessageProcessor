package com.liyf.messageprocessorlib;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.liyf.messageprocessorlib.crash.CrashHandler;

public class MPApp extends Application {

    public static String TAG = "ApplicationAPP";
    private static MPApp Instance;
    private CrashHandler crashHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        Instance = this;
        Log.i(TAG, "Application启动");
    }

    public void SendCrashHandler(String filePath){
        if(crashHandler == null){
            crashHandler = CrashHandler.getInstance(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+filePath);
            crashHandler.init(Instance);
        }

    }
    public static MPApp getInstance() {
        return Instance;
    }

    public static Context getContext() {
        return Instance;
    }

    public static String getRString(int resid) {
        return Instance.getString(resid);
    }
}
