package com.example.foodorderingsystem;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class UserMenuAdapter extends RecyclerView.Adapter<UserMenuAdapter.MyViewHolder> {

    private Activity activity;
    private ArrayList<Food> dataSet;
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
    String restaurant,delivery_fee;



    public static class MyViewHolder extends RecyclerView.ViewHolder {



        TextView food_name;
        TextView price;
        TextView allergy;
        ConstraintLayout relative;
        ImageView food_image;






        public MyViewHolder(View itemView) {
            super(itemView);

            this.food_name = (TextView) itemView.findViewById(R.id.food_name);
            this.price = (TextView) itemView.findViewById(R.id.price);
            this.allergy = (TextView) itemView.findViewById(R.id.allergy);
            this.relative = (ConstraintLayout) itemView.findViewById(R.id.relative);
            this.food_image = (ImageView) itemView.findViewById(R.id.food_image);



        }
    }

    public UserMenuAdapter(Activity activity,ArrayList<Food> data,String restaurant, ArrayList<String> key, String delivery_fee) {
        this.activity = activity;
        this.restaurant = restaurant;
        this.delivery_fee = delivery_fee;
        this.dataSet = data;
        this.key=key;

    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public void setDelivery_fee(String delivery_fee) {
        this.delivery_fee = delivery_fee;
    }

    @Override
    public UserMenuAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_user_menu_adapter, parent, false);

        UserMenuAdapter.MyViewHolder myViewHolder = new UserMenuAdapter.MyViewHolder(view);
        return myViewHolder;
    }



    @Override
    public void onBindViewHolder(UserMenuAdapter.MyViewHolder holder, final int listPosition) {

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
        TextView allergy = holder.allergy;
        ConstraintLayout relative = holder.relative;
        ImageView food_image = holder.food_image;



        //set value into textview
        food_name.setText(dataSet.get(listPosition).getFood());
        price.setText(" RM" +String.valueOf(dataSet.get(listPosition).getPrice()));
        allergy.setText(" Allergy: " +String.valueOf(dataSet.get(listPosition).getAllergy()));


        //set imageview
        food_image.setImageResource(R.drawable.popular1);
        if(!String.valueOf(dataSet.get(listPosition).getUrl_image()).equals("")){
            if(dataSet.get(listPosition).getBitmap() != null) {
                food_image.setImageBitmap(dataSet.get(listPosition).getBitmap());
            } else {
                new UserMenuAdapter.getAndSetImage(String.valueOf(dataSet.get(listPosition).getUrl_image()), holder, dataSet.get(listPosition)).execute();
            }
        }



        final Intent intent=new Intent(activity,FoodDetails.class);;

        relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                intent.putExtra("name", dataSet.get(listPosition).getFood());
                intent.putExtra("price", dataSet.get(listPosition).getPrice());
                intent.putExtra("allergy", dataSet.get(listPosition).getAllergy());
                intent.putExtra("image", dataSet.get(listPosition).getUrl_image());
                intent.putExtra("restaurant_name", restaurant);
                intent.putExtra("delivery_fee", delivery_fee);
                activity.startActivity(intent);



            }
        });
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

        Food recentHistory;

        public getAndSetImage(String url, final UserMenuAdapter.MyViewHolder holder, Food recentHistory){
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
            UserMenuAdapter.this.notifyDataSetChanged();
            if(bmp!=null) {
                food_image.setImageBitmap(bmp);
            }else{
                food_image.setImageResource(R.drawable.popular1);
            }
        }
    }



}