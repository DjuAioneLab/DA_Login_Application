package ex.loginservice;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 상단 바 숨김
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // 어플 시작시 4초 동안 splash 이미지 띄우고 LoginActivity로 이동
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity .class));
                finish();
            }
        }, 4000);
    }
}