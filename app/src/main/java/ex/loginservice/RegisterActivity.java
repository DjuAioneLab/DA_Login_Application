package ex.loginservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance(); // 파이어 베이스 인증
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference(); // 실시간 데이터 베이스

        EditText mEtName = findViewById(R.id.et_name);
        EditText mEtPhone = findViewById(R.id.et_phone);
        EditText mEtPwd = findViewById(R.id.et_pwd);
        Button mRegisterBtn = (Button)findViewById(R.id.finishregister_Btn);

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 회원가입 처리 시작
                String name = mEtName.getText().toString();
                String phoneNumber = mEtPhone.getText().toString();
                String password = mEtPwd.getText().toString();
                String email = phoneNumber + "@email.com";

                // Firebase Auth 진행
                mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                    UserAccount account = new UserAccount();
                                    account.setName(name);
                                    account.setIdToken(firebaseUser.getUid());
                                    account.setEmailId(firebaseUser.getEmail());
                                    account.setPassword(password);

                                    mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
                                    Toast.makeText(RegisterActivity.this, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();   // 현재 액티비티 파괴
                                }
                                else {
                                    Toast.makeText(RegisterActivity.this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}