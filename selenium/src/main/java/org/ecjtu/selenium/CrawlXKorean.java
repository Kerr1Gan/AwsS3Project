package org.ecjtu.selenium;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class CrawlXKorean {

    public static void main(String[] args) {
        SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);
        ChromeDriver driver = SeleniumEngine.getInstance().getChromeDriver();
        driver.get("http://www.xkorean.cam");
        String source = driver.getPageSource();
        Document doc = Jsoup.parse(source);
        Elements elements = doc.getElementsByClass("thcovering-video");
        Element ele = elements.get(1);
        for (int i = 0; i < ele.childNodeSize(); i++) {
            Element child = ele.child(i);
            for (int j = 0; j < child.childNodeSize(); j++) {
                Element child2 = child.child(j);
                if (child2.tagName().equals("a")) {
                    String videoUrl = child2.attr("href");
                    SeleniumEngine.getInstance().openNewTab(driver, "");
                    SeleniumEngine.getInstance().switchTab(driver, 1);
                    driver.get(videoUrl);
                    System.out.println("find vjs-big-play-button start");
                    driver.findElementByClassName("vjs-big-play-button").click();
                    System.out.println("find vjs-big-play-button end");
                    WebElement videoElement = driver.findElement(By.id("olvideo_html5_api"));
                    String realUrl = videoElement.getAttribute("src");
                    System.out.println("realUrl " + realUrl);
                    String source2 = driver.getPageSource();

                    int x = 0;
                    x++;
                } else {
                    int x = 0;
                    x++;
                }
            }
        }
    }
}
