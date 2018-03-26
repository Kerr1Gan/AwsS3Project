package org.ecjtu.selenium.xiann5;

import org.ecjtu.selenium.SeleniumEngine;
import org.ecjtu.selenium.ofo91.OfO91Model;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class CrawlXianN5 {
    public static final String URL = "http://m.xiann5.com/";

    public static void main(String[] args) {
        SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);
        ChromeDriver chromeDriver = SeleniumEngine.getInstance().newMobileDriver(false);
        try {
            chromeDriver.get("http://www.xiann5.com");
            waitContent(chromeDriver);
            List<WebElement> webElements = chromeDriver.findElement(By.className("bus_navlist")).findElements(By.tagName("li"));
            List<String[]> title = new ArrayList<>();
            for (WebElement element : webElements) {
                String[] arr = new String[2];
                arr[0] = element.getText();
                arr[1] = element.findElement(By.tagName("a")).getAttribute("href");
                title.add(arr);
            }
            title.remove(0);
            Map<String, List<OfO91Model>> map = new LinkedHashMap<>();
            for (String[] item : title) {
                String url = item[1];
                chromeDriver.get(url);
                waitContent(chromeDriver);
                List<OfO91Model> ofO91Models = new ArrayList<>();
                while (!Thread.interrupted()) {
                    List<WebElement> videoElements = chromeDriver.findElements(By.className("bus_vtem"));
                    for (WebElement video : videoElements) {
                        String url2 = video.findElement(By.tagName("a")).getAttribute("href");
                        String image = video.findElement(By.tagName("img")).getAttribute("src");
                        String name = video.findElement(By.className("t")).getText();
                        OfO91Model model = new OfO91Model();
                        model.setTitle(name);
                        model.setVideoUrl(url2);
                        model.setImageUrl(image);
                        ofO91Models.add(model);
                    }
                    try {
                        WebElement next = chromeDriver.findElement(By.className("nextPage"));
                        String nextUrl = next.getAttribute("href");
                        chromeDriver.get(nextUrl);
                    } catch (NoSuchElementException e) {
                        e.printStackTrace();
                        break;
                    }
                    waitContent(chromeDriver);
                }
                map.put(item[0], ofO91Models);
            }

            System.out.println(chromeDriver.getPageSource());
            try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("xiann5"))) {
                os.writeObject(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        chromeDriver.quit();
    }

    public static void waitContent(ChromeDriver chromeDriver) {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(1);
                chromeDriver.findElement(By.className("bus_promote"));
                break;
            } catch (NoSuchElementException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
