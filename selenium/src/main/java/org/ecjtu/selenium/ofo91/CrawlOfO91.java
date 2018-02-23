package org.ecjtu.selenium.ofo91;

import org.ecjtu.selenium.SeleniumEngine;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CrawlOfO91 {

    public static void main(String[] args) {
        SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);
        ChromeDriver chromeDriver = SeleniumEngine.getInstance().newDesktopChromeDriver();
        Map<String, List<OfO91Model>> map = new LinkedHashMap<>();
        try {
            WebElement menu = null;
            while (true) {
                try {
                    chromeDriver.get("http://www.ofo91.com/");
                    Thread.sleep(500);
                    menu = chromeDriver.findElement(By.className("main_nav"));
                } catch (NoSuchWindowException e) {
                    break;
                } catch (Exception e) {
                    chromeDriver.quit();
                    chromeDriver = SeleniumEngine.getInstance().newDesktopChromeDriver();
                    continue;
                }
                break;
            }
            List<WebElement> webElements = menu.findElements(By.tagName("a"));
            List<String> menuUrls = new ArrayList<>();
            List<String> titleList = new ArrayList<>();
            for (WebElement element : webElements) {
                menuUrls.add(element.getAttribute("href"));
                titleList.add(element.getText());
            }
            int index = 0;
            for (String title : titleList) {
                List<OfO91Model> list = new ArrayList<>();
                List<WebElement> pages = null;
                int flagIndex = 0;
                while (true) {
                    if (flagIndex++ > 20) {
                        break;
                    }
                    try {
                        chromeDriver.get(menuUrls.get(index));
                        pages = chromeDriver.findElement(By.className("pagination")).findElements(By.tagName("li"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        chromeDriver.quit();
                        chromeDriver = SeleniumEngine.getInstance().newDesktopChromeDriver();
                        continue;
                    }
                    break;
                }
                index++;
                List<String> pageList = new ArrayList<>();
                pages.remove(0);
                pages.remove(pages.size() - 1);
                String baseUrl = pages.get(pages.size() - 1).findElement(By.tagName("a")).getAttribute("href");
                baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("=") + 1);
                for (int i = 0; i < pages.size(); i++) {
                    String url = baseUrl + (i + 1);
                    pageList.add(url);
                }
                for (String page : pageList) {
                    WebElement element = null;
                    while (true) {
                        try {
                            chromeDriver.get(page);
                            element = chromeDriver.findElement(By.className("red_video_sec"));
                        } catch (Exception e) {
                            e.printStackTrace();
                            chromeDriver.quit();
                            chromeDriver = SeleniumEngine.getInstance().newDesktopChromeDriver();
                            continue;
                        }
                        break;
                    }
                    List<WebElement> elements = element.findElements(By.tagName("a"));
                    for (int i = 0; i < elements.size(); i++) {
                        String videoTitle = elements.get(i).getText();
                        String videoUrl = elements.get(i).getAttribute("href");
                        WebElement bgElement = null;
                        try {
                            bgElement = elements.get(i).findElement(By.className("red_video_card"));
                        } catch (Exception e) {
                        }
                        if (bgElement == null) continue;
                        String style = bgElement.getAttribute("style");
                        StringBuilder image = new StringBuilder();
                        boolean flag = false;
                        for (int j = 0; j < style.length(); j++) {
                            char c = style.charAt(j);
                            if (c == '"') {
                                flag = !flag;
                                continue;
                            }
                            if (flag) {
                                image.append(c);
                            }
                        }
                        OfO91Model model = new OfO91Model();
                        model.setImageUrl(image.toString());
                        model.setInnerVideoUrl("");
                        model.setTitle(videoTitle);
                        model.setVideoUrl(videoUrl);
                        list.add(model);
                    }
                }
                map.put(title, list);
                try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("res\\ofo91"))) {
                    os.writeObject(map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("system exit");
            chromeDriver.close();
        }
    }
}
