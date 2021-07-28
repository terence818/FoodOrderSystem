package com.example.foodorderingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText card_num, ccv, year;
    private TextView payment_amount;
    private Intent intent;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private boolean overlap = false;

    private static ArrayList<String> keys;


    private String payment, userLong,userLat, userAddress,restaurant_name,restaurant_long,restaurant_lat,restaurant_address;
    private String courier_assigned, courier_email, courier_timestamp;

    String coach;
    long fee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        if (savedInstanceState == null) {
            Intent extras = this.getIntent();
            if (extras == null) {
                payment = "0.00";
            } else {

                payment = extras.getStringExtra("total");
                userLong = extras.getStringExtra("userLong");
                userLat = extras.getStringExtra("userLat");
                userAddress = extras.getStringExtra("userAddress");
                restaurant_name = extras.getStringExtra("restaurant_name");
                restaurant_long = extras.getStringExtra("restaurant_long");
                restaurant_lat = extras.getStringExtra("restaurant_lat");
                restaurant_address = extras.getStringExtra("restaurant_address");
            }

        }

        keys=new ArrayList<String>();
        if(CartActivity.cartActivity!=null){
            keys=CartActivity.cartActivity.getCartKey();
            Log.i("keys is here", keys.toString());
        }

        courier_timestamp = "";
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        card_num = findViewById(R.id.card_number);
        ccv = findViewById(R.id.ccv);
        year = findViewById(R.id.year);
        payment_amount = findViewById(R.id.payment_amount);
        payment_amount.setText("RM " + payment);

        findViewById(R.id.pay).setOnClickListener(this);

        intent = getIntent();

        if (user != null) {
            ref.child(user.getUid()).child("Card_Information").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        if (dataSnapshot.child("card_num").exists()) {
                            card_num.setText(dataSnapshot.child("card_num").getValue(String.class));

                        }

                        if (dataSnapshot.child("ccv").exists()) {
                            ccv.setText(dataSnapshot.child("ccv").getValue(String.class));
                        }

                        if (dataSnapshot.child("year").exists()) {
                            year.setText(dataSnapshot.child("year").getValue(String.class));
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }

        ref.orderByChild("User_Information/type").equalTo("Courier").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Log.i("corier_existed","yes");
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        Log.i("check here", "npdq");
                        User courier = snapshot.child("User_Information").getValue(User.class);
                        Log.i("check here", courier.getEmail());
                        if (courier != null) {
                            if (courier.getTimestamp().equals("")) {
                                courier_assigned = courier.getUsername();
                                courier_email = courier.getEmail();
                                Log.i("check here", "here");
//                                snapshot.getKey();
                                break;
                            } else if (checktimings(courier.getTimestamp(),courier_timestamp )){
                                courier_assigned = courier.getUsername();
                                courier_email = courier.getEmail();
                                courier_timestamp=courier.getTimestamp();
                                Log.i("check here22", "here");
                            }
                        }

//                        if (dataSnapshot.child("card_num").exists()) {
//                            card_num.setText(dataSnapshot.child("card_num").getValue(String.class));
//
//                        }
//
//                        if (dataSnapshot.child("ccv").exists()) {
//                            ccv.setText(dataSnapshot.child("ccv").getValue(String.class));
//                        }
//
//                        if (dataSnapshot.child("year").exists()) {
//                            year.setText(dataSnapshot.child("year").getValue(String.class));
//                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.pay:
                String card_num_string = card_num.getText().toString().trim();
                String ccv_string = ccv.getText().toString().trim();
                String year_string = year.getText().toString().trim();


                //validation
                if (TextUtils.isEmpty(card_num_string)) {
                    Toast.makeText(getApplicationContext(), "Please key your card number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(ccv_string)) {
                    Toast.makeText(getApplicationContext(), "Please key in CCV", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(year_string)) {
                    Toast.makeText(getApplicationContext(), "Please key in expired date", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(courier_assigned)||TextUtils.isEmpty(courier_email)) {
                    Toast.makeText(getApplicationContext(), "Please try again!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Card card = new Card();
                card.setCard_num(card_num_string);
                card.setCcv(ccv_string);
                card.setYear(year_string);

                Track track = new Track();
                track.setStatus("Paid");
                track.setTotal(payment);
                track.setBuyerId(user.getUid());
                track.setBuyer_location_lat(userLat);
                track.setBuyer_location_long(userLong);
                track.setBuyerId(user.getUid());
                track.setCourier_name(courier_assigned);
                track.setCourier_email(courier_email);
                track.setRestaurant_address(restaurant_address);
                track.setRestaurant_name(restaurant_name);
                track.setRestaurant_lat(restaurant_lat);
                track.setRestaurant_long(restaurant_long);

                ref.child(user.getUid()).child("Card_Information").setValue(card);

                for(String key:keys){
                    ref.child(user.getUid()).child("Cart_List").child(key).removeValue();
                }

                ref.child("Tracking_Information").push().setValue(track, (error, ref) -> {

                    Toast.makeText(getApplicationContext(), "Payment Done.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(PaymentActivity.this, TrackingActivity.class);
                    intent.putExtra("key",ref.getKey());
                    startActivity(intent);

                });

        }


    }

    private boolean checktimings(String time, String endtime) {

        String pattern = "dd/M/yyyy HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        if(endtime.equals("")){
            return true;
        }
        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);


            if (date1.before(date2)) {
                return true;
            } else {

                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


}