package com.joelcamargo.mybakingapp.data;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by joelcamargo on 12/3/17.
 */

// Helper class that creates the retrofit client with an interceptor to help log calls and debug
// if necessary
@SuppressWarnings({"DefaultFileTemplate", "SameParameterValue"})
class RetrofitClientMaker {

    private static Retrofit retrofitClient = null;

    public static Retrofit getClient(String baseUrl) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

        if (retrofitClient == null) {
            retrofitClient = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofitClient;
    }
}
