package com.icil.locationreader;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private LocationManager mLocMan;
    private TextView mStatus;
    private TextView mResult;
    private String mProvider;
    private int mCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mStatus = (TextView) findViewById(R.id.status);
        mResult = (TextView) findViewById(R.id.result);
        mProvider = LocationManager.GPS_PROVIDER;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCount = 0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
            return;
        }
        mLocMan.requestLocationUpdates(mProvider, 1000, 1, mListener);
        mStatus.setText("현재상태 : 서비스 시작");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
            return;
        }
        mLocMan.removeUpdates(mListener);
        mStatus.setText("현재상태 : 서비스 정지");
    }

    LocationListener mListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mCount++;
                String sloc = String.format("수신회수 : %d\n위도 : %f\n경도 : %f\n고도 : %f",mCount,location.getLatitude(),location.getLongitude(),location.getAltitude());
                sloc += "\nextra : " + location.getExtras();
                sloc += "\naccuracy : " + location.getAccuracy();
                sloc += "\nprovider : " + location.getProvider();
                sloc += "\nbearing : " + location.getBearing();
                sloc += "\nspeed : " + location.getSpeed();
                sloc += "\nTime : " + location.getTime();
                sloc += "\nElapsedRealTime : " + location.getElapsedRealtimeNanos();
                sloc += "\nisFormMockProvider : " + location.isFromMockProvider();

                mResult.setText(sloc);
            }

        @Override
        public void onStatusChanged(String s, int status, Bundle bundle) {
            String sStatus ="";
            switch (status){
                case LocationProvider.OUT_OF_SERVICE:
                    sStatus = "범위 벗어남";
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    sStatus = "일시적 불능";
                    break;
                case LocationProvider.AVAILABLE:
                    sStatus = "사용 가능";
                    break;
            }
            mStatus.setText("현재상태 : " + sStatus);
        }

        @Override
        public void onProviderEnabled(String s) {
            mStatus.setText("현재상태 : 서비스 사용 가능");
        }

        @Override
        public void onProviderDisabled(String s) {
            mStatus.setText("현재상태 : 서비스 사용 불가");
        }
    };
}
