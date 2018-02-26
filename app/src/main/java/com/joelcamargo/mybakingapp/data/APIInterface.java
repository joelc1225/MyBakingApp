package com.joelcamargo.mybakingapp.data;

import com.joelcamargo.mybakingapp.model.Recipe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by joelcamargo on 12/3/17.
 */

// Interface just for types of API calls. We only need a GET call to get the JSON data we need.
@SuppressWarnings("DefaultFileTemplate")
public interface APIInterface {

    @GET("baking.json")
    Call<ArrayList<Recipe>> getRecipes();
}
