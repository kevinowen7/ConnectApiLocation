package com.example.tutorialgooglemaps;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends FragmentActivity {
    private GoogleMap mGoogleMap;
    private SupportMapFragment mMapFragment;
    private LatLng mLatLng;
    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setMap();
    }

    private ArrayList<HashMap<String, String>> RequestGetLocation(String location) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        CallBackGetLocation login_req = new CallBackGetLocation(MainActivity.this);
        //Log.d("CEKIDBOOK",id_book);
        try {
            arrayList = login_req.execute(
                    location
            ).get();
        }catch (Exception e){
            Log.d("Error Message",e.getMessage());
        }

        return arrayList;
    }

    private void setMap() {
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        assert mMapFragment != null;
        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mLocation = "Bandung";

                //call API
                //call http API untuk register
                ArrayList<HashMap<String, String>> reqShow = RequestGetLocation(mLocation);


                if (Objects.equals(reqShow.get(0).get("success"), "1")) {
                    mLatLng = new LatLng(Double.valueOf(Objects.requireNonNull(reqShow.get(1).get("lat"))),Double.valueOf(Objects.requireNonNull(reqShow.get(1).get("long"))));
                    mGoogleMap.addMarker(new MarkerOptions().position(mLatLng).title(mLocation).draggable(true));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng,20));
                    mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                        @Override
                        public void onMarkerDragStart(Marker marker) {

                        }

                        @Override
                        public void onMarkerDrag(Marker marker) {

                        }

                        @Override
                        public void onMarkerDragEnd(Marker marker) {
                            double latitude = marker.getPosition().latitude;
                            double longitude = marker.getPosition().longitude;
                            Toast.makeText(MainActivity.this, "Latitude : "+latitude+"\n Longitude : "+longitude, Toast.LENGTH_SHORT).show();
                        }
                    });

                } else if (Objects.equals(reqShow.get(0).get("success"), "-1")) {
                    //Internal error
                    final ArrayList<HashMap<String, String>> finalReqLogin = reqShow;
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, finalReqLogin.get(0).get("message"), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (Objects.equals(reqShow.get(0).get("success"), "0")) {
                    final ArrayList<HashMap<String, String>> finalReqLogin = reqShow;
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, finalReqLogin.get(0).get("message"), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
