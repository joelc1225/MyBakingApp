package com.joelcamargo.mybakingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joelcamargo.mybakingapp.model.Recipe;
import com.joelcamargo.mybakingapp.model.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by joelcamargo on 12/22/17.
 */

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepViewHolder> {

    private ArrayList<Step> mStepArrayList;
    private Recipe mRecipe;
    private Context mContext;

    // Constructor
    public StepsAdapter(Recipe recipe, Context context) {
        this.mRecipe = recipe;
        this.mContext = context;
    }

    @Override
    public StepsAdapter.StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.step_item_layout, parent, false);

        return new StepViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StepsAdapter.StepViewHolder holder, int position) {
        mStepArrayList = (ArrayList<Step>) mRecipe.getSteps();
        final Step currentStep = mStepArrayList.get(position);

        String stepNumber = mContext.getString
                (R.string.step_number_header) + " " + Integer.toString(position + 1);
        holder.mStepNumberTextView.setText(stepNumber);

        String shortDescription = currentStep.getShortDescription();
        holder.mRecipeShortDescription.setText(shortDescription);
    }

    @Override
    public int getItemCount() {
        mStepArrayList = (ArrayList<Step>) mRecipe.getSteps();
        return mStepArrayList.size();
    }

    public class StepViewHolder extends RecyclerView.ViewHolder {
        // binds the views needed to inflate the Steps list item
        @BindView(R.id.step_number_textView)
        TextView mStepNumberTextView;
        @BindView(R.id.recipe_short_description)
        TextView mRecipeShortDescription;


        public StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
