package com.example.updesk.Notifications;



import androidx.annotation.NonNull;

import com.example.updesk.Utilities.CONSTANTS;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static Retrofit retrofit;

    public synchronized static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(CONSTANTS.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    @NonNull
    public static NotificationApi getApi() {
        return getRetrofitInstance().create(NotificationApi.class);
    }
}