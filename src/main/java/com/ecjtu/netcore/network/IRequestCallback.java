package com.ecjtu.netcore.network;

import java.net.HttpURLConnection;

/**
 * Created by Ethan_Xiang on 2017/7/14.
 */
public interface IRequestCallback {
    void onSuccess(HttpURLConnection httpURLConnection, String response, byte[] bytes);
}
