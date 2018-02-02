package com.joelcamargo.mybakingapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_info, container, false);
    }
}
