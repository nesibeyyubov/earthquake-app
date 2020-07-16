package com.example.googlemap;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.googlemap.model.Earthquake;
import com.example.googlemap.ui.CustomWindowInfo;
import com.example.googlemap.util.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private RequestQueue requestQueue;
    private LocationListener locationListener;
    private float [] markerColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        requestQueue = Volley.newRequestQueue(this);
        markerColors = new float[]{
          BitmapDescriptorFactory.HUE_AZURE,
          BitmapDescriptorFactory.HUE_BLUE,
          BitmapDescriptorFactory.HUE_CYAN,
          BitmapDescriptorFactory.HUE_GREEN,
          BitmapDescriptorFactory.HUE_ROSE,
          BitmapDescriptorFactory.HUE_YELLOW,
        };

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getEarthquakes();

    }

    private void getEarthquakes() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray features = response.getJSONArray("features");

                            for(int i = 0;i<Constants.LIMIT;i++){
                                JSONObject properties = features.getJSONObject(i).getJSONObject("properties");
                                JSONObject geometry = features.getJSONObject(i).getJSONObject("geometry");
                                JSONArray coordinates = geometry.getJSONArray("coordinates");

                                double lng = coordinates.getDouble(0);
                                double lat = coordinates.getDouble(1);

                                Earthquake earthquake = new Earthquake();
                                earthquake.setPlace(properties.getString("place"));
                                earthquake.setType(properties.getString("type"));
                                earthquake.setTime(properties.getLong("time"));
                                earthquake.setMagnitude(properties.getDouble("mag"));
                                earthquake.setDetailLink(properties.getString("detail"));

                                java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
                                String formattedDate= dateFormat.format(new Date(properties.getLong("time")).getTime());

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(new LatLng(lat,lng));
                                markerOptions.title(earthquake.getPlace());
                                markerOptions.snippet("Magnitude: "+earthquake.getMagnitude()+"\n"+"Date: "+formattedDate);
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(markerColors[new Random().nextInt(markerColors.length)]));



                                if(earthquake.getMagnitude() > 3){
                                    CircleOptions circleOptions = new CircleOptions();
                                    circleOptions.center(new LatLng(lat,lng));
                                    circleOptions.fillColor(Color.RED);
                                    circleOptions.radius(30000);
                                    mMap.addCircle(circleOptions);
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                }
                                mMap.addMarker(markerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),5));


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue.add(jsonObjectRequest);
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
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.setInfoWindowAdapter(new CustomWindowInfo(this));
        mMap.setOnInfoWindowClickListener(this);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            assert location != null;
            Log.d("mytag", "Your location: "+location.toString());
            LatLng myLocation = new LatLng(location.getLatitude(),location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(myLocation).title("My location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,3));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                assert location != null;
                LatLng myLocation = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(myLocation).title("My location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,3));
            }
        }
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this,marker.getSnippet(),Toast.LENGTH_LONG).show();
    }
}