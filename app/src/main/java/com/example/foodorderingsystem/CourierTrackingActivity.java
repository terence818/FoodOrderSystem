package com.example.foodorderingsystem;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.Manifest;

import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CourierTrackingActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView restaurant_name, restaurant_address;
    private Button accept, decline;


    //location gps
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    protected LocationManager locationManager;
    private Location Location_cur;
    private final int REQUEST_LOCATION = 11;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String key;
    private float zoomLevel = 15.0f; //This goes up to 21
    Marker marker;
    private static final int NUMBER_ASYNCTASK = 2;
    private static int counter = 0;
    private static Track track;
    private LatLng mOrigin;
    private boolean tracking=false;
    //    private ClusterManager<MyItem> clusterManager;
    private Marker mCurrLocationMarker;
    private String button_status = "Paid";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        if (savedInstanceState == null) {
            Intent extras = this.getIntent();
            if (extras == null) {
                key = "0.00";
            } else {
                key = extras.getStringExtra("key");
            }

        }

        if(key==null){
            finish();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
//        key = "-MWOIcNGbZROqKXdKF69";

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Write Function To enable gps
            OnGPS();
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,

                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location_cur = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (Location_cur == null) {
                Location_cur = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (Location_cur == null) {
                Location_cur = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }

            if (Location_cur != null)
                setCourierLocation(Location_cur);
        }


        restaurant_name = findViewById(R.id.restaurant_name);
        restaurant_address = findViewById(R.id.restaurant_address);
        accept = findViewById(R.id.accept);
        decline = findViewById(R.id.decline);


        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (button_status) {
                    case "Paid":
                        ref.child("Tracking_Information").child(key).child("status").setValue("Received");
                        accept.setText("Reached restaurant");
                        decline.setEnabled(false);
                        decline.setAlpha(.5f);
                        decline.setClickable(false);
                        button_status = "Received";
                        String date = new SimpleDateFormat("dd/M/yyyy HH:mm", Locale.ENGLISH).format(new Date());
                        ref.child(user.getUid()).child("User_Information").child("timestamp").setValue(date);
                        break;
                    case "Received":
                        ref.child("Tracking_Information").child(key).child("status").setValue("Picked");
                        accept.setText("Food Received");
                        decline.setEnabled(false);
                        button_status = "Picked";
                        break;
                    case "Picked":
                        ref.child("Tracking_Information").child(key).child("status").setValue("OnTheWay");
                        setTimeAndDistance();
                        accept.setText("Start");
                        tracking=true;
                        decline.setEnabled(false);
                        button_status = "OnTheWay";
                        break;
                    case "OnTheWay":
                        ref.child("Tracking_Information").child(key).child("status").setValue("Reached");
                        accept.setText("Reached");
                        decline.setEnabled(false);
                        button_status = "Reached";
                        break;
                    case "Reached":
                        ref.child("Tracking_Information").child(key).child("status").setValue("Completed");
                        accept.setText("Completed");
                        decline.setEnabled(false);
                        tracking=false;
                        button_status = "Completed";
                        break;
                    case "Completed":
                        finish();
                        break;
                }

            }
        });

//        case "Paid":
//
//        case "Received":
//
//        case "Picked":
//
//        case "OnTheWay":
//
//        case "Reached":
//
//        case "Completed":


        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.child("Tracking_Information").child(key).child("status").setValue("Decline");
                String date = new SimpleDateFormat("dd/M/yyyy HH:mm", Locale.ENGLISH).format(new Date());
                ref.child(user.getUid()).child("User_Information").child("timestamp").setValue(date);
                finish();
            }
        });


        if (key != null)
            ref.child("Tracking_Information").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        track = dataSnapshot.getValue(Track.class);
//                        asyncTaskCompleted();
                        if (track != null) {
                            restaurant_name.setText(track.getRestaurant_name());
                            restaurant_address.setText(track.getRestaurant_address());

Log.i("here d",track.getStatus());
                            switch (track.getStatus()) {
                                case "Paid":
                                    Log.i("hello","wtf");
                                    accept.setText("Accept");
                                    decline.setEnabled(true);
                                    decline.setClickable(true);
                                    button_status = "Paid";
                                    break;
                                case "Received":
                                    accept.setText("Reached restaurant");
                                    decline.setEnabled(false);
                                    decline.setAlpha(.5f);
                                    decline.setClickable(false);
                                    button_status = "Received";
//                                    accept.setText("Food Received");
//                                    decline.setEnabled(false);
//                                    button_status = "Picked";
                                    break;
                                case "Picked":
                                    Log.i("hello","wtf");
                                    accept.setText("Food Received");
                                    decline.setEnabled(false);
                                    button_status = "Picked";

//                                    accept.setText("Start");
//                                    tracking=true;
//                                    decline.setEnabled(false);
//                                    button_status = "OnTheWay";
                                    break;
                                case "OnTheWay":

                                    accept.setText("Start");
                                    tracking=true;
                                    decline.setEnabled(false);
                                    button_status = "OnTheWay";

//                                    accept.setText("Reached");
//                                    decline.setEnabled(false);
//                                    button_status = "Reached";
                                    break;
                                case "Reached":

                                    accept.setText("Reached");
                                    decline.setEnabled(false);
                                    button_status = "Reached";

//                                    accept.setText("Completed");
//                                    decline.setEnabled(false);
//                                    tracking=false;
//                                    button_status = "Completed";
                                    break;
                                case "Completed":
                                    accept.setText("Completed");
                                    decline.setEnabled(false);
                                    decline.setAlpha(.5f);
                                    decline.setClickable(false);
                                    break;
                            }

                            asyncTaskCompleted();
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });


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
        mMap.getUiSettings().setZoomControlsEnabled(true);
        asyncTaskCompleted();
//        setClientLocation();
        getMyLocation();
    }

    public synchronized void asyncTaskCompleted() {
        counter++;
        if (counter == NUMBER_ASYNCTASK) {
            setRestaurant();
            setClientLocation();
        }
        //start new activity
    }

    public void setRestaurant() {
        double latitude, longitude;

        latitude = Double.parseDouble(track.getRestaurant_lat());
        longitude = Double.parseDouble(track.getRestaurant_long());

        if (mMap != null) {
            LatLng location = new LatLng(latitude, longitude);
            MarkerOptions marker_local = new MarkerOptions().position(location).title(track.getRestaurant_name());
            marker_local.icon(BitmapDescriptorFactory.fromBitmap(bitmapDescriptorFromVector(R.drawable.restaurant_icon)));
            mMap.addMarker(marker_local);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));
        }

//        setTimeAndDistance();
    }

    public void setClientLocation() {
        double latitude, longitude;

        latitude = Double.parseDouble(track.getBuyer_location_lat());
        longitude = Double.parseDouble(track.getBuyer_location_long());

        if (mMap != null) {
            LatLng location = new LatLng(latitude, longitude);
            MarkerOptions marker_local = new MarkerOptions().position(location).title("Client");
            marker_local.icon(BitmapDescriptorFactory.fromBitmap(bitmapDescriptorFromVector(R.drawable.client)));
            mMap.addMarker(marker_local);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        }
    }

    public void setCourierLocation(Location courierLocation) {

        double latitude, longitude;
        latitude = courierLocation.getLatitude();
        longitude = courierLocation.getLongitude();
        LatLng location = new LatLng(latitude, longitude);


        if (mMap != null) {
            if (mCurrLocationMarker != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));
                mCurrLocationMarker.setPosition(location);
            } else {
                mCurrLocationMarker = mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmapDescriptorFromVector(R.drawable.cur_loc)))
                        .title("You are here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));
            }
        }

        ref.child("Tracking_Information").child(key).child("courier_lat").setValue(String.valueOf(latitude));
        ref.child("Tracking_Information").child(key).child("courier_long").setValue(String.valueOf(longitude));

    }


    private Bitmap bitmapDescriptorFromVector(int vectorDrawableResourceId) {
        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(vectorDrawableResourceId);
        Bitmap b = bitmapdraw.getBitmap();
        return Bitmap.createScaledBitmap(b, width, height, false);

    }

    private void OnGPS() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        if (requestCode == 100) {
            if (!verifyAllPermissions(grantResults)) {
                Toast.makeText(getApplicationContext(), "No sufficient permissions", Toast.LENGTH_LONG).show();
            } else {
                getMyLocation();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean verifyAllPermissions(int[] grantResults) {

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void getMyLocation() {

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Location_cur = location;
                mOrigin = new LatLng(location.getLatitude(), location.getLongitude());
                setCourierLocation(location);

                if(tracking){
                    setTimeAndDistance();
                }

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

        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED) {
                mMap.setMyLocationEnabled(true);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, mLocationListener);

            } else {
                requestPermissions(new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                }, 100);
            }
        }
    }

    private void setTimeAndDistance() {

        double latitude, longitude;

        Log.i("dwqdwqd",track.toString());
        latitude = Double.parseDouble(track.getRestaurant_lat());
        longitude = Double.parseDouble(track.getRestaurant_long());
        // Getting URL to the Google Directions API

        LatLng location = new LatLng(latitude, longitude);
        String url = getDirectionsUrl(mOrigin, location);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Key
        String key = "key=" + "AIzaSyCcgAmTUTcrnaQb5BCFGr8lW0YwIIOjlMM";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

//        String url="https://maps.googleapis.com/maps/api/place/textsearch/json?query=new+york+city+point+of+interest&language=en&key="+key;

        Log.i("here", url);
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception on download", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class to download data from Google Directions URL
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("DownloadTask", "DownloadTask : " + data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                //parse JSON
                //create JSONObject, pass stinrg returned from doInBackground
                JSONObject resultObject = new JSONObject(result);
                //get "results" array
                JSONArray placesArray = resultObject.getJSONArray("routes");
                //marker options for each place returned

                Log.d("test", "The placesArray length is " + placesArray.length() + "...............");

                JSONObject placeObject = placesArray.getJSONObject(0);

                JSONArray legs = placeObject.getJSONArray("legs");

                JSONObject firstObject = legs.getJSONObject(0);
                JSONObject distance_object = firstObject.getJSONObject("distance");
                JSONObject duration_object = firstObject.getJSONObject("duration");

                Log.i("distance_travel", distance_object.getString("text"));
                Log.i("duration_travel", duration_object.getString("text"));

                ref.child("Tracking_Information").child(key).child("distance").setValue(distance_object.getString("text"));
                ref.child("Tracking_Information").child(key).child("duration").setValue(duration_object.getString("text"));

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}