package com.ecjtu.netcore.network;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ethan_Xiang on 2017/7/14.
 */
public abstract class BaseNetwork {
    public static int TIME_OUT = 5 * 1000;
    public static String CHARSET = "UTF-8";
    public static String HEADER_CONTENT_ENCODING = "Content-Encoding";
    public static String HEADER_CONTENT_LENGTH = "Content-Length";
    public static String HTTP_PREFIX = "http://";
    public static int CACHE_SIZE = 5 * 1024;
    private static final String TAG = "BaseNetwork";

    static class Method {
        static String OPTIONS = "OPTIONS";
        static String GET = "GET";
        static String HEAD = "HEAD";
        static String POST = "POST";
        static String PUT = "PUT";
        static String DELETE = "DELETE";
        static String TRACE = "TRACE";
    }

    private IRequestCallback mCallback = null;

    private HttpURLConnection mHttpUrlConnection = null;

    private InputStream mInputStream = null;

    private OutputStream mOutputStream = null;

    private HashMap<String, String> mHeaders = null;

    private boolean mDoInput = true;

    private boolean mDoOutput = false;

    private String mUrl = "";

    private Integer mTimeOut = null;

    Charset charset = Charset.forName("utf-8");

    public BaseNetwork setRequestCallback(IRequestCallback callback) {
        mCallback = callback;
        return this;
    }

    public BaseNetwork request(String urlStr, Map<String, String> mutableMap) {
        Exception ex = null;

        byte[] ret = null;
        try {
            mUrl = urlStr;
            URL url = new URL(mUrl);
            mHttpUrlConnection = (HttpURLConnection) url.openConnection();
            setupRequest(mHttpUrlConnection);
            String paramStr = setParams(mHttpUrlConnection, mutableMap);
            connect();
            pushContent(mHttpUrlConnection, paramStr);
            ret = getContent(mHttpUrlConnection);
        } catch (Exception e) {
            e.printStackTrace();
            ex = e;
        } finally {
            if (ex != null && mCallback instanceof IRequestCallbackV2) {
                ((IRequestCallbackV2) mCallback).onError(mHttpUrlConnection, ex);
            } else {
                String str = "";
                try {
                    if (ret != null) {
                        str = new String(ret, "utf-8");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mCallback.onSuccess(mHttpUrlConnection, str, ret);
            }
            mHttpUrlConnection.disconnect();
        }
        return this;
    }

    protected BaseNetwork setupRequest(HttpURLConnection httpURLConnection) throws ProtocolException {
        httpURLConnection.setDoInput(mDoInput);
        httpURLConnection.setDoOutput(mDoOutput);
        httpURLConnection.setRequestMethod(Method.GET);
        httpURLConnection.setConnectTimeout(mTimeOut != null ? mTimeOut : TIME_OUT);
        httpURLConnection.setReadTimeout(mTimeOut != null ? mTimeOut : TIME_OUT);
        httpURLConnection.setRequestProperty("Content-Type", "*/*");
        httpURLConnection.setRequestProperty(HEADER_CONTENT_ENCODING, CHARSET);
        if (mHeaders != null) {
            for (Map.Entry<String, String> entry : mHeaders.entrySet()) {
                httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public BaseNetwork setHeaders(HashMap<String, String> values) {
        if (mHeaders == null) {
            mHeaders = new HashMap<String, String>();
        }
        for (Map.Entry<String, String> entry : values.entrySet()) {
            setHeader(entry.getKey(), entry.getValue());
        }
        return this;
    }


    public BaseNetwork setHeader(String key, String value) {
        if (mHeaders == null) {
            mHeaders = new HashMap<String, String>();
        }
        mHeaders.put(key, value);
        return this;
    }

    protected String setParams(HttpURLConnection httpURLConnection, Map<String, String> mutableMap) throws ProtocolException, UnsupportedEncodingException {
        String ret = "";
        if (mutableMap != null) {
            httpURLConnection.setRequestMethod(Method.POST);
            setDoInputOutput(true, true);
            httpURLConnection.setDoInput(mDoInput);
            httpURLConnection.setDoOutput(mDoOutput);
            String param = "";
            for (Map.Entry entry : mutableMap.entrySet()) {
                if (!param.equals("")) {
                    param += "&";
                }
                param += String.format("%s=%s", entry.getKey(), entry.getValue());
            }

            httpURLConnection.setRequestProperty(HEADER_CONTENT_LENGTH, "" + param.getBytes("utf-8").length);
            ret = param;
        }
        return ret;
    }

    public void connect() throws IOException {
        try {
            mHttpUrlConnection.connect();
        } catch (IOException io) {
            throw io;
        }
    }

    protected byte[] getContent(HttpURLConnection httpURLConnection) throws IOException {
        byte[] ret = null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            byte[] temp = new byte[CACHE_SIZE];
            InputStream is = httpURLConnection.getInputStream();
            mInputStream = is;
            int len;
            len = is.read(temp);
            while (len > 0) {
                os.write(temp, 0, len);
                len = is.read(temp);
            }
            ret = os.toByteArray();
            os.close();
        } catch (Exception ex) {
            if (mInputStream != null) {
                mInputStream.close();
            }
            if (os != null) {
                os.close();
            }
            ex.printStackTrace();
            throw ex;
        }
        return ret;
    }

    public void cancel() {
        try {
            mOutputStream.close();
            mInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mHttpUrlConnection.disconnect();
        }
    }

    protected void pushContent(HttpURLConnection httpURLConnection, String param) throws IOException {
        if (httpURLConnection.getRequestMethod().equals(Method.POST)) {
            if (param != null && param.length() > 0) {
                mOutputStream = httpURLConnection.getOutputStream();
                mOutputStream.write(param.getBytes("utf-8"));
                mOutputStream.flush();
            }
        }
    }

    public BaseNetwork setDoInputOutput(boolean input, boolean output) {
        mDoInput = input;
        mDoOutput = output;
        return this;
    }

    public void setTimeOut(int timeOut) {
        mTimeOut = timeOut;
    }
}