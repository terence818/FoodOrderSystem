package com.example.foodorderingsystem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DeliveryAdapter  extends RecyclerView.Adapter<DeliveryAdapter.MyViewHolder> {

private static final String TAG ="error" ;
private Activity activity;
private ArrayList<Track> dataSet;
private ArrayList<String> key;
private int show_button = 1;
private FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
private DatabaseReference ref= firebaseDatabase.getReference();
private FirebaseAuth auth = FirebaseAuth.getInstance();
private FirebaseUser user = auth.getCurrentUser();
private String currentUserID;
private ValueEventListener a;
        int row_index=-1;
private final PlacesClient placesClient;




public static class MyViewHolder extends RecyclerView.ViewHolder {



    TextView restaurant_name;
    TextView restaurant_address;
    TextView restaurant_cuisine;
    TextView budget;
    RelativeLayout relative;
    ImageView restaurant_image;






    public MyViewHolder(View itemView) {
        super(itemView);

        this.restaurant_name = (TextView) itemView.findViewById(R.id.restaurant_name);
//            this.restaurant_address = (TextView) itemView.findViewById(R.id.restaurant_address);
        this.restaurant_cuisine = (TextView) itemView.findViewById(R.id.restaurant_cuisine);
        this.budget = (TextView) itemView.findViewById(R.id.budget);
        this.relative = (RelativeLayout) itemView.findViewById(R.id.relative);
        this.restaurant_image = (ImageView) itemView.findViewById(R.id.restaurant_image);



    }
}

    public DeliveryAdapter(Activity activity, ArrayList<Track> data, int showbutton, ArrayList<String> key) {

        if (!Places.isInitialized()) {
            Places.initialize(activity.getApplicationContext(), activity.getString(R.string.google_api_key), Locale.US);
        }
        this.activity = activity;
        show_button = showbutton;
        this.dataSet = data;
        this.key=key;

        placesClient=com.google.android.libraries.places.api.Places.createClient(activity);
    }

    @Override
    public DeliveryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_restaurant_adapter, parent, false);

        DeliveryAdapter.MyViewHolder myViewHolder = new DeliveryAdapter.MyViewHolder(view);
        return myViewHolder;
    }



    @Override
    public void onBindViewHolder(DeliveryAdapter.MyViewHolder holder, final int listPosition) {

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (auth.getCurrentUser() != null) {
            currentUserID = auth.getCurrentUser().getUid();
        } else {
            currentUserID = null;
        }
        ref = FirebaseDatabase.getInstance().getReference();


        TextView restaurant_name = holder.restaurant_name;
//        TextView restaurant_address = holder.restaurant_address;
        TextView restaurant_cuisine = holder.restaurant_cuisine;
        TextView budget = holder.budget;

        RelativeLayout relative = holder.relative;
        ImageView restaurant_image =holder.restaurant_image;
        if(dataSet.get(listPosition).getStatus().equals("Completed")){
            restaurant_image.setImageResource(R.drawable.greentick);
        }else{
            restaurant_image.setImageResource(R.drawable.pending);
        }





        //set value into textview
        restaurant_name.setText(dataSet.get(listPosition).getRestaurant_name());
        restaurant_cuisine.setText(String.valueOf(dataSet.get(listPosition).getBuyer_address()));
        budget.setText("$"+dataSet.get(listPosition).getTotal());


        final Intent intent=new Intent(activity,CourierTrackingActivity.class);;

        relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new Intent(activity, BookingActivity.class);
                intent.putExtra("key", key.get(listPosition));
                activity.startActivity(intent);


            }
        });
    }




    @Override
    public int getItemCount() {
        return dataSet.size();
    }



}

