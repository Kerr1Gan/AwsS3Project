package org.ecjtu.s3;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        System.out.println("Hello World");
        List<String[]> questions = new ArrayList<>();

        String[] ques = new String[5];
        ques[0] = "谁是佐助的真爱？";
        ques[1] = "小樱";
        ques[2] = "鸣人";
        ques[3] = "香玲";
        ques[4] = "-2";
        questions.add(ques);

        ques = new String[5];
        ques[0] = "犬夜叉最后和谁在一起了？";
        ques[1] = "戈薇";
        ques[2] = "桔梗";
        ques[3] = "杀生丸";
        ques[4] = "-1";
        questions.add(ques);

        ques = new String[5];
        ques[0] = "柯南是用什么麻醉小五郎的？";
        ques[1] = "手表";
        ques[2] = "眼镜";
        ques[3] = "球鞋";
        ques[4] = "-1";
        questions.add(ques);

        ques = new String[5];
        ques[0] = "第二位出场的奥特曼是谁？";
        ques[1] = "宇宙英雄";
        ques[2] = "佐菲";
        ques[3] = "赛文";
        ques[4] = "-2";
        questions.add(ques);

        ques = new String[5];
        ques[0] = "第二位奥特曼是谁？";
        ques[1] = "宇宙英雄";
        ques[2] = "佐菲";
        ques[3] = "赛文";
        ques[4] = "-3";
        questions.add(ques);

        ques = new String[5];
        ques[0] = "宠物精灵中小智的精灵？";
        ques[1] = "皮卡丘";
        ques[2] = "喵喵";
        ques[3] = "阿柏蛇";
        ques[4] = "-1";
        questions.add(ques);

        ques = new String[5];
        ques[0] = "魔鬼司令的那根针是用什么材料做的？";
        ques[1] = "ZMC";
        ques[2] = "ZMZ";
        ques[3] = "塑料";
        ques[4] = "-1";
        questions.add(ques);

        ques = new String[5];
        ques[0] = "新三角使用者的弟弟是谁？";
        ques[1] = "鹰羽龙";
        ques[2] = "鹰羽二郎丸";
        ques[3] = "新马烈";
        ques[4] = "-1";
        questions.add(ques);

        ques = new String[5];
        ques[0] = "魔鬼司令的那根针是用什么材料做的？";
        ques[1] = "ZMC";
        ques[2] = "ZMZ";
        ques[3] = "塑料";
        ques[4] = "-1";
        questions.add(ques);

        ques = new String[5];
        ques[0] = "艾斯奥特曼中的女主是谁？";
        ques[1] = "南夕子";
        ques[2] = "北斗";
        ques[3] = "堤光子";
        ques[4] = "-1";
        questions.add(ques);

        JSONArray jsonArray = new JSONArray();
        for (String[] q : questions) {
            JSONObject jObj = new JSONObject();
            try {
                jObj.put("question", q[0]);
                jObj.put("answer1", q[1]);
                jObj.put("answer2", q[2]);
                jObj.put("answer3", q[3]);
                jObj.put("answer", q[4]);
                jsonArray.put(jObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try (FileOutputStream os = new FileOutputStream("res\\question")) {
            os.write(jsonArray.toString().getBytes("utf-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

