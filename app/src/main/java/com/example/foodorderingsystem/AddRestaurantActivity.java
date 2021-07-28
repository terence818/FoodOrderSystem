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
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AddRestaurantActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    EditText editText;
    private Spinner dropdown_cuisine;
    TextView textView1,textView2,textView3;
    private GoogleApiClient mClient;
    private FusedLocationProviderClient fusedLocationClient;
    private ProgressBar progressBar;

    private EditText edit_fee;

    private DatabaseReference databaseReference;
    private DatabaseReference logReference;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private String currentUserID;
    private int  PERMISSIONS_REQUEST_FINE_LOCATION=1;
    private Activity that=this;

    String mStringLatitude;
    String mStringLongitude;
    String Address;
    String Cuisine;
    String Restaurantname;
    String  Placeid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        editText=findViewById(R.id.edit_address);
        edit_fee=findViewById(R.id.delivery_fee);
        textView1=findViewById(R.id.text_view1);


        //array to populate start time spinner
        String[] arraySpinner = new String[]{
                "Western", "Eastern", "Chinese", "Malay" , "Indian" , "FastFood"

        };

        //array to populate durationn spinner

        dropdown_cuisine = findViewById(R.id.cuisine);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown_cuisine.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        progressBar = findViewById(R.id.login_progress);
        progressBar.bringToFront();

        if (auth.getCurrentUser() != null) {
            currentUserID = auth.getCurrentUser().getUid();
        } else {
            currentUserID = null;
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Initialize places
        Places.initialize(getApplicationContext(),"AIzaSyCcgAmTUTcrnaQb5BCFGr8lW0YwIIOjlMM");

        ActivityCompat.requestPermissions(AddRestaurantActivity.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_FINE_LOCATION);

//        CheckBox ringerPermissions = (CheckBox) findViewById(R.id.ringer_permissions_checkbox);
//        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        // Check if the API supports such permission change and check if permission is granted
//        if (Build.VERSION.SDK_INT >= 24 && !nm.isNotificationPolicyAccessGranted()) {
//            ringerPermissions.setChecked(false);
//        } else {
//            ringerPermissions.setChecked(true);
//            ringerPermissions.setEnabled(false);
//        }


        //Set EditText non focusable
        editText.setFocusable(false);
        editText.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //Initialize place field list
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ID,Place.Field.NAME,Place.Field.ADDRESS,Place.Field.LAT_LNG
                        );
                //Create intent
                Intent intent =new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        fieldList).build(AddRestaurantActivity.this);
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

//        mGeofencing = new Geofencing(this, mClient);


        Button upload = findViewById(R.id.location_upload_button);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocation();
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
            textView1.setText("Restaurant name :" + String.format(place.getName()));
            //Set latitude & longtitude
            LatLng latLng = place.getLatLng();


            mStringLatitude = String.valueOf(latLng.latitude);
            mStringLongitude = String.valueOf(latLng.longitude);
//            textView2.setText("Latitude:" + String.valueOf(latLng.latitude));
//            textView3.setText("Longitude:" + String.valueOf(latLng.longitude));

            Address = editText.getText().toString();
            Restaurantname = String.format(place.getName());
            Placeid =String.format(place.getId());







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

    public void setLocation() {

        String cuisine = dropdown_cuisine.getSelectedItem().toString();
        String fee = edit_fee.getText().toString().trim();


        if (TextUtils.isEmpty(Address)) {
            Toast.makeText(AddRestaurantActivity.this, "Address cannot be empty", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(Restaurantname)) {
            Toast.makeText(AddRestaurantActivity.this, "Restaurant name cannot be empty", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }


            progressBar.setVisibility(View.VISIBLE);

            Restaurant restaurant = new Restaurant();
            restaurant.setAddress(Address);
            restaurant.setRestaurantname(Restaurantname);
            restaurant.setLatitude(mStringLatitude);
            restaurant.setLongitude(mStringLongitude);
            restaurant.setPlaceid(Placeid);
            restaurant.setCuisine(cuisine);
            restaurant.setDelivery_fee(fee);
            restaurant.setBudget("0");


            databaseReference.child("Restaurant_List").push().setValue(restaurant, new DatabaseReference.CompletionListener() {
                public void onComplete(DatabaseError error, @NonNull DatabaseReference ref) {

                    Toast.makeText(getApplicationContext(), "Restaurant Added Successfully.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });


    }


}