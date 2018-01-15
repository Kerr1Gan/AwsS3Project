package org.ecjtu.selenium;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ParseXKoreanPath {

    public static final String IP = "13.125.0.0";

    public static void main(String[] args){
        String jsonStr = null;

        try (FileInputStream in = new FileInputStream(new File(".//res//xkorean.json"))){
            byte[] temp = new byte[in.available()];
            in.read(temp);
            jsonStr = new String(temp);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> pathList = new ArrayList<>();
        if(jsonStr!=null){
            try {
                JSONArray jArr = new JSONArray(jsonStr);
                for(int i=0;i<jArr.length();i++){
                    JSONObject jObj = jArr.getJSONObject(i);
                    String path = jObj.optString("realUrl");
                    int len =0;
                    StringBuilder sb = new StringBuilder();
                    boolean flag = false;
                    String timeStamp = null;
                    String ip = null;
                    while (len<path.length()){
                        char c= path.charAt(len);
                        if(c=='~'){
                            flag=!flag;
                            len++;
                            continue;
                        }
                        if(flag){
                            sb.append(c);
                        }else if(!sb.toString().equals("")){
                            if(timeStamp==null){
                                timeStamp = sb.toString();
                                sb.replace(0,sb.length(),"");
                                flag=true;
                                len--;
                            }else if(ip==null){
                                ip = sb.toString();
                                sb.replace(0,sb.length(),"");
                            }
                        }
                        len++;
                    }
                    path=path.replace(ip,"%s");
                    path=path.replace(timeStamp,"%s");
                    pathList.add(String.format(path,System.currentTimeMillis(),IP));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try(BufferedWriter os = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("res\\xkoreanPath")))){
            for(String url : pathList){
                os.write(url);
                os.write("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
