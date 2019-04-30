package com.Liyf;

import android.os.Bundle;

import com.liyf.messageprocessorlib.MPActivity;

public class MainActivity extends MPActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void ReceiveMessage(int what, Object obj) {

    }


}
