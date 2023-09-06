package ex.loginservice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

// 사용자 계정 정보 모델 클래스
public class UserAccount {
    private String idToken;     // Firebase UID(고유 토큰정보)
    private String emailId;     // 이메일 아이디
    private String password;    // 비밀번호
    private String name;        // 이름

    private double latitude;    // 위도
    private double longitude;   // 경도

    // 생성자
    public UserAccount() {}

    public String getIdToken() { return idToken; }

    public void setIdToken(String idToken) { this.idToken = idToken; }

    public String getEmailId() { return emailId; }

    public void setEmailId(String emailId) { this.emailId = emailId; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}