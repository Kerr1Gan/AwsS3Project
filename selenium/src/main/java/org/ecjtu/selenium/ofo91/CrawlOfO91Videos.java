package org.ecjtu.selenium.ofo91;

import org.ecjtu.selenium.SeleniumEngine;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        ChromeDriver driver = null;
        if (map != null) {
            Set<Map.Entry<String, List<OfO91Model>>> entrySet = map.entrySet();
            for (Map.Entry<String, List<OfO91Model>> entry : entrySet) {
                List<OfO91Model> ofoList = entry.getValue();
                for (OfO91Model model : ofoList) {
                    if (model.getInnerVideoUrl() == null || model.getInnerVideoUrl().equals("")) {
                        SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);
                        driver = SeleniumEngine.getInstance().newDesktopChromeDriver();
                        try {
                            driver.get(model.getVideoUrl());
                            // get the inner video url
                            String innerVideoUrl = "";
                            model.setInnerVideoUrl(innerVideoUrl);
                        } catch (Exception e) {
                            e.printStackTrace();
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
        if (driver != null) {
            driver.quit();
        }
    }
}
