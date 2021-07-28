package com.example.foodorderingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.foodorderingsystem.adapter.AllMenuAdapter;
import com.example.foodorderingsystem.adapter.PopularAdapter;
import com.example.foodorderingsystem.adapter.RecommendedAdapter;
import com.example.foodorderingsystem.model.Allmenu;
import com.example.foodorderingsystem.model.FoodData;
import com.example.foodorderingsystem.model.Popular;
import com.example.foodorderingsystem.model.Recommended;
import com.example.foodorderingsystem.retrofit.ApiInterface;
import com.example.foodorderingsystem.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ApiInterface apiInterface;

    RecyclerView popularRecyclerView, recommendedRecyclerView, allMenuRecyclerView;

    PopularAdapter popularAdapter;
    RecommendedAdapter recommendedAdapter;
    AllMenuAdapter allMenuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);

        Call<List<Allmenu>> call = apiInterface.getAllData();
        Call<List<Recommended>> call2 = apiInterface.getAllRec();
        Call<List<Popular>> call3 = apiInterface.getAllPopular();

        call.enqueue(new Callback<List<Allmenu>>() {
            @Override
            public void onResponse(Call<List<Allmenu>> call, Response<List<Allmenu>> response) {
                List<Allmenu> foodDataList = response.body();
                getAllMenu(foodDataList);
            }

            @Override
            public void onFailure(Call<List<Allmenu>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Server is not responding.", Toast.LENGTH_SHORT).show();
            }
        });

//        call2.enqueue(new Callback<List<Recommended>>() {
//            @Override
//            public void onResponse(Call<List<Recommended>> call, Response<List<Recommended>> response) {
//                List<Recommended> foodDataList = response.body();
//                getRecommendedData(foodDataList);
//            }

//            @Override
//            public void onFailure(Call<List<Recommended>> call, Throwable t) {
//                Toast.makeText(MainActivity.this, "Server is not responding.", Toast.LENGTH_SHORT).show();
//            }
//        });

//        call3.enqueue(new Callback<List<Popular>>() {
//            @Override
//            public void onResponse(Call<List<Popular>> call, Response<List<Popular>> response) {
//                List<Popular> foodDataList = response.body();
//                getPopularData(foodDataList);
//            }

//            @Override
//            public void onFailure(Call<List<Popular>> call, Throwable t) {
//                Toast.makeText(MainActivity.this, "Server is not responding.", Toast.LENGTH_SHORT).show();
//            }
//        });




    }

//    private void  getPopularData(List<Popular> popularList){
//
//        popularRecyclerView = findViewById(R.id.popular_recycler);
//        popularAdapter = new PopularAdapter(this, popularList);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        popularRecyclerView.setLayoutManager(layoutManager);
//        popularRecyclerView.setAdapter(popularAdapter);
//
//    }
//
//    private void  getRecommendedData(List<Recommended> recommendedList){
//
//        recommendedRecyclerView = findViewById(R.id.recommended_recycler);
//        recommendedAdapter = new RecommendedAdapter(this, recommendedList);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        recommendedRecyclerView.setLayoutManager(layoutManager);
//        recommendedRecyclerView.setAdapter(recommendedAdapter);
//
//    }

    private void  getAllMenu(List<Allmenu> allmenuList){

        allMenuRecyclerView = findViewById(R.id.all_menu_recycler);
        allMenuAdapter = new AllMenuAdapter(this, allmenuList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        allMenuRecyclerView.setLayoutManager(layoutManager);
        allMenuRecyclerView.setAdapter(allMenuAdapter);
        allMenuAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Intent intent;

        int id = item.getItemId();

        if (id == R.id.logout) {
            intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }  else if(id == R.id.profile) {
            intent = new Intent(MainActivity.this, AccountActivity.class);
            startActivity(intent);
        } else {
            intent = new Intent(MainActivity.this, OrderActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
