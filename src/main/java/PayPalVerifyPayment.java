

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by KerriGan on 2018/2/14.
 */

public class PayPalVerifyPayment {


    private static final String TOKEN_URL = "https://api.sandbox.paypal.com/v1/oauth2/token";
    private static final String PAYMENT_DETAIL = "https://api.sandbox.paypal.com/v1/payments/payment/";
    private static final String clientId = "Aa1DTb-LTiOXtSvui98eKLcvlv1e8zvzigGnzLKzsc2yOO_SEkwy7IVXcgfM1vHFWlQ4hkvLOYYH6bVr";
    private static final String secret = "EN-HYwq-ntbqp76LI2yDEaO0j9-gPz5yPYq7K8HTj7qLsUIp7mNQ_EmVR3eb-1-EwxhQsh4wHuI5ASG6";

    /**
     * 获取token
     * 了解更多：https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
     *
     * @return
     */
    private String getAccessToken() {
        try {
            URL url = new URL(TOKEN_URL);
            String authorization = clientId + ":" + secret;
            authorization = Base64.encodeBase64String(authorization.getBytes());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");// 提交模式
            //设置请求头header
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Accept-Language", "en_US");
            conn.setRequestProperty("Authorization", "Basic " + authorization);
            // conn.setConnectTimeout(10000);//连接超时 单位毫秒
            // conn.setReadTimeout(2000);//读取超时 单位毫秒
            conn.setDoOutput(true);// 是否输入参数
            String params = "grant_type=client_credentials";
            conn.getOutputStream().write(params.getBytes());// 输入参数

            InputStreamReader inStream = new InputStreamReader(conn.getInputStream());
            BufferedReader reader = new BufferedReader(inStream);
            StringBuilder result = new StringBuilder();
            String lineTxt = null;
            while ((lineTxt = reader.readLine()) != null) {
                result.append(lineTxt);
            }
            reader.close();
            String accessTokey = new JSONObject(result.toString()).optString("access_token");
            System.out.println("getAccessToken:" + accessTokey);
            return accessTokey;
        } catch (Exception err) {
            err.printStackTrace();
        }
        return null;
    }

    /**
     * 获取支付详情
     * 了解更多：https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
     *
     * @param paymentId 支付ID，来自于用户客户端
     * @return
     */
    public String getPaymentDetails(String paymentId) {
        try {
            URL url = new URL(PAYMENT_DETAIL + paymentId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");// 提交模式
            //设置请求头header
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + getAccessToken());
            // conn.setConnectTimeout(10000);//连接超时 单位毫秒
            // conn.setReadTimeout(2000);//读取超时 单位毫秒
            InputStreamReader inStream = new InputStreamReader(conn.getInputStream());
            BufferedReader reader = new BufferedReader(inStream);
            StringBuilder result = new StringBuilder();
            String lineTxt = null;
            while ((lineTxt = reader.readLine()) != null) {
                result.append(lineTxt);
            }
            reader.close();
            return result.toString();
        } catch (Exception err) {
            err.printStackTrace();
        }
        return null;
    }

    /**
     * 获取支付详情
     * 了解更多：https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
     *
     * @param paymentId 支付ID，来自于用户客户端
     * @return
     */
    public boolean verifyPayment(String paymentId) throws Exception {
        String str = getPaymentDetails(paymentId);
        System.out.println(str);
        JSONObject detail = new JSONObject(str);
        //校验订单是否完成
        if ("approved".equals(detail.optString("state"))) {
            JSONObject transactions = detail.optJSONArray("transactions").optJSONObject(0);
            JSONObject amount = transactions.optJSONObject("amount");
            JSONArray relatedResources = transactions.optJSONArray("related_resources");
            //从数据库查询支付总金额与Paypal校验支付总金额
            double total = 0;
            System.out.println("amount.optDouble('total'):" + amount.optDouble("total"));
            if (total != amount.optDouble("total")) {
                return false;
            }
            //校验交易货币类型
            String currency = "USD";
            if (!currency.equals(amount.optString("currency"))) {
                return false;
            }
            //校验每个子订单是否完成
            for (int i = 0, j = relatedResources.length(); i < j; i++) {
                JSONObject sale = relatedResources.optJSONObject(i).optJSONObject("sale");
                if (sale != null) {
                    if (!"completed".equals(sale.optString("state"))) {
                        System.out.println("子订单未完成,订单状态:" + sale.optString("state"));
                    }
                }
            }
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        PayPalVerifyPayment payment = new PayPalVerifyPayment();
        boolean success = false;
        try {
            success = payment.verifyPayment("PAY-87X20470C98270520LKB2XBI");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(success ? "支付完成" : "支付校验失败");
    }
}

