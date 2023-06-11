package com.example.updesk.Notifications;



import com.example.updesk.Utilities.CONSTANTS;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificationApi {

    @Headers({"Authorization: key=" + CONSTANTS.SERVER_KEY, "Content-Type:" + CONSTANTS.CONTENT_TYPE})
    @POST("fcm/send")
    Call<ResponseBody> postNotification(
            @Body PushNotification notification
    );
}
