package com.joelcamargo.mybakingapp.customMatcher;

import com.joelcamargo.mybakingapp.model.Recipe;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * Created by joelcamargo on 2/20/18.
 */

@SuppressWarnings("ALL")
public class RecipeTitleMatcher extends TypeSafeDiagnosingMatcher<Recipe> {

    @Override
    protected boolean matchesSafely(Recipe item, Description mismatchDescription) {
        String stringToMatch = "Recipe Name";
        mismatchDescription.appendText("was incorrect value expected");
        return item.getName().equals(stringToMatch);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("The Recipe's title");
    }

}
