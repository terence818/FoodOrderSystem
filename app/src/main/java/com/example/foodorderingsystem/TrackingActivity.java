package com.example.foodorderingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.VerticalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import com.transferwise.sequencelayout.SequenceLayout;
import com.transferwise.sequencelayout.SequenceStep;

import java.util.ArrayList;
import java.util.List;


public class TrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    ImageButton profile, cart, track , back;


    private GoogleMap mMap;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String key;
    private boolean user_location = false;
    private Track track_object;
    private Marker mCurrLocationMarker;
    private TextView driver_name, distance, duration, testing;
    private float zoomLevel = 15.0f; //This goes up to 21

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        profile = (ImageButton) findViewById(R.id.profile);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        if (savedInstanceState == null) {
            Intent extras = this.getIntent();
            if (extras == null) {
                key = null;
            } else {
                key = extras.getStringExtra("key");
            }

        }



        driver_name = findViewById(R.id.driver_name);
        duration = findViewById(R.id.duration);
        distance = findViewById(R.id.distance);
        ;
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        back = (ImageButton) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrackingActivity.this, UserRestaurantActivity.class);
                startActivity(intent);
            }
        });

        VerticalStepView mSetpview0 = (VerticalStepView) findViewById(R.id.step_view0);

        List<String> list0 = new ArrayList<>();
        list0.add("Your Order has been received");
        list0.add("The restaurant is preparing your order");
        list0.add("Your order has been picked up for delivery");
        list0.add("Order arriving soon");
        list0.add("Order is delivered successfully");
        list0.add("Order Completed");

        mSetpview0.setStepsViewIndicatorComplectingPosition(0)//设置完成的步数
                .reverseDraw(false)//default is true
                .setStepViewTexts(list0)//总步骤
                .setLinePaddingProportion(0.65f)//设置indicator线与线间距的比例系数
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(this, R.color.forest_green))//设置StepsViewIndicator完成线的颜色
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(this, R.color.black))//设置StepsViewIndicator未完成线的颜色
                .setStepViewComplectedTextColor(ContextCompat.getColor(this, android.R.color.black))//设置StepsView text完成线的颜色
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(this, R.color.black))//设置StepsView text未完成线的颜色
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(this, R.drawable.greentick))//设置StepsViewIndicator CompleteIcon
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(this, R.drawable.blackcircle))//设置StepsViewIndicator DefaultIcon
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(this, R.drawable.greencircle));//设置StepsViewIndicator AttentionIcon

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSetpview0.setStepsViewIndicatorComplectingPosition(4);

                Intent intent = new Intent(TrackingActivity.this, AccountActivity.class);
                startActivity(intent);

            }
        });

        cart = (ImageButton) findViewById(R.id.cart);

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrackingActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        track = (ImageButton) findViewById(R.id.track);

        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrackingActivity.this, TrackingActivity.class);
                startActivity(intent);
            }
        });

//         testing = findViewById(R.id.testing);


//                key = "-MWOIcNGbZROqKXdKF69";
        if (key != null) {
            ref.child("Tracking_Information").child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        track_object = dataSnapshot.getValue(Track.class);

                        if (track_object != null) {
                            switch (track_object.getStatus()) {
                                case "Paid":
                                    mSetpview0.setStepsViewIndicatorComplectingPosition(0);
//                                    testing.setText("Tracking");
                                    break;
                                case "Received":
                                    mSetpview0.setStepsViewIndicatorComplectingPosition(1);
//                                    testing.setText("Tracking");
                                    break;
                                case "Picked":
                                    mSetpview0.setStepsViewIndicatorComplectingPosition(2);
//                                    testing.setText("Tracking");
                                    break;
                                case "OnTheWay":
                                    mSetpview0.setStepsViewIndicatorComplectingPosition(3);
//                                    testing.setText("Tracking");
                                    break;
                                case "Reached":
                                    mSetpview0.setStepsViewIndicatorComplectingPosition(4);
//                                    testing.setText("Tracking");
                                    break;
                                case "Completed":
                                    mSetpview0.setStepsViewIndicatorComplectingPosition(5);
//                                    testing.setText("Tracking");
                                    break;
                                case "Decline":
                                    Toast.makeText(getApplicationContext(), "Your order has been rejected!", Toast.LENGTH_SHORT).show();
//                                    finish();
//                                    testing.setText("Tracking");
                                    break;
                                default:
//                                    Toast.makeText(getApplicationContext(), "Your order has been rejected!", Toast.LENGTH_SHORT).show();
                                    break;
                            }

                            if (!track_object.getDistance().equals("") && !track_object.getDuration().equals("")) {

                                distance.setText(track_object.getDistance());
                                int distance = Integer.parseInt(track_object.getDuration().replaceAll("[^\\d.]", ""));


//                                str = str.replaceAll("[^\\d.]", "");
                                String duration_total = "Your order will arrive in " + distance + "-" + (distance + 4) + " minute!";
                                duration.setText(duration_total);
                            }

                            driver_name.setText(track_object.getCourier_name());
                            if (!track_object.getCourier_lat().equals("") && !track_object.getCourier_long().equals("")) {
                                setCourierLocation(track_object);
                            }

                            if (!user_location) {
                                setClientLocation();
                            }
                        }


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else {
            ref.child("Tracking_Information").orderByChild("buyerId").equalTo(user.getUid()).limitToLast(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            track_object = snapshot.getValue(Track.class);

                            if (track_object != null) {
                                switch (track_object.getStatus()) {
                                    case "Paid":
                                        mSetpview0.setStepsViewIndicatorComplectingPosition(0);
//                                    testing.setText("Tracking");
                                        break;
                                    case "Received":
                                        mSetpview0.setStepsViewIndicatorComplectingPosition(1);
//                                    testing.setText("Tracking");
                                        break;
                                    case "Picked":
                                        mSetpview0.setStepsViewIndicatorComplectingPosition(2);
//                                    testing.setText("Tracking");
                                        break;
                                    case "OnTheWay":
                                        mSetpview0.setStepsViewIndicatorComplectingPosition(3);
//                                    testing.setText("Tracking");
                                        break;
                                    case "Reached":
                                        mSetpview0.setStepsViewIndicatorComplectingPosition(4);
//                                    testing.setText("Tracking");
                                        break;
                                    case "Completed":
                                        mSetpview0.setStepsViewIndicatorComplectingPosition(5);
//                                    testing.setText("Tracking");
                                        break;
                                    case "Decline":
                                        Toast.makeText(getApplicationContext(), "Your order has been rejected!", Toast.LENGTH_SHORT).show();
                                        finish();
//                                    testing.setText("Tracking");
                                        break;
                                    default:
//                                    Toast.makeText(getApplicationContext(), "Your order has been rejected!", Toast.LENGTH_SHORT).show();
                                        break;
                                }

                                if (!track_object.getDistance().equals("") && !track_object.getDuration().equals("")) {

                                    distance.setText(track_object.getDistance());
                                    int distance = Integer.parseInt(track_object.getDuration().replaceAll("[^\\d.]", ""));


//                                str = str.replaceAll("[^\\d.]", "");
                                    String duration_total = "Your order will arrive in " + distance + "-" + (distance + 4) + " minute!";
                                    duration.setText(duration_total);
                                }

                                driver_name.setText(track_object.getCourier_name());
                                if (!track_object.getCourier_lat().equals("") && !track_object.getCourier_long().equals("")) {
                                    setCourierLocation(track_object);
                                }

                                if (!user_location) {
                                    setClientLocation();
                                }
                            }

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
//            Toast.makeText(getApplicationContext(), "No order found!!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
//        setClientLocation();
        setClientLocation();
    }

    public void setClientLocation() {
        double latitude, longitude;

        if (track_object != null) {
            latitude = Double.parseDouble(track_object.getBuyer_location_lat());
            longitude = Double.parseDouble(track_object.getBuyer_location_long());

            if (mMap != null) {
                LatLng location = new LatLng(latitude, longitude);
                MarkerOptions marker_local = new MarkerOptions().position(location).title("Client");
                mMap.addMarker(marker_local);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));
                user_location = true;
            }
        }

    }

    public void setCourierLocation(Track courierLocation) {

        double latitude, longitude;
        latitude = Double.parseDouble(courierLocation.getCourier_lat());
        longitude = Double.parseDouble(courierLocation.getCourier_long());
        LatLng location = new LatLng(latitude, longitude);

        if (mMap != null) {
            if (mCurrLocationMarker != null) {
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));
                mCurrLocationMarker.setPosition(location);
            } else {
                mCurrLocationMarker = mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmapDescriptorFromVector(R.drawable.driver_icon)))
                        .title("You are here"));
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));
            }
        }

    }

    private Bitmap bitmapDescriptorFromVector(int vectorDrawableResourceId) {
        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(vectorDrawableResourceId);
        Bitmap b = bitmapdraw.getBitmap();
        return Bitmap.createScaledBitmap(b, width, height, false);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Store UI state to the savedInstanceState.
        // This bundle will be passed to onCreate on next call.  EditText txtName = (EditText)findViewById(R.id.txtName);

        String key_start = key;
        savedInstanceState.putString("key", key_start);


        super.onSaveInstanceState(savedInstanceState);
    }
}