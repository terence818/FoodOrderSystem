package com.example.foodorderingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
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

public class CartActivity extends AppCompatActivity implements View.OnClickListener  {

    private String restaurant, key;
    private Intent intent;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String currentUserID;
    private ValueEventListener a;

    private boolean overlap = false;
    private final int REQUEST_LOCATION = 11;


    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<Cart> data, pending;
    private static ArrayList<String> keys;
    private static ArrayList<Restaurant> restaurantsList;
    static View.OnClickListener myOnClickListener;
    public static CartActivity cartActivity;
    double subtotal=0;
    double delivery=3;
    double total=0;


    String payment_type, userLat,userLong,userAddress;

    private TextView subtotal_textview, delivery_textview, total_textview;
    EditText editText;
    CardView cash_payment,card_payment;
    Button place_order;
    ImageButton profile,cart,track, back;
    private String restaurant_name, restaurant_lat, restaurant_long,restaurant_address,delivery_fee;

    private String courier_assigned, courier_email, courier_timestamp;

    public  void updateValue(String value, String type){
        if(type.equals("add")){
            subtotal+=Double.parseDouble(value);
            total+=Double.parseDouble(value);
        }else{
            subtotal-=Double.parseDouble(value);
            total-=Double.parseDouble(value);
        }

        subtotal_textview.setText("RM" + String.valueOf(subtotal));
        total_textview.setText("RM" +String.valueOf(total));
    }

    public  ArrayList<Restaurant> getRestaurant(){
        return restaurantsList;
    }

    public ArrayList<String> getCartKey(){return keys; }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartActivity=this;

        courier_timestamp = "";

        if (savedInstanceState == null) {
            Intent extras = this.getIntent();
            if (extras == null) {
                restaurant = null;
                delivery_fee = null;

            } else {
                restaurant = extras.getStringExtra("restaurant_name");
                key = extras.getStringExtra("key");
                delivery_fee = extras.getStringExtra("delivery_fee");


            }
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        subtotal_textview = findViewById(R.id.sub_total);
        delivery_textview = findViewById(R.id.delivery_fee);
        total_textview = findViewById(R.id.total);
        editText=findViewById(R.id.textArea_Address);

        cash_payment=findViewById(R.id.cash_payment);
        card_payment=findViewById(R.id.card_payment);
        findViewById(R.id.cash_payment).setOnClickListener(this);
        findViewById(R.id.card_payment).setOnClickListener(this);



        layoutManager = new LinearLayoutManager(CartActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        restaurantsList=new ArrayList<Restaurant>();
        data = new ArrayList<Cart>();
        keys = new ArrayList<String>();
//        pending = new ArrayList<Event>();

//        data.add(new Plan("s","s","s","s"));
        adapter = new CartAdapterActivity(this, data, 3, keys);

        recyclerView.setAdapter(adapter);





        ref.child(user.getUid()).child("User_Information").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.child("address").exists()) {
                        editText.setText(dataSnapshot.child("address").getValue(String.class));
                        userAddress=dataSnapshot.child("address").getValue(String.class);
                    }

                    if (dataSnapshot.child("latitude").exists()) {
//                        editText.setText(dataSnapshot.child("latitude").getValue(String.class));
                        userLat=dataSnapshot.child("latitude").getValue(String.class);
                    }

                    if (dataSnapshot.child("longitude").exists()) {
//                        editText.setText(dataSnapshot.child("longitude").getValue(String.class));
                        userLong=dataSnapshot.child("longitude").getValue(String.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        //populate the adapter
        ref.child(user.getUid()).child("Cart_List").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                restaurantsList.clear();
                data.clear();
                if (dataSnapshot.exists()) {
//                    double subtotal=0;
//                    double delivery=3;
//                    double total=0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Cart key_Detail = snapshot.getValue(Cart.class);
                        subtotal+= Double.parseDouble(key_Detail.getPrice())*Double.parseDouble(key_Detail.getQuantity());

                        subtotal_textview.setText("RM" + String.valueOf(subtotal));
//                        delivery_textview.setText("RM" + delivery_fee);
                        setSetRestaurantList(key_Detail.getRestaurant());
//                        total=subtotal + Double.parseDouble(delivery_fee);
//                        total_textview.setText("RM" + String.valueOf(total));
                        keys.add(snapshot.getKey());
                        data.add(key_Detail);
                    }
                    adapter.notifyDataSetChanged();

                }
            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


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

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        back = (ImageButton) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, UserRestaurantActivity.class);
                startActivity(intent);
            }
        });

        profile = (ImageButton)findViewById(R.id.profile);

        profile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });

        cart = (ImageButton)findViewById(R.id.cart);

        cart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        track = (ImageButton)findViewById(R.id.track);

        track.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, TrackingActivity.class);
                startActivity(intent);
            }
        });

        editText.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                OpenDialog();
            }
        });

        place_order = (Button)findViewById(R.id.place_order);

        place_order.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(payment_type.equals("card")) {

                    String total_string=String.valueOf(total);
                    if(total_string.equals("")||restaurantsList.size()<1){
                        Toast.makeText(getApplicationContext(), "Restaurant not found.", Toast.LENGTH_SHORT).show();
                        return;
                    }



                    Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                    intent.putExtra("total", total_string);
                    intent.putExtra("userLong", userLong);
                    intent.putExtra("userLat", userLat);
                    intent.putExtra("userAddress", userAddress);
                    intent.putExtra("restaurant_name", restaurant_name);
                    intent.putExtra("restaurant_address", restaurant_address);
                    intent.putExtra("restaurant_long", restaurant_long);
                    intent.putExtra("restaurant_lat", restaurant_lat);
                    startActivity(intent);

                } else {

                    String total_string=String.valueOf(total);
                    if(total_string.equals("")||restaurantsList.size()<1){
                        Toast.makeText(getApplicationContext(), "Restaurant not found.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(courier_assigned)||TextUtils.isEmpty(courier_email)) {
                        Toast.makeText(getApplicationContext(), "Please try again!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Track track = new Track();
                    track.setStatus("Paid");
                    track.setTotal(total_string);
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

                    for(String key:keys){
                        ref.child(user.getUid()).child("Cart_List").child(key).removeValue();
                    }

                    ref.child("Tracking_Information").push().setValue(track, (error, ref) -> {

//                        Toast.makeText(getApplicationContext(), "Payment Done.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(CartActivity.this, TrackingActivity.class);
                        intent.putExtra("key",ref.getKey());
                        startActivity(intent);

                    });

//                    Intent intent = new Intent(CartActivity.this, TrackingActivity.class);
//                    intent.putExtra("total", total_string);
//                    intent.putExtra("userLong", userLong);
//                    intent.putExtra("userLat", userLat);
//                    intent.putExtra("userAddress", userAddress);
//                    intent.putExtra("restaurant_name", restaurant_name);
//                    intent.putExtra("restaurant_address", restaurant_address);
//                    intent.putExtra("restaurant_long", restaurant_long);
//                    intent.putExtra("restaurant_lat", restaurant_lat);
//                    startActivity(intent);


                }
            }
        });



    }

    private void OpenDialog() {
        LinearLayout layout = new LinearLayout(CartActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
        final AlertDialog a = builder.setItems(new String[]{ "Edit Address"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                intent = new Intent(CartActivity.this, AccountActivity.class);
                startActivity(intent);


            }
        }).setTitle("Edit Account Information").setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();
    }

    public void setSetRestaurantList(String name){
        boolean existed=false;
        for(Restaurant restaurant1:restaurantsList){
            if(restaurant1.getRestaurantname().equals(name)){
                existed=true;
            }
        }

        if(existed){
            return;
        }

        ref.child("Restaurant_List").orderByChild("restaurantname").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Restaurant restaurant = snapshot.getValue(Restaurant.class);
                        restaurantsList.add(restaurant);

                        if(restaurant!=null){
                            restaurant_name=restaurant.getRestaurantname();
                            restaurant_lat=restaurant.getLatitude();
                            restaurant_long=restaurant.getLongitude();
                            restaurant_address=restaurant.getAddress();
                            delivery_fee=restaurant.getDelivery_fee();

                            total=subtotal + Double.parseDouble(delivery_fee);
                            delivery_textview.setText("RM" + delivery_fee);
                            total_textview.setText("RM" + String.valueOf(total));
                        }

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
        Intent i;
        switch (v.getId()) {
            case R.id.cash_payment:
                cash_payment.setCardBackgroundColor(Color.LTGRAY);
                card_payment.setCardBackgroundColor(Color.WHITE);
                payment_type="cash";


                break;
            case R.id.card_payment:
                card_payment.setCardBackgroundColor(Color.LTGRAY);
                cash_payment.setCardBackgroundColor(Color.WHITE);
                payment_type="card";


                break;

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