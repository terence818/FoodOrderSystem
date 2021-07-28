package com.example.foodorderingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserMenuActivity extends AppCompatActivity {

    private String restaurant, key, delivery_fee;
    private Intent intent;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ValueEventListener a;


    private final int REQUEST_LOCATION = 11;


    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<Food> data,data2;
    private static ArrayList<String> keys;
    static View.OnClickListener myOnClickListener;

//    public static UserMenuActivity cartActivity;

    ImageButton profile,cart,track, filter, back;

    TextView restaurant_name;

    EditText filter_key;

    String[] listItems;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();
    ArrayList<String> mItem = new ArrayList<>();
    String Fullitem = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);

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

        restaurant_name=findViewById(R.id.textView);
        restaurant_name.setText(restaurant);

        listItems = getResources().getStringArray(R.array.allergy_item);
        checkedItems = new boolean[listItems.length];


        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        filter_key = findViewById(R.id.filter_key);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(UserMenuActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<Food>();
        data2 = new ArrayList<Food>();
        keys = new ArrayList<String>();
//        pending = new ArrayList<Event>();

//        data.add(new Plan("s","s","s","s"));
        adapter = new UserMenuAdapter(this, data, restaurant, keys, delivery_fee);

        recyclerView.setAdapter(adapter);

        back = (ImageButton) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserMenuActivity.this, UserRestaurantActivity.class);
                startActivity(intent);
            }
        });

        profile = (ImageButton) findViewById(R.id.profile);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserMenuActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });

        cart = (ImageButton) findViewById(R.id.cart);

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserMenuActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        track = (ImageButton) findViewById(R.id.track);

        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserMenuActivity.this, TrackingActivity.class);
                startActivity(intent);
            }
        });

        filter_key.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                String queary = s.toString();


                if (data2.size() > 0 && !queary.equals("")) {
                    data.clear();
                    for (int i = 0; i < data2.size(); i++) {
                        if (data2.get(i).getFood().toLowerCase().contains(queary.toLowerCase())) {
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

        filter = (ImageButton) findViewById(R.id.filter);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LinearLayout layout = new LinearLayout(getBaseContext());

                layout.setOrientation(LinearLayout.VERTICAL);


                AlertDialog.Builder mBuilder = new AlertDialog.Builder(UserMenuActivity.this);
                final SeekBar seek = new SeekBar(UserMenuActivity.this);

//                mBuilder.setMessage("Budget");
//                mBuilder.addView(tv);

                mBuilder.setView(layout);

                mBuilder.setTitle(R.string.dialog_filter_allergy);
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

                        FilterAllergy();

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






    //populate the adapter
        ref.child("Restaurant_List").child(key).child("menu_list").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                data.clear();
                data2.clear();
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Food key_Detail = snapshot.getValue(Food.class);
                        keys.add(snapshot.getKey());
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
    }

    private void  FilterAllergy() {
        if (data2.size() > 0 && !Fullitem.equals("")) {
            data.clear();

            String[] item2=Fullitem.split(",");

            for (int i = 0; i < data2.size(); i++) {


                for(int j=0;j<item2.length;j++){
                    if (data2.get(i).getAllergy().toLowerCase().contains(item2[j].toLowerCase().replace(",",""))) {
                        data.add(data2.get(i));

                    }
                }
            }

            adapter.notifyDataSetChanged();
        } else {
            data.clear();
            data.addAll(data2);
            adapter.notifyDataSetChanged();
        }
    }

}