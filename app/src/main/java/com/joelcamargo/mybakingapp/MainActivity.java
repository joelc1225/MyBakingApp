package com.joelcamargo.mybakingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.joelcamargo.mybakingapp.data.APIInterface;
import com.joelcamargo.mybakingapp.data.APIUtils;
import com.joelcamargo.mybakingapp.model.Recipe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @BindView(R.id.mainToolBar) Toolbar mToolBar;
    RecipeAdapter mRecipeAdapter;
    LinearLayoutManager mLinearLayoutManager;
    ArrayList<Recipe> mRecipeList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mToolBar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolBar);

        // creates an empty arrayList that we'll update after the api call
        mRecipeList = new ArrayList<>();
        mProgressBar.setVisibility(View.VISIBLE);

        // applies the settings for our layoutManager and sets it on the recyclerView
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        // creates the adapter and sets it onto the recyclerView
        mRecipeAdapter = new RecipeAdapter(mRecipeList, this);
        mRecyclerView.setAdapter(mRecipeAdapter);

        // Sets up our retrofitClient and makes the GET call to the api
        APIInterface retrofitClient = APIUtils.getApiClient();
        Call<ArrayList<Recipe>> call = retrofitClient.getRecipes();

        call.enqueue(new Callback<ArrayList<Recipe>>() {
            // If response is OK, onResponse code runs.
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {

                mProgressBar.setVisibility(View.INVISIBLE);

                // using the response from the api, update the list in the adapter with new data
                // then notify the adapter to show on UI
                mRecipeList = response.body();
                mRecipeAdapter = new RecipeAdapter(mRecipeList, getApplicationContext());
                mRecyclerView.setAdapter(mRecipeAdapter);
                mRecipeAdapter.notifyDataSetChanged();
            }

            // If api call fails, toast and log code runs. Fill empty view??
            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {

                // TODO empty view??
                Toast.makeText(MainActivity.this, "Call Failed",
                        Toast.LENGTH_LONG).show();

                Log.d(TAG, "CALL FAILED!!!!");
                Log.d(call.toString(), t.getLocalizedMessage());
            }
        });
    }
}
