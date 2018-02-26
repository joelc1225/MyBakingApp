package com.joelcamargo.mybakingapp;

import com.google.gson.Gson;
import com.joelcamargo.mybakingapp.model.Recipe;

import java.lang.reflect.Type;

/**
 * Created by joelcamargo on 2/25/18.
 */

@SuppressWarnings("DefaultFileTemplate")
class ConverterHelper {

    public static String convertToJsonString(Recipe recipe, Type type) {
        if (recipe == null) return null;
        Gson gson = new Gson();
        return gson.toJson(recipe, type);
    }

    public static <T> T convertFromJsonString(String jsonString, Type type) {
        if (jsonString == null) return null;
        Gson gson = new Gson();
        return gson.fromJson(jsonString, type);
    }
}
