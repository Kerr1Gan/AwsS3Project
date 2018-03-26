package org.ecjtu.selenium.xiann5;

import org.ecjtu.selenium.SeleniumEngine;
import org.ecjtu.selenium.ofo91.OfO91Model;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class CrawlXianN5Videos {

    public static void main(String[] args) {
        Map<String, List<OfO91Model>> map = null;
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream("res\\xiann5"))) {
            map = (Map<String, List<OfO91Model>>) is.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (map != null) {
            SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);
            ChromeDriver chromeDriver = SeleniumEngine.getInstance().newMobileDriver(false);
            try {
                for (Map.Entry<String, List<OfO91Model>> entry : map.entrySet()) {
                    String title = entry.getKey();
                    List<OfO91Model> list = entry.getValue();
                    for (OfO91Model model : list) {
                        if (model.getInnerVideoUrl() != null) {
                            continue;
                        }
                        chromeDriver.get(model.getVideoUrl());
                        waitForContent(chromeDriver);
                        WebElement videoElement = chromeDriver.findElement(By.id("jc_player"));
                        String innerVideoUrl = videoElement.findElement(By.tagName("video")).getAttribute("src");
                        model.setInnerVideoUrl(innerVideoUrl);
                    }
                    try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("res\\xiann5"))) {
                        os.writeObject(map);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            chromeDriver.quit();
        }
    }

    private static void waitForContent(ChromeDriver chromeDriver) {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(1);
                chromeDriver.findElement(By.id("jc_player"));
            } catch (NoSuchElementException e) {
                e.printStackTrace();
                continue;
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            break;
        }
    }
}
