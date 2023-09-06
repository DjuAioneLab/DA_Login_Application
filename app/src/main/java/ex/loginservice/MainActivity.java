package ex.loginservice;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance(); // 파이어 베이스 인증
    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference(); // 실시간 데이터 베이스

    AppCompatButton earthquakeUrl;
    AppCompatButton floodUrl;
    AppCompatButton heatwaveUrl;

    private TextView tv_OutPut;

    double gps_length = 9999999.999999;
    String provider;
    double latitude;
    double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_OutPut = (TextView) findViewById(R.id.textViewTemp);


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // 버튼 초기화
        earthquakeUrl = findViewById(R.id.appCompatButton);
        floodUrl = findViewById(R.id.appCompatButton2);
        heatwaveUrl = findViewById(R.id.appCompatButton3);

        // 위치 권환 요청 및 위치 업데이트
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            // 가장 최근 위치정보 가져오기
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
            }
            // 위치정보를 원하는 시간, 거리마다 갱신해준다.
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//                    1000,
//                    1,
//                    gpsLocationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    100000,
                    1,
                    gpsLocationListener);
        }
        earthquakeUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://djuaionelab.github.io/DA-plus/";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        floodUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://3hwangg.github.io/23opensw/DAweb.html";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        heatwaveUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://j0hw.github.io/heatwave/";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }


    // 상단 바 숨김

    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // 위치 리스너는 위치정보를 전달할 때 호출되므로 onLocationChanged()메소드 안에 위지청보를 처리를 작업을 구현 해야합니다.
            provider = location.getProvider();
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            Log.d("csv", provider + " " + latitude + " " + longitude);

            try {
                loadData();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (CsvException e) {
                throw new RuntimeException(e);
            }

            String userId = mFirebaseAuth.getCurrentUser().getUid();

            saveLocationToFirebase(userId, latitude, longitude);

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onProviderDisabled(String provider) {

        }
    };

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            RequestHttpConnection requestHttpURLConnection = new RequestHttpConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.

            Log.d("onPostEx", "출력 값 : " + s);

            String tmperature = null;

            try {
                JSONObject jsonObj_1 = new JSONObject(s);
                String response = jsonObj_1.getString("response");

                // response 로 부터 body 찾기
                JSONObject jsonObj_2 = new JSONObject(response);
                String body = jsonObj_2.getString("body");

                // body 로 부터 items 찾기
                JSONObject jsonObj_3 = new JSONObject(body);
                String items = jsonObj_3.getString("items");

                // items로 부터 itemlist 를 받기
                JSONObject jsonObj_4 = new JSONObject(items);
                JSONArray jsonArray = jsonObj_4.getJSONArray("item");

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObj_4 = jsonArray.getJSONObject(i);
                    String fcstValue = jsonObj_4.getString("obsrValue");
                    String category = jsonObj_4.getString("category");

                    if (category.equals("T3H") || category.equals("T1H")) {
                        tmperature = fcstValue + "℃";
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


            tv_OutPut.setText(tmperature);

        }
    }

    public void saveLocationToFirebase(String userId, double latitude, double longitude) {
        // 위치 정보를 데이터베이스에 저장
        UserAccount userAccount = new UserAccount();
        userAccount.setLatitude(latitude);
        userAccount.setLongitude(longitude);

        mDatabaseRef.child("UserAccount").child(userId).child("latitude").setValue(latitude);
        mDatabaseRef.child("UserAccount").child(userId).child("longitude").setValue(longitude);
    }

    private String loadData() throws IOException, CsvException {
        TextView loctv = (TextView) findViewById(R.id.textViewLocation);
        AssetManager assetManager = this.getAssets();
        InputStream inputStream = assetManager.open("tempinfo.csv");
        CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream, "UTF-8"));
        gps_length = 99999999.99999999;
        double now_length = 0.;
        String[] infoSave = {"", "", ""};
        List<String[]> allContent = (List<String[]>) csvReader.readAll();
        for (String content[] : allContent) {
            StringBuilder sb = new StringBuilder("");
            now_length = Math.pow(Math.abs(latitude - Double.parseDouble(content[14])), 2.0) + Math.pow(Math.abs(longitude - Double.parseDouble(content[13])), 2.0);
            //Log.d("test", String.valueOf(content[14]) + " " +  String.valueOf(content[13]) + " " + String.valueOf(now_length));

            if (gps_length > now_length) {
                gps_length = now_length;
                infoSave[0] = content[2] + " " + content[3];

                infoSave[1] = content[5];
                infoSave[2] = content[6];


            }
            //Log.d("csv", "위치 : " + content[2] + content[3] + content[4] + " || X : " + content[5] + " || Y : " + content[6] + " || 위도 : " + content[13] + " || 경도 : " + content[14]);
        }


        Log.d("csv", infoSave[0] + ", X : " + infoSave[1] + ", Y : " + infoSave[2]);

        loctv.setText(infoSave[0]);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c1 = Calendar.getInstance();
        String strToday = sdf.format(c1.getTime());

        String baseTime;
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HHmm");
        String currentTime = currentTimeFormat.format(c1.getTime());

        if (currentTime.compareTo("0600") >= 0 && currentTime.compareTo("1200") < 0) {
            baseTime = "0600";
        } else if (currentTime.compareTo("1200") >= 0 && currentTime.compareTo("1800") < 0) {
            baseTime = "1200";
        } else if (currentTime.compareTo("1800") >= 0 && currentTime.compareTo("0000") < 0) {
            baseTime = "1800";
        } else {
            baseTime = "0000";
        }

        String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst?" +
                "serviceKey=" + "LBvNv%2BIYcpegQeY9lSdJGJuZZaIjsKgOwOQyOEtU9RZr4qMsceUJpjwUOc9ltnVfZJpGJRoKtcbBPojKGx08CA%3D%3D" +
                "&numOfRows=" + "10" +
                "&pageNo=" + "1" +
                "&dataType=" + "JSON" +
                "&base_date=" + "20230905"+
                "&base_time=" + "0000" + // 조건에 따라 현재 시간을 설정
                "&nx=" + infoSave[1] +
                "&ny=" + infoSave[2];

// AsyncTask를 통해 HttpURLConnection 수행.
        MainActivity.NetworkTask networkTask = new NetworkTask(url, null);
        networkTask.execute();

        return strToday;
    }
}
