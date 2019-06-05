package com.example.hetzi_beta.Utils;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.hetzi_beta.BusinessApp.HomePage.BusinessHomeActivity;

public class HtzWrapperActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
//                new Thread() {
//                    @Override
//                    public void run() {
//                        Looper.prepare();
//                        Intent intent = new Intent(HtzWrapperActivity.this, BugReportActivity.class);
//                        startActivity(intent);
//                        Toast.makeText(HtzWrapperActivity.this, "חצי: שגיאה כללית", Toast.LENGTH_SHORT);
//                        Looper.loop();
//                    }
//                }.start();
//                try
//                {
//                    Thread.sleep(4000); // Let the Toast display before app will get shutdown
//                }
//                catch (InterruptedException e) {    }
//                System.exit(2);
//            }
//        });

    }
}
