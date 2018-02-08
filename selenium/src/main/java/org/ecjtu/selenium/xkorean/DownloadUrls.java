package org.ecjtu.selenium.xkorean;

import org.ecjtu.selenium.SeleniumEngine;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.util.List;

public class DownloadUrls {

    private static final int BEGIN_INDEX = 0;
    private static final int END_INDEX = 9999;

    // 15
    public static void main(String[] args) {
        SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);
        List<XKoreanModel> xKoreanModels = null;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(".\\res\\xkorean"))) {
            xKoreanModels = (List<XKoreanModel>) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ChromeDriver driver = SeleniumEngine.getInstance().getChromeDriver();
        try (FileWriter writer = new FileWriter(".\\res\\downloadUrls.txt")) {
            for (int i = BEGIN_INDEX; i < xKoreanModels.size() && i < END_INDEX; i++) {
                XKoreanModel model = xKoreanModels.get(i);
                driver.get(model.innerVideoUrl);
                System.out.println("find real path start innerUrl " + model.innerVideoUrl);
                try {
                    SeleniumEngine.getInstance().mobileClickById(driver, driver.findElement(By.id("videooverlay")), "videooverlay");
                } catch (Exception e2) {
                    //ignore
                }
                System.out.println("find real path end");
                WebElement videoElement = driver.findElement(By.id("olvideo_html5_api"));
                String realUrl = videoElement.getAttribute("src");
//                realUrl = changeIp(realUrl, "13.125.0.0");
                System.out.println("index " + i + " " + model.title + " downloadUrl " + realUrl);
                writer.write(realUrl + "\n");
            }
        } catch (Exception e) {
            //ignore
        }

        driver.quit();
        System.out.println("exit");
    }

    public static String changeIp(String path, String resetIP) {
        int len = 0;
        StringBuilder sb = new StringBuilder();
        boolean flag = false;
        String timeStamp = null;
        String ip = null;
        while (len < path.length()) {
            char c = path.charAt(len);
            if (c == '~') {
                flag = !flag;
                len++;
                continue;
            }
            if (flag) {
                sb.append(c);
            } else if (!sb.toString().equals("")) {
                if (timeStamp == null) {
                    timeStamp = sb.toString();
                    sb.replace(0, sb.length(), "");
                    flag = true;
                    len--;
                } else if (ip == null) {
                    ip = sb.toString();
                    sb.replace(0, sb.length(), "");
                }
            }
            len++;
        }
        path = path.replace(ip, "%s");
        path = path.replace(timeStamp, "%s");
        return String.format(path, System.currentTimeMillis(), resetIP);
    }

}
