package com.joelcamargo.mybakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.reflect.TypeToken;
import com.joelcamargo.mybakingapp.model.Recipe;

import java.lang.reflect.Type;

/**
 * Implementation of App Widget functionality.
 */
public class BakingWidgetProvider extends AppWidgetProvider {

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, Recipe recipe,
                                        int appWidgetId) {

        Log.d("UPDATEAPPWIDGET", " CALLED!!!!!!");

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget_provider);

        // gets data needed to fill recipe title view on the widget
        String recipeTitle = recipe.getName();
        views.setTextViewText(R.id.widget_recipe_name_textView, recipeTitle);

        // Sets list adapter
        Intent intentForListView = new Intent(context, WidgetRemoteViewsService.class);
        // uses converter helper class to turn Recipe into json String to send through intent
        Type type = new TypeToken<Recipe>() {
        }.getType();
        intentForListView.putExtra("recipe", ConverterHelper.convertToJsonString(recipe, type));
        Log.d("SETTING REMOTE", " ADAPTER!!!!!!");
        views.setRemoteAdapter(R.id.widget_listView, intentForListView);

        // creates bundle that will hold Recipe for necessary intents
        Bundle bundle = new Bundle();
        bundle.putParcelable("recipe", recipe);

        // Create an intent to launch FragmentActivity when title of recipe is clicked
        Intent intent = new Intent(context, FragmentActivity.class);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // sets onClick onto the textView with pending intent
        views.setOnClickPendingIntent(R.id.widget_recipe_name_textView, pendingIntent);

        Log.d("UPDATEAPPWIDGET", " CALLED!!!!!!");
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager,
                                        Recipe recipe, int[] appWidgetIds) {

        // Get recipe from shared preferences if recipe is null
        if (recipe == null) {
            SharedPreferences preferences = context.getSharedPreferences("prefs", 0);
            String recipeString = preferences.getString("recipe", "DEFAULT WHEN NULL");
            Type type = new TypeToken<Recipe>() {
            }.getType();
            recipe = ConverterHelper.convertFromJsonString(recipeString, type);
        }

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, recipe, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateAppWidgets(context.getApplicationContext(), appWidgetManager, null, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

