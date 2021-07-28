package com.example.foodorderingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class CartAdapterActivity extends RecyclerView.Adapter<CartAdapterActivity.MyViewHolder> {

    private Activity activity;
    private ArrayList<Cart> dataSet;
    private ArrayList<String> key;
    private Location Location_cur;
    private int show_button = 1;
    private FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
    private DatabaseReference ref= firebaseDatabase.getReference();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private String currentUserID;
    private ValueEventListener a;
    int row_index=-1;



    public static class MyViewHolder extends RecyclerView.ViewHolder {



        TextView food_name;
        TextView price;
        ImageView food_image;
        ElegantNumberButton quantity;
        Button delete;






        public MyViewHolder(View itemView) {
            super(itemView);

            this.food_name = (TextView) itemView.findViewById(R.id.food_name);
            this.price = (TextView) itemView.findViewById(R.id.price);
            this.food_image = (ImageView) itemView.findViewById(R.id.food_image);
            this.quantity = (ElegantNumberButton)itemView.findViewById(R.id.number_button);
            this.delete = (Button) itemView.findViewById(R.id.delete);



        }
    }

    public CartAdapterActivity(Activity activity,ArrayList<Cart> data,int showbutton, ArrayList<String> key) {
        this.activity = activity;
        show_button = showbutton;
        this.dataSet = data;
        this.key=key;

    }

    @Override
    public CartAdapterActivity.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_cart_adapter, parent, false);

        CartAdapterActivity.MyViewHolder myViewHolder = new CartAdapterActivity.MyViewHolder(view);
        return myViewHolder;
    }



    @Override
    public void onBindViewHolder(CartAdapterActivity.MyViewHolder holder, final int listPosition) {

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (auth.getCurrentUser() != null) {
            currentUserID = auth.getCurrentUser().getUid();
        } else {
            currentUserID = null;
        }
        ref = FirebaseDatabase.getInstance().getReference();


        TextView food_name = holder.food_name;
        TextView price = holder.price;

        ImageView food_image = holder.food_image;
        ElegantNumberButton quantity = holder.quantity;

        Button delete = holder.delete;



        //set value into textview
        food_name.setText(dataSet.get(listPosition).getFood());
        price.setText(" RM" +String.valueOf(dataSet.get(listPosition).getPrice()));
        quantity.setNumber(dataSet.get(listPosition).getQuantity());
        Log.d("888",dataSet.get(listPosition).getQuantity());

        quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                quantity.setNumber(String.valueOf(newValue));
                if(CartActivity.cartActivity!=null){
                    if(oldValue<newValue){
                        CartActivity.cartActivity.updateValue(dataSet.get(listPosition).getPrice(), "add");
                    }else{
                        CartActivity.cartActivity.updateValue(dataSet.get(listPosition).getPrice(),"minus");
                    }

                }
                Log.d("23", String.format("oldValue: %d   newValue: %d", oldValue, newValue));
            }
        });


        //set imageview
        food_image.setImageResource(R.drawable.popular1);
        if(!String.valueOf(dataSet.get(listPosition).getUrl_image()).equals("")){
            if(dataSet.get(listPosition).getBitmap() != null) {
                food_image.setImageBitmap(dataSet.get(listPosition).getBitmap());
            } else {
                new CartAdapterActivity.getAndSetImage(String.valueOf(dataSet.get(listPosition).getUrl_image()), holder, dataSet.get(listPosition)).execute();
            }
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                a = new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        if (dataSnapshot.exists()) {
//                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                for (DataSnapshot areaSnapshot : snapshot.child("food").getChildren()) {
//                                    if(areaSnapshot.child("food").getValue(String.class).equals(dataSet.get(listPosition).getFood())){
//                                        areaSnapshot.getRef().removeValue();
//                                    }
//
//                                }
//
//
//                            }
//
//                        }
//                    }
//
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                    }
//                };
                ref.child(user.getUid()).child("Cart_List").child(key.get(listPosition)).removeValue();



            }


        });



        final Intent intent=new Intent(activity,FoodDetails.class);;


    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    private class getAndSetImage extends AsyncTask<String,Void,Void> {

        Bitmap bmp;

        URL url;
        String urlString;
        ImageView food_image;

        Cart recentHistory;

        public getAndSetImage(String url, final MyViewHolder holder, Cart recentHistory){
            urlString = url;
            food_image=holder.food_image;
            this.recentHistory = recentHistory;
        }

        @Override
        protected Void doInBackground(String... voids) {


            //get image from url and set imageview
            try {
                url = new URL(urlString);
                recentHistory.setBitmap(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
            }catch(MalformedURLException e)
            {
                e.printStackTrace();

            }catch(IOException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            CartAdapterActivity.this.notifyDataSetChanged();
            if(bmp!=null) {
                food_image.setImageBitmap(bmp);
            }else{
                food_image.setImageResource(R.drawable.popular1);
            }
        }
    }



}