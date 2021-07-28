package com.example.foodorderingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class RestaurantAdapterActivity extends RecyclerView.Adapter<RestaurantAdapterActivity.MyViewHolder> {

    private static final String TAG ="error" ;
    private Activity activity;
    private ArrayList<Restaurant> dataSet;
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
    private final PlacesClient placesClient;





    public static class MyViewHolder extends RecyclerView.ViewHolder {



        TextView restaurant_name;
        TextView restaurant_address;
        TextView restaurant_cuisine;
        TextView delivery_fee;
        TextView budget;
        RelativeLayout relative;
        ImageView restaurant_image;






        public MyViewHolder(View itemView) {
            super(itemView);

            this.restaurant_name = (TextView) itemView.findViewById(R.id.restaurant_name);
//            this.restaurant_address = (TextView) itemView.findViewById(R.id.restaurant_address);
            this.restaurant_cuisine = (TextView) itemView.findViewById(R.id.restaurant_cuisine);
            this.budget = (TextView) itemView.findViewById(R.id.budget);
            this.delivery_fee = (TextView) itemView.findViewById(R.id.delivery_fee);
            this.relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            this.restaurant_image = (ImageView) itemView.findViewById(R.id.restaurant_image);



        }
    }

    public RestaurantAdapterActivity(Activity activity,ArrayList<Restaurant> data,int showbutton, ArrayList<String> key) {

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
    public RestaurantAdapterActivity.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_restaurant_adapter, parent, false);

        RestaurantAdapterActivity.MyViewHolder myViewHolder = new RestaurantAdapterActivity.MyViewHolder(view);
        return myViewHolder;
    }



    @Override
    public void onBindViewHolder(RestaurantAdapterActivity.MyViewHolder holder, final int listPosition) {

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
        TextView delivery_fee = holder.delivery_fee;
        RelativeLayout relative = holder.relative;
        ImageView restaurant_image =holder.restaurant_image;



        //set value into textview
        restaurant_name.setText(dataSet.get(listPosition).getRestaurantname());
        restaurant_cuisine.setText(" Cuisine: " +String.valueOf(dataSet.get(listPosition).getCuisine()));
        budget.setText(" Budgets: RM"+String.valueOf(dataSet.get(listPosition).getBudget()));
        delivery_fee.setText(" Delivery fees: RM"+String.valueOf(dataSet.get(listPosition).getDelivery_fee()));
        newfunction(restaurant_image ,dataSet.get(listPosition).getPlaceid());



        final Intent intent=new Intent(activity,MenuActivity.class);;

        relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new Intent(activity, BookingActivity.class);
                intent.putExtra("key", key.get(listPosition));
                intent.putExtra("restaurant_name", dataSet.get(listPosition).getRestaurantname());
                intent.putExtra("restaurant_address", dataSet.get(listPosition).getAddress());
                intent.putExtra("restaurant_cuisine", dataSet.get(listPosition).getAddress());
                intent.putExtra("budget", dataSet.get(listPosition).getBudget());
                intent.putExtra("lat", dataSet.get(listPosition).getLatitude());
                intent.putExtra("long", dataSet.get(listPosition).getLongitude());
                activity.startActivity(intent);


            }
        });
    }

    private void setPhotoByPlaceId(final ImageView imageView, String placeId, int position) {
        String photo = "maxwidth=400&maxheight=400&photoreference="+placeId;

        String key = "key=" + "AIzaSyBXpng1P3Tznd1SzjO8_0njGEooSdamrvk";
        // Building the parameters to the web service
        String parameters = photo+"&"+ key;
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/photo?" + parameters;
        Log.e("here url", url);
        new DownloadImageTask(imageView, url,position).execute();

    }

    private void newfunction(final ImageView imageView, String id){
        // Define a Place ID.

// Specify fields. Requests for photos must always have the PHOTO_METADATAS field.
        final List<Place.Field> fields = Collections.singletonList(Place.Field.PHOTO_METADATAS);

// Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
        final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(id, fields);

        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
            final Place place = response.getPlace();

            // Get the photo metadata.
            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            if (metadata == null || metadata.isEmpty()) {
                Log.w(TAG, "No photo metadata.");
                return;
            }
            final PhotoMetadata photoMetadata = metadata.get(0);

            // Get the attribution text.
            final String attributions = photoMetadata.getAttributions();

            // Create a FetchPhotoRequest.
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500) // Optional.
                    .setMaxHeight(300) // Optional.
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                imageView.setImageBitmap(bitmap);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                    // TODO: Handle error with given status code.
                }
            });
        });
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        ImageView bmImage;
        String urlString;
        int position;

        public DownloadImageTask(ImageView bmImage, String url, int position) {
            this.bmImage = bmImage;
            this.urlString=url;
            this.position=position;
        }

        protected Bitmap doInBackground(String... voids) {
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlString).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

            if(result!=null)
                bmImage.setImageBitmap(result);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }



}
