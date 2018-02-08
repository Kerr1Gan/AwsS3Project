package org.ecjtu.selenium.weipai;

import org.ecjtu.selenium.SeleniumEngine;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CrawlWeiPaiUrls {

    public static void main(String[] args) {
        SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);
        try (ObjectInputStream os = new ObjectInputStream(new FileInputStream(new File("res\\weipai")))) {
            Map<String, List<WeiPaiModel>> saved = (LinkedHashMap<String, List<WeiPaiModel>>) os.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ChromeDriver driver = SeleniumEngine.getInstance().newDesktopChromeDriver();
        String[] links = new String[]{
                "http://www.weipai.vc/plugin.php?id=smart_video:smart_video&mod=s&fcid=9&scid=0&tcid=0", // 微拍福利
                "http://www.weipai.vc/plugin.php?id=smart_video:smart_video&mod=s&fcid=2&scid=0&tcid=0", // 微拍专辑
                "http://www.weipai.vc/plugin.php?id=smart_video:smart_video&mod=s&fcid=1&scid=0&tcid=0", // 微拍美女
                "http://www.weipai.vc/plugin.php?id=smart_video:smart_video&mod=s&fcid=11&scid=0&tcid=0", // 韩国女主播
                "http://www.weipai.vc/plugin.php?id=smart_video:smart_video&mod=s&fcid=14&scid=0&tcid=0", // 美星写真
                "http://www.weipai.vc/plugin.php?id=smart_video:smart_video&mod=s&fcid=15&scid=0&tcid=0", // 微视频
                "http://www.weipai.vc/plugin.php?id=smart_video:smart_video&mod=s&fcid=21&scid=0&tcid=0" // 美拍福利
        };
        String[] titles = new String[]{"微拍福利", "微拍专辑", "微拍美女", "韩国女主播", "美星写真", "微视频", "美拍福利"};

        Map<String, List<WeiPaiModel>> maps = new LinkedHashMap<>();
        int index = 0;
        for (String link : links) {
            driver.get(link);
            List<WeiPaiModel> ret = new ArrayList<>();
            while (!Thread.interrupted()) {
                driver.findElements(By.className("yk-row"));
                WebElement element = driver.findElement(By.className("yk-row"));
                List<WebElement> itemList = element.findElements(By.className("yk-col3"));
                for (WebElement item : itemList) {
                    WebElement aTag = item.findElement(By.tagName("a"));
                    WebElement imgTag = item.findElement(By.tagName("img"));
                    String title = aTag.getAttribute("title");
                    String href = aTag.getAttribute("href");
                    String imgUrl = imgTag.getAttribute("src");
                    WeiPaiModel model = new WeiPaiModel();
                    model.setTitle(title);
                    model.setHref(href);
                    model.setImgUrl(imgUrl);
                    if (ret.indexOf(model) < 0) {
                        ret.add(model);
                    }
                }
                maps.put(titles[index], ret);
                try {
                    element = driver.findElement(By.className("pg"));
                    WebElement next = element.findElement(By.linkText("下一页"));
                    next.click();
                } catch (NoSuchElementException e) {
                    e.printStackTrace();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File("res\\weipai")))) {
                os.writeObject(maps);
            } catch (IOException e) {
                e.printStackTrace();
            }
            index++;
        }
        if (driver != null) {
            driver.quit();
        }
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File("res\\weipai")))) {
            os.writeObject(maps);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("system exit");
    }
}
