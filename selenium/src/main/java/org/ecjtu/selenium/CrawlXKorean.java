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
//        Element ele = elements.get(1);
        elements.remove(0);
        for (Element e : elements) {
            for (int i = 0; i < e.children().size(); i++) {
                Element child = e.child(i);
                for (int j = 0; j < child.children().size(); j++) {
                    Element child2 = child.child(j);
                    if (child2.tagName().equals("a")) {
                        String videoUrl = child2.attr("href");
                        System.out.println("videoUrl " + videoUrl);
                        SeleniumEngine.getInstance().openNewTab(driver, "");
                        SeleniumEngine.getInstance().switchTab(driver, 1);
                        driver.get(videoUrl);
                        System.out.println("find vjs-big-play-button start");
                        String innerWindowUrl = driver.findElement(By.id("player")).findElement(By.tagName("iframe")).getAttribute("src");
                        System.out.println("innerVideoUrl " + innerWindowUrl);
                        SeleniumEngine.getInstance().openNewTab(driver, "");
                        SeleniumEngine.getInstance().switchTab(driver, 2);
                        driver.get(innerWindowUrl);
                        SeleniumEngine.getInstance().mobileClickById(driver, driver.findElement(By.id("videooverlay")), "videooverlay");
                        System.out.println("find vjs-big-play-button end");
                        WebElement videoElement = driver.findElement(By.id("olvideo_html5_api"));
                        String realUrl = videoElement.getAttribute("src");
                        String imageUrl = driver.findElement(By.id("olvideo")).getAttribute("poster");
                        String title = e.child(1).text();
                        System.out.println("realUrl " + realUrl + " imageUrl " + imageUrl + " title " + title);
                        driver.close();
                        SeleniumEngine.getInstance().switchTab(driver, 1);
                        driver.close();
                        SeleniumEngine.getInstance().switchTab(driver, 0);
                    } else {
                    }
                }
            }
        }
        driver.quit(); // 关闭驱动并退出所有窗口
    }
}
