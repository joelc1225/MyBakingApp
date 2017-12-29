package com.joelcamargo.mybakingapp;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joelcamargo.mybakingapp.model.Recipe;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeInfoFragment extends Fragment {


    public RecipeInfoFragment() {
        // Required empty public constructor
    }

    // public interface created to bridge communication between activity and fragment
    public interface UpdateViewsInterface {
        void updateInfoViews(Recipe recipe);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("onCreateVIew", " Reached!!!!");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_info, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("onAttach", " reached!!!");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("onActivity", " Created reached!!!!");
    }


}
