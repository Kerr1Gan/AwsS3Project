package com.ecjtu.netcore;

import com.ecjtu.netcore.network.AsyncNetwork;
import com.ecjtu.netcore.network.BaseNetwork;
import com.ecjtu.netcore.network.IRequestCallback;
import com.sun.istack.internal.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by Ethan_Xiang on 2017/10/11.
 */

public class RequestManager {

    public static final AsyncNetwork requestDeviceInfo(@NotNull String url, IRequestCallback listener) {
        Map<String, String> var8 = new LinkedHashMap<>();
        var8.put("param", "info");
        AsyncNetwork var4 = new AsyncNetwork();
        var4.setRequestCallback(listener);
        String localUrl = url;
        if (!url.startsWith(BaseNetwork.HTTP_PREFIX)) {
            localUrl = "http://" + url;
        }

        var4.request("" + localUrl + "/API/Info", var8);
        return var4;
    }


}
