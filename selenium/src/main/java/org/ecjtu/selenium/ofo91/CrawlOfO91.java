package org.ecjtu.selenium.ofo91;

import org.ecjtu.selenium.SeleniumEngine;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.List;

public class CrawlOfO91 {

    public static void main(String[] args) {
        SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);
        ChromeDriver chromeDriver = SeleniumEngine.getInstance().newDesktopChromeDriver();
        try {
            chromeDriver.get("http://www.ofo91.com/");
            WebElement menu = chromeDriver.findElement(By.className("main_nav"));
            List<WebElement> webElements = menu.findElements(By.tagName("a"));
            List<String> menuUrls = new ArrayList<>();
            for (WebElement element : webElements) {
                menuUrls.add("http://www.ofo91.com/" + element.getAttribute("href"));
            }
            List<String> pageList = new ArrayList<>();
            List<WebElement> pages = chromeDriver.findElement(By.className("pagination")).findElements(By.tagName("li"));
            pages.remove(0);
            pages.remove(pages.size() - 1);
            String baseUrl = pages.get(pages.size() - 1).findElement(By.tagName("a")).getAttribute("href");
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("=") + 1);
            for (int i = 0; i < pages.size(); i++) {
                baseUrl += (i + 1);
                pageList.add(baseUrl);
            }
            WebElement element = chromeDriver.findElement(By.className("red_video_sec"));
            List<WebElement> elements = element.findElements(By.tagName("a"));
            String title = elements.get(0).getText();
            String videoUrl = elements.get(0).getAttribute("href");
            WebElement bgElement = elements.get(0).findElement(By.className("red_video_card"));
            String style = bgElement.getAttribute("style");
            StringBuilder image = new StringBuilder();
            boolean flag = false;
            for (int i = 0; i < style.length(); i++) {
                char c = style.charAt(i);
                if (c == '"') {
                    flag = !flag;
                    continue;
                }
                if (flag) {
                    image.append(c);
                }
            }
            int x = 0;
            x++;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("system exit");
            chromeDriver.close();
        }
    }
}
