package com.example.chatapp.Remote;

import com.example.chatapp.model.FCMResponse;
import com.example.chatapp.model.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({"Authorization: key=AAAAjnofAJE:APA91bH3D9GW0zAWgg-StFuYtm4iNjT9Rb1wRHbgmzyAGOHl3T7ZP_ottgCgQweQ19i-OeUs82RX8H3xFHptq8mZQbuDM-W3ukp8SmC3EDQQtaXob3Jmt3gj95mtzAT-2r9FXzbzOt1n",
            "Content-Type:application/json"})
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification (@Body FCMSendData body);
}
