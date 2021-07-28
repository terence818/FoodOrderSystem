package com.example.foodorderingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserRestaurantActivity extends AppCompatActivity {

    private Intent intent;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ValueEventListener a;


    protected LocationManager locationManager;
    private Location Location_cur;
    private final int REQUEST_LOCATION = 11;


    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<Restaurant> data, data2;
    private static ArrayList<String> key;
    static View.OnClickListener myOnClickListener;


    ImageButton profile, cart, filter, track, back;
    TextView location, textViewScore;
    EditText filter_key;
    String[] listItems;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();
    ArrayList<String> mItem = new ArrayList<>();
    private int limit = 1000000000;
    String Fullitem = "", Budget;
//    public static UserRestaurantActivity userRestaurantActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_restaurant);

        listItems = getResources().getStringArray(R.array.cuisine_item);
        checkedItems = new boolean[listItems.length];

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        filter_key = findViewById(R.id.filter_key);

        recyclerView = (RecyclerView) findViewById(R.id.menu_recycler);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(UserRestaurantActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<Restaurant>();
        data2 = new ArrayList<Restaurant>();

        key = new ArrayList<String>();
        adapter = new UserRestaurantAdapter(this, data, 3, key);

        recyclerView.setAdapter(adapter);

        location = findViewById(R.id.textView3);

        filter_key.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                String queary = s.toString();


                if (data2.size() > 0 && !queary.equals("")) {
                    data.clear();
                    for (int i = 0; i < data2.size(); i++) {
                        if (data2.get(i).getRestaurantname().toLowerCase().contains(queary.toLowerCase())) {
                            data.add(data2.get(i));
                        }
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    data.clear();
                    data.addAll(data2);
                    adapter.notifyDataSetChanged();
                }

            }


            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        ref.child(user.getUid()).child("User_Information").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.child("address").exists()) {
                        location.setText(dataSnapshot.child("address").getValue(String.class));
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        //populate the adapter
        ref.child("Restaurant_List").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                data.clear();
                data2.clear();
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Restaurant key_Detail = snapshot.getValue(Restaurant.class);
                        key.add(snapshot.getKey());
                        data.add(key_Detail);
                        data2.add(key_Detail);

                    }

                    adapter.notifyDataSetChanged();

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
                Intent intent = new Intent(UserRestaurantActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        profile = (ImageButton) findViewById(R.id.profile);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserRestaurantActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });

        cart = (ImageButton) findViewById(R.id.cart);

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserRestaurantActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        track = (ImageButton) findViewById(R.id.track);

        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserRestaurantActivity.this, TrackingActivity.class);
                startActivity(intent);
            }
        });

        filter = (ImageButton) findViewById(R.id.filter);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LinearLayout layout = new LinearLayout(getBaseContext());
                limit = 1000000000;
                layout.setOrientation(LinearLayout.VERTICAL);


                AlertDialog.Builder mBuilder = new AlertDialog.Builder(UserRestaurantActivity.this);
                final TextView tv = new TextView(UserRestaurantActivity.this);
                tv.setText("Budget");
                tv.setTextSize(20);

                layout.setPadding(10, 10, 10, 10);
                final SeekBar seek = new SeekBar(UserRestaurantActivity.this);

                seek.setMax(100);
                seek.setKeyProgressIncrement(1);
//                mBuilder.setMessage("Budget");
//                mBuilder.addView(tv);
                layout.addView(tv);
                layout.addView(seek);
                mBuilder.setView(layout);

                mBuilder.setTitle(R.string.dialog_title);
                mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
//                        if (isChecked) {
//                            if (!mUserItems.contains(position)) {
//                                mUserItems.add(position);
//                            }
//                        } else if (mUserItems.contains(position)) {
//                            mUserItems.remove(position);
//                        }
                        if (isChecked) {

                            mUserItems.add(position);
                            mItem.add(listItems[position]);


                        } else {
                            mUserItems.remove((Integer.valueOf(position)));
                            mItem.remove(listItems[position]);
                        }
                    }
                });


                seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        int point = progress;
                        limit = progress;
                        tv.setText("Budget: RM " + point);

//                        txtView.setText("Value of : " + progress);
                    }


                    public void onStartTrackingTouch(SeekBar arg0) {
                        //do something


                    }


                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //do something

                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        Fullitem = "";
                        for (int i = 0; i < mUserItems.size(); i++) {

//                            String item = listItems[mUserItems.get(i)];
                            Fullitem += listItems[mUserItems.get(i)];
                            if (i != mUserItems.size() - 1) {
                                Fullitem += ",";
//                                FilterCuisine();
                            }
                        }

                        FilterCuisine();

                    }
                });

                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            mUserItems.clear();
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });


    }

    private void FilterCuisine() {
        if (data2.size() > 0 && !Fullitem.equals("")) {
            data.clear();

            String[] item2 = Fullitem.split(",");

            for (int i = 0; i < data2.size(); i++) {


                for (String s : item2) {
                    if (data2.get(i).getCuisine().toLowerCase().contains(s.toLowerCase().replace(",", ""))) {
                        try {
                            if (Integer.parseInt(data2.get(i).getBudget()) <= limit) {
                                data.add(data2.get(i));
                            }
                        } catch (NumberFormatException e) {
                            Log.i("Number exception", "INVALID BUDGET!");
                        }
                    }

                }

            }

            adapter.notifyDataSetChanged();
        } else if (data2.size() > 0) {
            data.clear();
            for (int i = 0; i < data2.size(); i++) {

                try {
                    if (Integer.parseInt(data2.get(i).getBudget()) <= limit) {
                        data.add(data2.get(i));
                    }
                } catch (NumberFormatException e) {
                    Log.i("Number exception", "INVALID BUDGET!");
                }

            }
            Log.i("hey", data.toString());
//            data.addAll(data2);
            adapter.notifyDataSetChanged();
        } else {
            data.clear();
            data.addAll(data2);
            adapter.notifyDataSetChanged();
        }
    }


}