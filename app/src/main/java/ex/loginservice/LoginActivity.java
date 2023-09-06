package ex.loginservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance(); // 파이어 베이스 인증
    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference(); // 실시간 데이터 베이스
    EditText mEtPhone;
    EditText mEtPwd;
    Button loginBtn;
    Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEtPhone = findViewById(R.id.phoneNumberField);
        mEtPwd = findViewById(R.id.pwdField);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.ToRegister_Btn);

        // 상단 바 숨김
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // 로그인 버튼
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = mEtPhone.getText().toString();
                String password = mEtPwd.getText().toString();
                String email = phoneNumber + "@email.com";

                mFirebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    // 로그인 성공
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(LoginActivity.this, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();
                                    finish();   // 현재 액티비티 파괴
                                }
                                else {
                                    Toast.makeText(LoginActivity.this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        // 회원가입 창으로 이동
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

}
