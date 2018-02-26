package com.joelcamargo.mybakingapp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    // boolean to check for two pane mode
    public static boolean mTwoPane;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.mainToolBar)
    Toolbar mToolBar;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.empty_view)
    TextView mEmptyView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.retry_button)
    Button mRetryButton;
    private RecipeAdapter mRecipeAdapter;
    private ArrayList<Recipe> mRecipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check if in two pane mode
        mTwoPane = findViewById(R.id.two_pane_layout) != null;
        Log.d("onCreate,Two Pane Mode?", " " + mTwoPane);

        // forces landscape layout if in two pane mode
        if (mTwoPane) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        // If recipe list is in memory from last app launch, dont make API call
        GridLayoutManager mGridLayoutManager;
        LinearLayoutManager mLinearLayoutManager;
        if (savedInstanceState != null) {

            // sets up views to inflate
            ButterKnife.bind(this);

            mProgressBar.setVisibility(View.INVISIBLE);
            mRetryButton.setVisibility(View.INVISIBLE);
            mEmptyView.setVisibility(View.INVISIBLE);
            mToolBar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(mToolBar);

            // retrieves recipe list from last run, so we dont need to call API again
            mRecipeList = savedInstanceState.getParcelableArrayList("recipeList");

            // applies the settings for our layoutManager and sets it on the recyclerView
            // ** depends on layout mode
            if (!mTwoPane) {
                Log.d("NOT in two pane", " MODE");
                mLinearLayoutManager = new LinearLayoutManager(this);
                mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(mLinearLayoutManager);
            } else {
                Log.d("IS IN two pane", " MODE");
                mGridLayoutManager = new GridLayoutManager(this, 2);
                mRecyclerView.setLayoutManager(mGridLayoutManager);
            }


            // creates the adapter and sets it onto the recyclerView
            mRecipeAdapter = new RecipeAdapter(mRecipeList, this);
            mRecyclerView.setAdapter(mRecipeAdapter);

        } else {
            Log.d("state", "NULL (new start)");
            ButterKnife.bind(this);
            mToolBar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(mToolBar);

            // shows while we're fetching data to display
            mProgressBar.setVisibility(View.VISIBLE);

            // creates an empty arrayList that we'll update after the api call
            mRecipeList = new ArrayList<>();
            mEmptyView.setVisibility(View.INVISIBLE);
            mRetryButton.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);

            if (!mTwoPane) {
                Log.d("NOT in two pane", " MODE");
                mLinearLayoutManager = new LinearLayoutManager(this);
                mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(mLinearLayoutManager);
            } else {
                Log.d("IS IN two pane", " MODE");
                mGridLayoutManager = new GridLayoutManager(this, 2);
                mRecyclerView.setLayoutManager(mGridLayoutManager);
            }

            // creates the adapter and sets it onto the recyclerView
            mRecipeAdapter = new RecipeAdapter(mRecipeList, this);
            mRecyclerView.setAdapter(mRecipeAdapter);

            // calls API for recipes info
            callApi();

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // saves recipe list so we dont need to call API again on orientation change
        outState.putParcelableArrayList("recipeList", mRecipeList);
    }

    private void callApi() {
        Log.d("about to call", " APIIIIIIII");

        mProgressBar.setVisibility(View.VISIBLE);
        mRetryButton.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);

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

                mProgressBar.setVisibility(View.INVISIBLE);
                mEmptyView.setVisibility(View.VISIBLE);
                mRetryButton.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Call Failed",
                        Toast.LENGTH_LONG).show();

                Log.d(TAG, "CALL FAILED!!!!");
                Log.d(call.toString(), t.getLocalizedMessage());

                mRetryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callApi();
                    }
                });
            }
        });
    }
}
