package org.ecjtu.selenium.weipai;

import org.ecjtu.selenium.SeleniumEngine;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CrawlWeiPaiVideos {

    public static void main(String[] args) {
        Map<String, List<WeiPaiModel>> saved = null;
        try (ObjectInputStream os = new ObjectInputStream(new FileInputStream(new File("res\\weipai")))) {
            saved = (LinkedHashMap<String, List<WeiPaiModel>>) os.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);
        ChromeDriver driver = null;
        if (saved != null) {
            driver = SeleniumEngine.getInstance().newDesktopChromeDriver();
            loop:
            for (Map.Entry<String, List<WeiPaiModel>> entry : saved.entrySet()) {
                String key = entry.getKey();
                List<WeiPaiModel> value = entry.getValue();
                int saveFlag = 0;
                List<WeiPaiModel> extras = new ArrayList<>();
                for (WeiPaiModel model : value) {
                    try {
                        driver.get(model.getHref());
                        int index = -10;
                        String pageSource = "";
                        while (!Thread.interrupted()) {
                            pageSource = driver.getPageSource();
                            index = pageSource.indexOf(").setup(");
                            break;
                        }
                        StringBuffer buf = new StringBuffer();
                        if (index < 0) {
                            int vIndex = pageSource.indexOf("f_video[0]='") + 12;
                            int mIndex = pageSource.indexOf("m_video[0]=\"") + 12;
                            while (!Thread.interrupted()) {
                                char ch = pageSource.charAt(vIndex++);
                                if (ch == '\'') {
                                    break;
                                }
                                buf.append(ch);
                            }
                            model.setVideoUrl(buf.toString());
                            buf.replace(0, buf.length(), "");
                            while (!Thread.interrupted()) {
                                char ch = pageSource.charAt(mIndex++);
                                if (ch == '\"') {
                                    break;
                                }
                                buf.append(ch);
                            }
                            model.setVideoImageUrl(buf.toString());
                            WebElement scrollBox = driver.findElement(By.className("scroll_box"));
                            List<WebElement> innerVideos = scrollBox.findElements(By.className("inner"));
                            for (int i = 1; i < innerVideos.size(); i++) {
                                WeiPaiModel inner = new WeiPaiModel();
                                vIndex = pageSource.indexOf(String.format("f_video[%d]='", i)) + 12;
                                mIndex = pageSource.indexOf(String.format("m_video[%d]=\"", i)) + 12;
                                buf.replace(0, buf.length(), "");
                                while (!Thread.interrupted()) {
                                    char ch = pageSource.charAt(vIndex++);
                                    if (ch == '\'') {
                                        break;
                                    }
                                    buf.append(ch);
                                }
                                inner.setVideoUrl(buf.toString());
                                buf.replace(0, buf.length(), "");
                                while (!Thread.interrupted()) {
                                    char ch = pageSource.charAt(mIndex++);
                                    if (ch == '\"') {
                                        break;
                                    }
                                    buf.append(ch);
                                }
                                inner.setVideoImageUrl(buf.toString());
                                inner.setTitle(innerVideos.get(i).getAttribute("title"));
                                extras.add(inner);
                            }
                        } else {
                            index += 8;
                            while (!Thread.interrupted()) {
                                char ch = pageSource.charAt(index++);
                                buf.append(ch);
                                if (ch == '}') {
                                    break;
                                }
                            }
                            JSONObject jObj = new JSONObject(buf.toString());
                            model.setVideoUrl(jObj.optString("file"));
                            model.setVideoImageUrl(jObj.optString("image"));
                        }
                        saveFlag++;
                        if (saveFlag >= 10) {
                            saveFlag = 0;
                            try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File("res\\weipai")))) {
                                os.writeObject(saved);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (NoSuchWindowException e) {
                        e.printStackTrace();
                        break loop;
                    } catch (WebDriverException e) {
                        e.printStackTrace();
                        if (e.getClass().getSimpleName().equals("WebDriverException")) {
                            break loop;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                for (WeiPaiModel extra : extras) {
                    if (value.indexOf(extra) < 0) {
                        value.add(extra);
                    }
                }
                try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File("res\\weipai")))) {
                    os.writeObject(saved);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (driver != null) {
            driver.quit();
        }
        System.out.println("system exit");
    }
}
