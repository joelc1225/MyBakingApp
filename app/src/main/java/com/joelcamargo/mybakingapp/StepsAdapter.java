package com.joelcamargo.mybakingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.joelcamargo.mybakingapp.model.Recipe;
import com.joelcamargo.mybakingapp.model.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by joelcamargo on 12/22/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepViewHolder> {

    private static StepRecyclerViewClickListener mClickListener;
    private ArrayList<Step> mStepArrayList;
    private final Recipe mRecipe;
    private final Context mContext;


    // Constructor
    public StepsAdapter(Context context, Recipe recipe, StepRecyclerViewClickListener listener) {
        this.mRecipe = recipe;
        this.mContext = context;
        mClickListener = listener;
    }

    @Override
    public StepsAdapter.StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.step_item_layout, parent, false);

        return new StepViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StepsAdapter.StepViewHolder holder, final int position) {
        // creates the list of steps from the current recipe
        mStepArrayList = (ArrayList<Step>) mRecipe.getSteps();
        // grabs the step from the clicked position
        final Step currentStep = mStepArrayList.get(position);
        // grabs the integer value of the current step to display in view
        if (position == 0) {

            holder.mStepNumberTextView.setText("");
        } else {

            final String stepNumber = mContext.getString
                    (R.string.step_number_header) + " " + Integer.toString(position);
            holder.mStepNumberTextView.setText(stepNumber);
        }


        String shortDescription = currentStep.getShortDescription();
        holder.mRecipeShortDescription.setText(shortDescription);
    }

    @Override
    public int getItemCount() {
        mStepArrayList = (ArrayList<Step>) mRecipe.getSteps();
        return mStepArrayList.size();
    }

    public interface StepRecyclerViewClickListener {

        @SuppressWarnings("unused")
        void stepRecyclerViewListItemCLick(View v, int position);
    }

    public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // binds the views needed to inflate the Steps list item
        @BindView(R.id.step_number_textView)
        TextView mStepNumberTextView;
        @BindView(R.id.recipe_short_description)
        TextView mRecipeShortDescription;
        @BindView(R.id.step_frame_layout)
        FrameLayout mFrameLayout;


        public StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickListener.stepRecyclerViewListItemCLick(view, this.getLayoutPosition());
        }
    }
}
