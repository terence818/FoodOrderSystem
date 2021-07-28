package com.example.foodorderingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeliveryActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String email;
    private int sec = 5;
    private boolean declined = false;

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<Track> data, data2;
    private static ArrayList<String> keys;
    static View.OnClickListener myOnClickListener;

//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            if (dialog.isShowing() && msg.what <= 0) {
//                if (!declined) {
//                    ref.child("Tracking_Information").child(key).child("status").setValue("Decline");
//                }
//                dialog.dismiss();
//                return;
//            }
//            sec--;
//            dialogButton.setText("Ok (" + msg.what + ")");
//
//            handler.sendEmptyMessageDelayed(msg.what - 1, 1000);
//        }
//    };
//    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        recyclerView = (RecyclerView) findViewById(R.id.menu_recycler);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(DeliveryActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<Track>();
//        data2 = new ArrayList<Restaurant>();

        keys = new ArrayList<String>();
        adapter = new DeliveryAdapter(this, data, 3, keys);

        recyclerView.setAdapter(adapter);

        if (savedInstanceState == null) {
            Intent extras = this.getIntent();
            if (extras == null) {
                email = "";
            } else {
                email = extras.getStringExtra("email");
            }

        }

        if (email != null)
            ref.child("Tracking_Information").orderByChild("courier_email").equalTo(email).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    data.clear();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Track track = snapshot.getValue(Track.class);

//                            Restaurant key_Detail = snapshot.getValue(Restaurant.class);


                            if(track!=null&&!track.getStatus().equals("Decline")){
                                keys.add(snapshot.getKey());
                                data.add(track);
                            }

                            if (snapshot.getKey() != null) {
                                if (track != null)
                                    createDialog(track, snapshot.getKey());

                            }


                        }

                        adapter.notifyDataSetChanged();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

    }

    Dialog dialog;
    Button dialogButton;
    String key;

    private void createDialog(Track track, String key) {
        if (track != null && !track.getStatus().equals("Paid")) {
            return;
        }
        this.key = key;

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog);

        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);

        dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (track != null && track.getStatus().equals("Paid")) {


                    if (dialog.isShowing()) {
                        dialog.dismiss();
                        dialog.cancel();
                    }

                    Intent intent = new Intent(DeliveryActivity.this, CourierTrackingActivity.class);
                    intent.putExtra("key", key);
                    startActivity(intent);

                }
            }
        });


        if (!((Activity) this).isFinishing()) {
            dialog.show();
        }



    }

    protected void onStop() {
        super.onStop();

    }
}