package com.example.foodorderingsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;


import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class AccountActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    EditText editText,username,email,phone;
    TextView name;
    private GoogleApiClient mClient;
    private FusedLocationProviderClient fusedLocationClient;
    private ProgressBar progressBar;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser User;
    private String currentUserID;
    private int  PERMISSIONS_REQUEST_FINE_LOCATION=1;
    private Activity that=this;

    String mStringLatitude;
    String mStringLongitude;
    String Address;

    ImageButton profile,cart,track, back;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        editText=findViewById(R.id.address_edit_text);
        username=findViewById(R.id.username_edit_text);
        phone=findViewById(R.id.phone_edit_text);
        email=findViewById(R.id.email_edit_text);

        name=findViewById(R.id.textView);



        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();
        User = auth.getCurrentUser();

        progressBar = findViewById(R.id.login_progress);
        progressBar.bringToFront();

        back = (ImageButton) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, UserRestaurantActivity.class);
                startActivity(intent);
            }
        });

        profile = (ImageButton)findViewById(R.id.profile);

        profile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });

        cart = (ImageButton)findViewById(R.id.cart);

        cart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        track = (ImageButton)findViewById(R.id.track);

        track.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, TrackingActivity.class);
                startActivity(intent);
            }
        });



        if (auth.getCurrentUser() != null) {
            currentUserID = auth.getCurrentUser().getUid();
        } else {
            currentUserID = null;
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Initialize places
        Places.initialize(getApplicationContext(),"AIzaSyCcgAmTUTcrnaQb5BCFGr8lW0YwIIOjlMM");

        ActivityCompat.requestPermissions(AccountActivity.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_FINE_LOCATION);

        if (User != null) {

            ref.child(User.getUid()).child("User_Information").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        if (dataSnapshot.child("username").exists()) {
                            username.setText(dataSnapshot.child("username").getValue(String.class));
                            name.setText(dataSnapshot.child("username").getValue(String.class) + " Profile");
                        }

                        if (dataSnapshot.child("email").exists()) {
                            email.setText(dataSnapshot.child("email").getValue(String.class));
                        }

                        if (dataSnapshot.child("phone").exists()) {
                            phone.setText(dataSnapshot.child("phone").getValue(String.class));
                        }

                        if (dataSnapshot.child("address").exists()) {
                            editText.setText(dataSnapshot.child("address").getValue(String.class));
                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }

        Button back = findViewById(R.id.back_button);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AccountActivity.this, UserRestaurantActivity.class);
                startActivity(intent);

            }
            });

        Button upload = findViewById(R.id.update_button);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAccount();
//                mGeofencing.registerAllGeofences();

                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(that, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
//                                    textView1.setText(String.format("Locality Name: %s", location));
                                }
                            }
                        });
            }
        });


        editText.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //Initialize place field list
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ID,Place.Field.NAME,Place.Field.ADDRESS,Place.Field.LAT_LNG
                );
                //Create intent
                Intent intent =new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        fieldList).build(AccountActivity.this);
                //Start activity result
                startActivityForResult(intent,100);

            }
        });

        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(com.google.android.gms.location.places.Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRingerPermissionsClicked(View view) {
        Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK) {
            //When success
            //Initialize place
            Place place = Autocomplete.getPlaceFromIntent(data);

            //Set address on EditText

            editText.setText(place.getAddress());
            //Set locality name
            //Set latitude & longtitude
            LatLng latLng = place.getLatLng();


            mStringLatitude = String.valueOf(latLng.latitude);
            mStringLongitude = String.valueOf(latLng.longitude);
//            textView2.setText("Latitude:" + String.valueOf(latLng.latitude));
//            textView3.setText("Longitude:" + String.valueOf(latLng.longitude));

            Address = editText.getText().toString();








        } else if(resultCode == AutocompleteActivity.RESULT_ERROR) {
            //When error
            //Initialize status
            Status status = Autocomplete.getStatusFromIntent(data);
            //Display toast
            Toast.makeText(getApplicationContext(),"Please choose a location.", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onConnected(@Nullable Bundle connectionHint) {


    }

    /***
     * Called when the Google API Client is suspended
     *
     @param cause cause The reason for the disconnection. Defined by constants CAUSE_.
     */
    @Override
    public void onConnectionSuspended(int cause) {

    }

    /***
     * Called when the Google API Client failed to connect to Google Play Services
     *
     * @param result A ConnectionResult that can be used for resolving the error
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {


    }

    public void setAccount() {




        progressBar.setVisibility(View.VISIBLE);

                    String Username = username.getText().toString().trim();
                    String Email = email.getText().toString().trim();
                    String Phone = phone.getText().toString().trim();

                    User user = new User();
                    user.setUsername(Username);
                    user.setEmail(Email);
                    user.setPhone(Phone);
                    user.setAddress(Address);
                    user.setType("User");
                    user.setLatitude(mStringLatitude);
                    user.setLongitude(mStringLongitude);

                    ref.child(User.getUid()).child("User_Information").setValue(user, new DatabaseReference.CompletionListener() {
                        public void onComplete(DatabaseError error, @NonNull DatabaseReference ref) {

                            Toast.makeText(getApplicationContext(), "Upload Successfully.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });



    }



}