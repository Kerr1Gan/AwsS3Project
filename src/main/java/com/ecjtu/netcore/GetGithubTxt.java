package com.ecjtu.netcore;

import com.ecjtu.netcore.network.AsyncNetwork;
import com.ecjtu.netcore.network.IRequestCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;

public class GetGithubTxt {
    private static final String[] urls = new String[]{"https://raw.githubusercontent.com/dmlc/web-data/master/mxnet/ptb/ptb.train.txt",
            "https://raw.githubusercontent.com/dmlc/web-data/master/mxnet/ptb/ptb.valid.txt",
            "https://raw.githubusercontent.com/dmlc/web-data/master/mxnet/ptb/ptb.test.txt",
            "https://raw.githubusercontent.com/dmlc/web-data/master/mxnet/tinyshakespeare/input.txt"};

    public static void main(String[] args) {
        for (String url : urls) {
            AsyncNetwork request = new AsyncNetwork();
            request.request(url, null);
            request.setRequestCallback(new IRequestCallback() {
                @Override
                public void onSuccess(HttpURLConnection httpURLConnection, String response, byte[] bytes) {
                    String name = url.substring(url.lastIndexOf("/") + 1);
                    File f = new File(name);
                    try (FileOutputStream os = new FileOutputStream(f)) {
                        os.write(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println(url + " finished");
                }
            });
        }
    }
}
