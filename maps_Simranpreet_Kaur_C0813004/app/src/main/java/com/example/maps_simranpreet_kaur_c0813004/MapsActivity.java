package com.example.maps_simranpreet_kaur_c0813004;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.graphics.Color;
import android.view.SearchEvent;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.maps_simranpreet_kaur_c0813004.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    MarkerOptions options1;
    private float[] distance;
    float t ;
    private float dis = (float) 0.0;
    LatLng   userloc;
    private ActivityMapsBinding binding;
    // home marker
    private Marker  MyMarker;
    String markersTitle = "";
    // destination marker
    private Marker destinationMarker;
    Polyline line;
    Polygon shape;
    // create quadrilateral
    public static int POLYGON_SIDES = 4;
    public static final int REQUEST_CODE = 1;
    // to draw line between markers
    List<Marker> markerList = new ArrayList<>();
    //use location manager and location listener
    LocationManager locationManager;
    LocationListener locationListener;
    private Object PolylineOptions;
    private Object PolygonOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("1");
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(@NonNull Location location) {

                setHomeMarker(location);
            }
        };


        if (!isGrantedPermission())
            requestLocationPermissiom();
        else
            startUpdateLocation();

    /*  mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

           @Override public boolean onMarkerClick(Marker marker) {
            //  Take some action here

            try {

         /*       Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.CANADA);
              // geocoder.getFromLocation(Location.distanceBetween(marker.getPosition().latitude, marker.getPosition().longitude, userloc.latitude, userloc.longitude, distance)

                List<Address> addresses = geocoder.getFromLocation(marker.getPosition().latitude , marker.getPosition().longitude , 1);
               String a= addresses.get(0).getAddressLine(0);
               String city=  addresses.get(0).getAdminArea();

               String l = addresses.get(0).getSubAdminArea();
               System.out.println("Address "+a + "  " +l + "  "+city  );
                Toast toast=Toast.makeText(getApplicationContext(),"Address: "+a + l + city,Toast.LENGTH_SHORT);
                toast.setMargin(150,150);
                toast.show();

            }catch (IOException e)
               {e.printStackTrace();}
          return true;
            }

        }
        ); */
        // draw polygon for 4 points using long press
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener()

        {
            @Override
            public void onMapLongClick (LatLng LatLng){

                // set marker
                setMarker(LatLng);

            }
        });

        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick( Polygon polygon) {
                Toast toast = Toast.makeText(getApplicationContext(), "Total Distance is "+ t, Toast.LENGTH_SHORT);
                toast.setMargin(150, 150);
                toast.show();
            }
        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick( Polyline polyline) {
                System.out.println("pl pts" + polyline.getPoints());
                System.out.println("line pts " +line.getPoints());
                float[] results = new float[1];
                Location.distanceBetween(markerList.get(0).getPosition().latitude, markerList.get(0).getPosition().latitude, markerList.get(1).getPosition().latitude, markerList.get(1).getPosition().latitude, results);
                float distance = results[0];
                //  Location.distanceBetween(polyline.getPoints().);

                System.out.println("polyln points" + polyline.getPoints());
                Toast toast = Toast.makeText(getApplicationContext(), "Polyline Distance is "+ distance, Toast.LENGTH_SHORT);
                toast.setMargin(150, 150);
                toast.show();
            }
        });
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                System.out.println("draggable S");
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                System.out.println("draggable E");

                LatLng LatLng = marker.getPosition();
                float[] results = new float[1];
                Location.distanceBetween(LatLng.latitude , LatLng.longitude , userloc.latitude , userloc.longitude , results);
                float distance = results[0];
                String marker1 = marker.getTitle();
                marker.setTitle(marker1);
                LatLng loc = marker.getPosition();
                marker.setPosition(loc);

                try {
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.CANADA);
                    List<Address> addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1);
                    if(addresses.size() > 0)
                    {
                        Address address = addresses.get(0);
                        String a = address.getAddressLine(0);
                        String city = address.getAdminArea();
                        String l = address.getSubAdminArea();
                        System.out.println("Address " + a + "  " + l + "  " + city);
                        Toast toast = Toast.makeText(getApplicationContext(), "Address: " + a + l + city, Toast.LENGTH_SHORT);
                        toast.setMargin(150, 150);
                        toast.show();
                    }

                }catch (IOException e)
                {
                    e.printStackTrace();
                }
                if (markerList.size() == POLYGON_SIDES)
                    drawLine();
            }
        });


    }
    private  void setHomeMarker(Location location)
    {

        userloc = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(userloc).title("you are here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).snippet("my location : " +userloc);
        MyMarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userloc , 15));
    }


    private void startUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

            return;
        }
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 5000, 0, locationListener);

    }

    private void requestLocationPermissiom() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

    }

    private boolean isGrantedPermission() {

        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //  mFragments.noteStateNotSaved();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CODE == requestCode)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

                return;
            }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
    }


    private void setMarker(LatLng LatLng)  {

        switch(markerList.size())
        {
            case 0 : markersTitle = "A";
                break;
            case 1 : markersTitle = "B";
                break;
            case 2 : markersTitle = "C";
                break;
            case 3 : markersTitle = "D";
                break;
        }

        float[] results = new float[1];
        Location.distanceBetween(LatLng.latitude , LatLng.longitude , userloc.latitude , userloc.longitude , results);
        float distance = results[0];

        options1 = new MarkerOptions().position(LatLng).title(markersTitle).snippet("distance" + distance).draggable(true);
        mMap.addMarker(options1);
        if(equals(options1.getPosition()))
        {
            clearMap1();
        }
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.CANADA);
            List<Address> addresses = geocoder.getFromLocation(LatLng.latitude, LatLng.longitude, 1);
            if(addresses.size() > 0)
            {
                Address address = addresses.get(0);
                String a = address.getAddressLine(0);
                String city = address.getAdminArea();
                String l = address.getSubAdminArea();
                Toast toast = Toast.makeText(getApplicationContext(), "Address: " + a + l + city, Toast.LENGTH_SHORT);
                toast.setMargin(150, 150);
                toast.show();
            }

        }catch (IOException e)
        {
            e.printStackTrace();
        }

        markerList.add(mMap.addMarker(options1));
        if (markerList.size() == POLYGON_SIDES)
            drawLine();

        if (markerList.size() > POLYGON_SIDES)
            clearMap();

    }

    private void clearMap1() {
        for(Marker marker : markerList)
            marker.remove();
        markerList.clear();
        markerList = null;

        line.remove();


    }



    private void clearMap() {
        for(Marker marker : markerList)
            marker.remove();
        markerList.clear();
        markerList = null;
        mMap.clear();
        //  line.remove();
        // line = null;
        shape.remove();
        shape = null;


    }

    private void drawShape() {
        PolygonOptions options = new PolygonOptions().fillColor(Color.GREEN).strokeColor(Color.RED).strokeWidth(5).clickable(true);
        int j = 0;
        for(int i = 0 ; i < POLYGON_SIDES ; i++) {
            options.add(markerList.get(i).getPosition());
            shape = mMap.addPolygon(options);

        }
        while(j < 3) {

            float[] results = new float[1];
            Location.distanceBetween(markerList.get(0).getPosition().latitude, markerList.get(0).getPosition().latitude, markerList.get(1).getPosition().latitude, markerList.get(1).getPosition().latitude, results);
            float distance = results[0];

            float[] results1 = new float[1];
            Location.distanceBetween(markerList.get(1).getPosition().latitude, markerList.get(1).getPosition().latitude, markerList.get(2).getPosition().latitude, markerList.get(2).getPosition().latitude, results1);
            float distance1 = results1[0];

            float[] results2 = new float[1];
            Location.distanceBetween(markerList.get(2).getPosition().latitude, markerList.get(2).getPosition().latitude, markerList.get(3).getPosition().latitude, markerList.get(3).getPosition().latitude, results2);
            float distance2 = results2[0];

            t = distance + distance1 + distance2 ;

            j++;
        }


    }

    private void drawLine() {
        PolylineOptions options = new PolylineOptions().color(Color.RED).width(10).clickable(true);
        int j = 0;

        for(int i = 0 ; i < POLYGON_SIDES ; i++) {
            options.add(markerList.get(i).getPosition());
            line = mMap.addPolyline(options);
        }
        while(j < 3) {

            float[] results = new float[1];
            Location.distanceBetween(markerList.get(0).getPosition().latitude, markerList.get(0).getPosition().latitude, markerList.get(1).getPosition().latitude, markerList.get(1).getPosition().latitude, results);
            float distance = results[0];

            float[] results1 = new float[1];
            Location.distanceBetween(markerList.get(1).getPosition().latitude, markerList.get(1).getPosition().latitude, markerList.get(2).getPosition().latitude, markerList.get(2).getPosition().latitude, results1);
            float distance1 = results1[0];

            float[] results2 = new float[1];
            Location.distanceBetween(markerList.get(2).getPosition().latitude, markerList.get(2).getPosition().latitude, markerList.get(3).getPosition().latitude, markerList.get(3).getPosition().latitude, results2);
            float distance2 = results2[0];

            System.out.println("distance inner1  "+ distance);
            System.out.println("distance inner2 " + distance1);
            System.out.println("distance inner3  "+ distance2);
            t = distance + distance1 + distance2 ;

            System.out.println("total dis  "+ t);

            j++;


        }
        drawShape();
    }

}