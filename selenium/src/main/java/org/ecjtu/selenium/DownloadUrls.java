package org.ecjtu.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;

public class DownloadUrls {

    private static final int BEGIN_INDEX = 16;
    private static final int END_INDEX = 50;
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
        try {
            for (int i = BEGIN_INDEX; i < xKoreanModels.size() && i < END_INDEX; i++) {
                XKoreanModel model = xKoreanModels.get(i);
                driver.get(model.innerVideoUrl);
                System.out.println("find real path start");
                try {
                    SeleniumEngine.getInstance().mobileClickById(driver, driver.findElement(By.id("videooverlay")), "videooverlay");
                } catch (Exception e2) {
                    //ignore
                }
                System.out.println("find real path end");
                WebElement videoElement = driver.findElement(By.id("olvideo_html5_api"));
                String realUrl = videoElement.getAttribute("src");
                System.out.println("index " + i + " " + model.title + " downloadUrl " + realUrl);
            }
        } catch (Exception e) {
            //ignore
        }

        driver.quit();
        System.out.println("exit");
    }
}
