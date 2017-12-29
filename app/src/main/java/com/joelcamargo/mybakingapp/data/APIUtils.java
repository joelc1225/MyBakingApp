package com.joelcamargo.mybakingapp.data;

/**
 * Created by joelcamargo on 12/3/17.
 */

// Utils class that contains a helper method that combines the retrofit client
// and API interface with one method
public class APIUtils {
    private static final String RECIPES_BASE_URL =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";

    public static APIInterface getApiClient() {
        return RetrofitClientMaker.getClient(RECIPES_BASE_URL).create(APIInterface.class);
    }
}
