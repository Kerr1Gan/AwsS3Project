package com.ecjtu.netcore.network;


import java.net.HttpURLConnection;

public interface IRequestCallbackV2 extends IRequestCallback {
    void onError(HttpURLConnection httpURLConnection, Exception exception);
}