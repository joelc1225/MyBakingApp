package com.joelcamargo.mybakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joelcamargo.mybakingapp.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by joelcamargo on 12/1/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.MyViewHolder> {

    private final ArrayList<Recipe> recipeArrayList;
    private final Context mContext;

    // Constructor
    public RecipeAdapter(ArrayList<Recipe> recipes, Context context) {
        this.recipeArrayList = recipes;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.recipe_card_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecipeAdapter.MyViewHolder holder, final int position) {

        final Recipe recipe = recipeArrayList.get(position);
        // gets the image URI for the image
        String imageUri = recipe.getImage();

        // checks if string uri is valid before trying to load into picasso
        if (!imageUri.isEmpty()) {
            Log.d("has image", " uri OK!!");
            Picasso.with(mContext).load(imageUri)
                    .placeholder(R.drawable.ic_favorite_border_black_24dp)
                    .error(R.drawable.ic_favorite_border_black_24dp)
                    .into(holder.mBackgroundImageView);
        }

        String recipeName = recipe.getName();
        holder.mRecipeNameTextView.setText(recipeName);

        holder.mBackgroundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Creates Bundle that will hold our Recipe object
                Bundle bundle = new Bundle();
                bundle.putParcelable("recipe", recipe);

                // creates intent to open FragmentActivity (which is a container for our fragments)
                Intent openRecipeInfoIntent = new Intent(mContext, FragmentActivity.class);
                openRecipeInfoIntent.putExtras(bundle);
                mContext.startActivity(openRecipeInfoIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // binds the views needed for this adapter
        @BindView(R.id.recipeImageBackground)
        ImageView mBackgroundImageView;
        @BindView(R.id.recipe_name_textview)
        TextView mRecipeNameTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
