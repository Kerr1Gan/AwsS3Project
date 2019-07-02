package org.ecjtu.selenium.ofo91;

import org.ecjtu.selenium.SeleniumEngine;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CrawlOfO91Videos {

    public static void main(String[] args) {
        Map<String, List<OfO91Model>> map = null;
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream("res\\ofo91"))) {
            map = (Map<String, List<OfO91Model>>) is.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String jsonString = null;
        try (FileInputStream is = new FileInputStream("res\\ofo91.json")) {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            byte[] temp = new byte[1024 * 10];
            int len = 0;
            while ((len = is.read(temp)) > 0) {
                buf.write(temp, 0, len);
            }
            jsonString = buf.toString("utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String[]> cache = new ArrayList<>();
        if (jsonString != null) {
            try {
                JSONArray oldArray = new JSONArray(jsonString);
                for (int i = 0; i < oldArray.length(); i++) {
                    JSONObject oldObj = (JSONObject) oldArray.get(i);
                    JSONArray oldArr = (JSONArray) oldObj.get("array");
                    for (int j = 0; j < oldArr.length(); j++) {
                        JSONObject obj = oldArr.getJSONObject(j);
                        String imageUrl = obj.optString("imageUrl");
                        String innerVideoUrl = obj.optString("innerVideoUrl");
                        if (imageUrl != null && innerVideoUrl != null && !imageUrl.equals("") && !innerVideoUrl.equals("")) {
                            String[] cacheItem = new String[2];
                            cacheItem[0] = imageUrl;
                            cacheItem[1] = innerVideoUrl;
                            cache.add(cacheItem);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ChromeDriver driver = null;
        if (map != null) {
            Set<Map.Entry<String, List<OfO91Model>>> entrySet = map.entrySet();
            SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);
            driver = SeleniumEngine.getInstance().newDesktopChromeDriver(false);
            List<String> downloadList = new ArrayList<>();
            int downloadIndex = 0;
            loop:
            for (Map.Entry<String, List<OfO91Model>> entry : entrySet) {
                List<OfO91Model> ofoList = entry.getValue();
                for (OfO91Model model : ofoList) {
                    if (model.getInnerVideoUrl() == null || model.getInnerVideoUrl().equals("")) {
                        inner:
                        while (true) {
                            try {
                                //load from cache
                                for (String[] item : cache) {
                                    if (item[0].equals(model.getImageUrl())) {
                                        model.setInnerVideoUrl(item[1]);
                                        break inner;
                                    }
                                }

                                // get the inner video url
                                driver.get(model.getVideoUrl());
                                String innerVideoUrl = driver.findElement(By.id("playerv")).getAttribute("src");
                                model.setInnerVideoUrl(innerVideoUrl);
                                downloadList.add(innerVideoUrl);
                                if (downloadList.size() >= 10) {
                                    for (String url : downloadList) {
                                        String path;
                                        while (true) {
                                            path = "res\\ofo\\download" + downloadIndex++ + ".txt";
                                            File file = new File(path);
                                            if (file.exists()) {
                                                continue;
                                            }
                                            break;
                                        }
                                        try (BufferedWriter writer = new BufferedWriter(
                                                new FileWriter(path))) {
                                            writer.write(url);
                                            writer.write("\n");
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                            } catch (NoSuchWindowException e) {
                                break loop;
                            } catch (Exception e) {
                                e.printStackTrace();
                                driver.quit();
                                driver = SeleniumEngine.getInstance().newDesktopChromeDriver();
                                continue;
                            }
                            break;
                        }
                    }
                }
                try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("res\\ofo91"))) {
                    os.writeObject(map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("system exit");
        if (driver != null) {
            driver.quit();
        }
    }
}
