package ex.loginservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class CsvTest extends AppCompatActivity {


    double gps_length = 9999999.999999;
    String provider;
    double latitude;
    double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csv_test);

        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CsvTest.this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            // 가장 최근 위치정보 가져오기
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
            }
            // 위치정보를 원하는 시간, 거리마다 갱신해준다.
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//                    1001,
//                    1,
//                    gpsLocationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000,
                    1,
                    gpsLocationListener);
        }


    }

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


        } public void onStatusChanged(String provider, int status, Bundle extras) {

        } public void onProviderEnabled(String provider) {

        } public void onProviderDisabled(String provider) {

        }
    };
    private void loadData() throws IOException, CsvException {
        AssetManager assetManager = this.getAssets();
        InputStream inputStream = assetManager.open("tempinfo.csv");
        CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream, "UTF-8"));
        gps_length = 99999999.99999999;
        double now_length = 0.;
        String[] infoSave = {"", "", ""};
        List<String[]> allContent = (List<String[]>) csvReader.readAll();
        for(String content[] : allContent){
            StringBuilder sb = new StringBuilder("");
            now_length = Math.pow(Math.abs(latitude - Double.parseDouble(content[14])), 2.0) + Math.pow(Math.abs(longitude - Double.parseDouble(content[13])), 2.0);
            //Log.d("test", String.valueOf(content[14]) + " " +  String.valueOf(content[13]) + " " + String.valueOf(now_length));

            if (gps_length > now_length){
                gps_length = now_length;
                infoSave[0] = content[2] + content[3] + content[4];
                infoSave[1] = content[5];
                infoSave[2] = content[6];

            }
            //Log.d("csv", "위치 : " + content[2] + content[3] + content[4] + " || X : " + content[5] + " || Y : " + content[6] + " || 위도 : " + content[13] + " || 경도 : " + content[14]);
        }
        Log.d("csv", infoSave[0] + ", X : " + infoSave[1] + ", Y : " + infoSave[2]);
    }
}

