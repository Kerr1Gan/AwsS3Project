package org.ecjtu.selenium;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class CrawlXKorean {

    static List<XKoreanModel> sXKoreanModels = null;

    public static void main(String[] args) {
        SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(".\\res\\xkorean"))) {
            sXKoreanModels = (List<XKoreanModel>) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (sXKoreanModels == null) {
            sXKoreanModels = new ArrayList<>();
        }
        ChromeDriver driver = null;
        try {
            for (int i = 35; i <= 37; i++) {
                driver = SeleniumEngine.getInstance().getChromeDriver();
                crawl(driver, String.format("http://www.xkorean.cam/page/%d", i));
                SeleniumEngine.getInstance().releaseChromeDriver();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        saveCache(sXKoreanModels);
        if (driver != null) {
            driver.quit(); // 关闭驱动并退出所有窗口
        }
    }

    public static void crawl(ChromeDriver driver, String url) {
        driver.get(url);
        String source = driver.getPageSource();
        Document doc = Jsoup.parse(source);
        Elements elements = doc.getElementsByClass("thcovering-video");
        for (Element e : elements) {
            System.out.println("current crawl index " + elements.indexOf(e));
            for (int i = 0; i < e.children().size(); i++) {
                Element child = e.child(i);
                loop:
                for (int j = 0; j < child.children().size(); j++) {
                    Element child2 = child.child(j);
                    if (child2.tagName().equals("a")) {
                        String videoUrl = child2.attr("href");
                        if (checkIfExists(sXKoreanModels, videoUrl)) {
                            System.out.println("videoUrl exists " + videoUrl);
                            continue loop;
                        }
                        System.out.println("videoUrl " + videoUrl);

                        SeleniumEngine.getInstance().openNewTabJs(driver, "");
                        SeleniumEngine.getInstance().switchTab(driver, 1);
                        try {
                            driver.get(videoUrl);
                        } catch (Exception e3) {
                            e3.printStackTrace();
                            try {
                                driver.close();
                            } catch (Exception e4) {
                                e4.printStackTrace();
                            }
                            SeleniumEngine.getInstance().switchTab(driver, 0);
                            continue loop;
                        }
                        System.out.println("find real path start");
                        String innerWindowUrl = "";
                        try {
                            innerWindowUrl = driver.findElement(By.id("player")).findElement(By.tagName("iframe")).getAttribute("src");
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            try {
                                driver.close();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                            SeleniumEngine.getInstance().switchTab(driver, 0);
                            continue loop;
                        }
                        System.out.println("innerVideoUrl " + innerWindowUrl);
                        SeleniumEngine.getInstance().openNewTabJs(driver, "");
                        SeleniumEngine.getInstance().switchTab(driver, 2);
                        driver.get(innerWindowUrl);
                        try {
                            SeleniumEngine.getInstance().mobileClickById(driver, driver.findElement(By.id("videooverlay")), "videooverlay");
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            try {
                                driver.close();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            try {
                                SeleniumEngine.getInstance().switchTab(driver, 1);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            try {
                                driver.close();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            try {
                                SeleniumEngine.getInstance().switchTab(driver, 0);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            continue loop;
                        }
                        System.out.println("find real path end");
                        WebElement videoElement = driver.findElement(By.id("olvideo_html5_api"));
                        String realUrl = videoElement.getAttribute("src");
                        String imageUrl = driver.findElement(By.id("olvideo")).getAttribute("poster");
                        String title = e.child(1).text();
                        System.out.println("realUrl " + realUrl + " imageUrl " + imageUrl + " title " + title);
                        driver.close();
                        SeleniumEngine.getInstance().switchTab(driver, 1);
                        driver.close();
                        SeleniumEngine.getInstance().switchTab(driver, 0);
                        XKoreanModel model = new XKoreanModel();
                        model.imageUrl = imageUrl;
                        model.innerVideoUrl = innerWindowUrl;
                        model.realUrl = realUrl;
                        model.title = title;
                        model.videoUrl = videoUrl;
                        sXKoreanModels.add(model);

                        new Thread(() -> {
                            saveCache(sXKoreanModels);
                        }).start();
                    }
                }
            }
        }
        System.out.println("crawl end");
    }


    public static boolean checkIfExists(List<XKoreanModel> list, String videoUrl) {
        for (XKoreanModel model : list) {
            if (model.videoUrl.equals(videoUrl)) {
                return true;
            }
        }
        return false;
    }

    public static synchronized void saveCache(List<XKoreanModel> list) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(".\\res\\xkorean")))) {
            out.writeObject(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class XKoreanModel implements Serializable {
        public String videoUrl;
        public String innerVideoUrl;
        public String realUrl;
        public String imageUrl;
        public String title;
    }

    private static class DriverTimerTask extends TimerTask {
        private ChromeDriver mChromeDriver;

        public DriverTimerTask(ChromeDriver webDriver) {
            mChromeDriver = webDriver;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    System.out.println("crawl time out.......");
                    if (mChromeDriver != null) {
                        mChromeDriver.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

}
