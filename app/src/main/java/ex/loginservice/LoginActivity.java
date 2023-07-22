package ex.loginservice;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import android.Manifest;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LoginActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 상단 바 숨김
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // 위치 권한 허용 여부 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 권한이 허용되지 않음: 권한 요청
            getCurrentLocation();
        } else {
            // 권한이 이미 허용됨: 위치 가져오기 및 업데이트 시작
            getCurrentLocation();
            startLocationUpdates();
        }
    }

    private void getCurrentLocation() {

        // 위치 권한 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }

        // 장치의 현재 위치(위도, 경도) 가져오기
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();       // 위도
                            double longitude = location.getLongitude();     // 경도
                            // Do something with the location
                            Log.d("LocationUpdate", "Latitude: " + latitude + ", Longitude: " + longitude);
                        }
                    }
                });
    }

    private void startLocationUpdates() {
        // LocationRequest 설정
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(6000000); // 10분마다 업데이트
        locationRequest.setFastestInterval(3000000); // 최소 5분마다 업데이트
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // LocationCallback 정의
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        double latitude = location.getLatitude();       // 위도
                        double longitude = location.getLongitude();     // 경도
                        // Do something with the location
                        Log.d("LocationUpdate", "Latitude: " + latitude + ", Longitude: " + longitude);
                    }
                }
            }
        };
    }
}

