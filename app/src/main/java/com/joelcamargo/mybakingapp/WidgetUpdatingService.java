package com.joelcamargo.mybakingapp;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.joelcamargo.mybakingapp.model.Recipe;

/**
 * Created by joelcamargo on 2/21/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class WidgetUpdatingService extends IntentService {

    private static final String ACTION_UPDATE_RECIPE_ON_WIDGET =
            "com.joelcamargo.mybakingapp.action.updateWidget";

    public WidgetUpdatingService() {
        super("WidgetUpdatingService");
    }

    public static void startActionUpdateWidget(Context context, Recipe recipe) {

        // creates Bundle to hold recipe that goes into intent
        Bundle bundle = new Bundle();
        bundle.putParcelable("recipe", recipe);

        // creates intent that will start Update widget service
        Intent intent = new Intent(context, WidgetUpdatingService.class);
        intent.setAction(ACTION_UPDATE_RECIPE_ON_WIDGET);
        intent.putExtras(bundle);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // recipe variable will update inside if statement
        Recipe recipe = null;

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_RECIPE_ON_WIDGET.equals(action)) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    recipe = bundle.getParcelable("recipe");
                }

                // creates appWidget manager used to communicate with BakingWidgetProvider
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                        new ComponentName(this, BakingWidgetProvider.class));
                // Now update the widgets
                BakingWidgetProvider.updateAppWidgets(this, appWidgetManager, recipe, appWidgetIds);
            }
        }
    }
}
