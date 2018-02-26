package com.joelcamargo.mybakingapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.gson.reflect.TypeToken;
import com.joelcamargo.mybakingapp.model.Ingredient;
import com.joelcamargo.mybakingapp.model.Recipe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joelcamargo on 2/22/18.
 */

@SuppressWarnings("DefaultFileTemplate")
class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context mContext;
    private List<Ingredient> mIngredientList = new ArrayList<>();
    private final Intent mIntent;

    public WidgetRemoteViewsFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
        mIntent = intent;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mIngredientList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        Ingredient currentIngredient = mIngredientList.get(position);
        String measurementString =
                currentIngredient.getQuantity() + " " + currentIngredient.getMeasure();
        String ingredientName = currentIngredient.getIngredient();

        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_list_item);

        remoteView.setTextViewText(R.id.widget_measure_textView, measurementString);
        remoteView.setTextViewText(R.id.widget_ingredientNameTextView, ingredientName);

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        Log.d("INITDATA", "IN FACTORY");
        mIngredientList.clear();
        String mJsonString = mIntent.getStringExtra("recipe");
        Type type = new TypeToken<Recipe>() {
        }.getType();
        Recipe recipe = ConverterHelper.convertFromJsonString(mJsonString, type);
        mIngredientList = recipe.getIngredients();
    }
}
