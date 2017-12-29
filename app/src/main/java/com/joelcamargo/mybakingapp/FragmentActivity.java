package com.joelcamargo.mybakingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.joelcamargo.mybakingapp.model.Ingredient;
import com.joelcamargo.mybakingapp.model.Recipe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by joelcamargo on 12/19/17.
 */

public class FragmentActivity extends AppCompatActivity implements RecipeInfoFragment.UpdateViewsInterface {

    // Recipe that will be instantiated once intent is received
    private static Recipe mReceivedRecipe;

    @BindView(R.id.fragmentToolbar)
    Toolbar mFragmentToolBar;
    @BindView(R.id.steps_recyclerView)
    RecyclerView mStepsRecyclerView;
    ExpandingList mExpandingList;
    StepsAdapter mStepsAdapter;
    LinearLayoutManager mLinearLayoutManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        ButterKnife.bind(this);
        setSupportActionBar(mFragmentToolBar);

        // Gets the bundle containing our Recipe object
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            mReceivedRecipe = bundle.getParcelable("recipe");
        }

        // Creates and applies the RecipeInfoFragment
        RecipeInfoFragment fragment = new RecipeInfoFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction()
                .add(fragment, "currentRecipe");
        ft.commit();

        // applies the settings for our layoutManager and sets it on the recyclerView
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mStepsRecyclerView.setLayoutManager(mLinearLayoutManager);

        mStepsAdapter = new StepsAdapter(mReceivedRecipe, getApplicationContext());
        mStepsRecyclerView.setAdapter(mStepsAdapter);
        updateInfoViews(mReceivedRecipe);
        mStepsAdapter.notifyDataSetChanged();
    }

    // interface implemented to update views in our Fragment
    @Override
    public void updateInfoViews(Recipe recipe) {
        // Sets the recipe name in the toolbar
        String recipeName = mReceivedRecipe.getName();
        mFragmentToolBar.setTitle(recipeName);

        // Gets the ingredient array to create the sub-items below
        ArrayList<Ingredient> ingredientArrayList = (ArrayList<Ingredient>) recipe.getIngredients();
        int ingredientArraySize = ingredientArrayList.size();

        // Creates expanding list
        mExpandingList = (ExpandingList) findViewById(R.id.expanding_list_main);

        // Creates the items and applies the data into our expandingLists views
        ExpandingItem item = mExpandingList.createNewItem(R.layout.expanding_layout);
        TextView expandable_title = item.findViewById(R.id.title_ingredients_list);
        expandable_title.setText(R.string.ingredient_list_header);
        item.createSubItems(ingredientArraySize);

        // for loop to iterate through all ingredients and populate sub item textViews
        for (int position = 0; position < ingredientArraySize; position++) {
            // Ingredient being used for current sub item position
            Ingredient currentIngredient = ingredientArrayList.get(position);
            String ingredientName = currentIngredient.getIngredient();
            String ingredientMeasure = currentIngredient.getMeasure();
            String ingredientQuantity = String.valueOf(currentIngredient.getQuantity());
            String fullMeasureString = ingredientQuantity +" "+ ingredientMeasure+" "+ingredientName;
            Log.d("current ingredient is: ", fullMeasureString);

            View currentSubItem = item.getSubItemView(position);

            ((TextView) currentSubItem.findViewById(R.id.ingredientName))
                    .setText(ingredientName);

            ((TextView) currentSubItem.findViewById(R.id.measure_textView))
                    .setText(getString(R.string.measure_string_format, ingredientQuantity, ingredientMeasure));
        }
    }
}
