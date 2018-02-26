package com.joelcamargo.mybakingapp;

import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.joelcamargo.mybakingapp.customMatcher.RecipeTitleMatcher;
import com.joelcamargo.mybakingapp.model.Ingredient;
import com.joelcamargo.mybakingapp.model.Recipe;
import com.joelcamargo.mybakingapp.model.Step;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;


/**
 * Created by joelcamargo on 2/16/18.
 */
@SuppressWarnings("DefaultFileTemplate")
@RunWith(AndroidJUnit4.class)
public class MainActivityBasicTest {

    private final List<Ingredient> ingredients = new ArrayList<>();
    private final List<Step> steps = new ArrayList<>();

    @Rule
    public IntentsTestRule<MainActivity> mActivityTestRule
            = new IntentsTestRule<>(MainActivity.class);

    @Before
    public void stubAllExternalIntents() {
        intending(not(isInternal())).respondWith(new ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void clickRecipeCardSendsIntentCorrectly() {
        // adds info to both lists required for a Recipe object
        ingredients.add(new Ingredient((float) 2.5, "cups", "flour"));
        long iD = 1;
        String recipeShortDescription = "Melt butter and bittersweet chocolate.";
        steps.add(new Step(iD, recipeShortDescription,
                "main description", "videoUrl", "thumbnailUrl"));

        // Recipe object used to test intent
        String recipe_name = "Recipe Name";
        long servings = 5;
        String image = "image_url_goes_here";
        Recipe testRecipe = new Recipe(iD, recipe_name, ingredients, steps, servings, image);

        // Builds the result to return when the activity is launched
        Intent resultData = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable("recipe", testRecipe);
        resultData.putExtras(bundle);
        ActivityResult result =
                new ActivityResult(Activity.RESULT_OK, resultData);

        // Set up result stubbing when an intent sent to "contacts" is seen.
        intending(toPackage("com.joelcamargo.mybakingapp")).respondWith(result);

        // Find the view in adapter and perform action on that view
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        // Verify that the intent to the Fragment activity
        // was sent with correct action and Recipe Object
        intended(toPackage("com.joelcamargo.mybakingapp"));

        // checks that the data inside the intent is correct
        // checks title
        assertThat(testRecipe, new RecipeTitleMatcher());
        // checks class being sent intent
        intended(hasComponent(FragmentActivity.class.getName()));
        // checks that data was correctly stored inside intent
        intended(hasExtraWithKey("recipe"));

    }
}
