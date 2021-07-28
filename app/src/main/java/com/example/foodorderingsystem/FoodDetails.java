package com.example.foodorderingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FoodDetails extends AppCompatActivity {

    // now we will get data from intent and set to UI


    private DatabaseReference logReference;
    private FirebaseAuth auth;
    private DatabaseReference ref;
    private FirebaseUser User;
    private String currentUserID;

    ImageView imageView;
    TextView itemName, itemPrice, itemRating;
    RatingBar ratingBar;
    ImageButton back,profile,cart,track;
    Button add;

    String name, price, rating, imageUrl,quantity,restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        Intent intent = getIntent();


        auth = FirebaseAuth.getInstance();
        User = auth.getCurrentUser();

        if (auth.getCurrentUser() != null) {
            currentUserID = auth.getCurrentUser().getUid();
        } else {
            currentUserID = null;
        }
        ref = FirebaseDatabase.getInstance().getReference();

        name = intent.getStringExtra("name");
        price = intent.getStringExtra("price");
        rating = intent.getStringExtra("rating");
        imageUrl = intent.getStringExtra("image");
        restaurant = intent.getStringExtra("restaurant_name");

        imageView = findViewById(R.id.imageView5);
        itemName = findViewById(R.id.name);
        itemPrice = findViewById(R.id.price);
//        itemRating = findViewById(R.id.rating);
//        ratingBar = findViewById(R.id.ratingBar);

        Glide.with(getApplicationContext()).load(imageUrl).into(imageView);
        itemName.setText("Name: " + name);
        itemPrice.setText("RM "+price);
//        itemRating.setText(rating);
//        ratingBar.setRating(Float.parseFloat(rating));



        ElegantNumberButton button = (ElegantNumberButton) findViewById(R.id.number_button);
        button.setOnClickListener(new ElegantNumberButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = button.getNumber();
            }
        });



        back = (ImageButton)findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodDetails.this, UserMenuActivity.class);
                startActivity(intent);
            }
        });

        profile = (ImageButton)findViewById(R.id.profile);

        profile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodDetails.this, AccountActivity.class);
                startActivity(intent);
            }
        });

        cart = (ImageButton)findViewById(R.id.cart);

        cart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodDetails.this, CartActivity.class);
                startActivity(intent);
            }
        });

        add = (Button)findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setCart();
            }
        });

        track = (ImageButton)findViewById(R.id.track);

        track.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodDetails.this, TrackingActivity.class);
                startActivity(intent);
            }
        });



    }

    public void setCart() {

        double prices = Double.parseDouble(price);
        double number = Double.parseDouble(quantity);
        double itemprice=prices*number;

        String item_price = String.valueOf(itemprice);


        Cart cart = new Cart();
        cart.setFood(name);
        cart.setPrice(item_price);
        cart.setQuantity(quantity);
        cart.setUrl_image(imageUrl);
        cart.setRestaurant(restaurant);



        ref.child(User.getUid()).child("Cart_List").push().setValue(cart, new DatabaseReference.CompletionListener() {
            public void onComplete(DatabaseError error, @NonNull DatabaseReference ref) {

                Toast.makeText(getApplicationContext(), "Item Added to Cart Successfully.", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
