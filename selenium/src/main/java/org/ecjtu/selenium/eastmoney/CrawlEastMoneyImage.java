package org.ecjtu.selenium.eastmoney;

import org.ecjtu.selenium.SeleniumEngine;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CrawlEastMoneyImage {
    private static final SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat("yyyy-M-d");

    public static void main(String[] args) throws IOException {
        SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);
        ChromeDriver driver = SeleniumEngine.getInstance().newDesktopChromeDriver();
        driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
        BufferedReader reader = new BufferedReader(new FileReader(new File("res" + File.separator + "config.txt")));
        String line = reader.readLine();
        String[] params = line.split(" ");
        int[] metric = new int[params.length];
        int index = 0;
        for (String param : params) {
            metric[index++] = Integer.parseInt(param);
        }
        List<CrawlEastMoney.GuPiaoModel> modelList = null;
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream("res" + File.separator + "eastmoney"))) {
            modelList = (List<CrawlEastMoney.GuPiaoModel>) is.readObject();
        } catch (Exception e) {
        }
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream("res" + File.separator + "eastmoney_zhishu"))) {
            List<CrawlEastMoney.GuPiaoModel> list2 = (List<CrawlEastMoney.GuPiaoModel>) is.readObject();
            modelList.addAll(0, list2);
        } catch (Exception e) {
        }
        loop:
        if (modelList != null) {
            String text = null;
            while (true) {
                try {
                    Thread.sleep(500);
                    if (driver == null) {
                        driver = SeleniumEngine.getInstance().newDesktopChromeDriver();
                        driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
                        driver.get("http://so.eastmoney.com/web/s?keyword=%E4%B8%8A%E8%AF%81%E6%8C%87%E6%95%B0");
                        Thread.sleep(500);
                    } else {
                        driver.get("http://so.eastmoney.com/web/s?keyword=%E4%B8%8A%E8%AF%81%E6%8C%87%E6%95%B0");
                    }
                    WebElement button = driver.findElement(By.tagName("button"));
                    try {
                        button.click();
//                    SeleniumEngine.getInstance().desktopClick(button);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Thread.sleep(500);
                    int count = 0;
                    while (true) {
                        text = driver.findElements(By.className("clearflaot")).get(5).getText();
                        text = text.substring(text.indexOf("\n") + 1);
                        text = text.split(" ")[0];
                        if (text != null || !text.equals("")) {
                            break;
                        } else {
                            Thread.sleep(500);
                            if (count++ >= 5) {
                                break;
                            }
                        }
                    }
                } catch (TimeoutException e) {
                    e.printStackTrace();
                    driver.quit();
                    driver = null;
                    continue;
                } catch (Exception e) {
                    e.printStackTrace();
                    break loop;
                }
                break;
            }
            System.out.println("date " + text);
            File dir = new File("res" + File.separator, text);
            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdirs();
            }
            List<CrawlEastMoney.GuPiaoModel> extraList = new ArrayList<>();
            for (int j = 0; j < modelList.size(); j++) {
                CrawlEastMoney.GuPiaoModel model = modelList.get(j);
                String name = model.name.replace("*", "@");
                File image0 = new File(dir.getAbsolutePath() + File.separator + model.daiMa + name, "merge.png");
                if (image0.exists()) {
                    continue;
                }
                try {
                    while (true) {
                        try {
                            if (driver != null) {
                                driver.get(model.href);
                            } else {
                                driver = SeleniumEngine.getInstance().newDesktopChromeDriver();
                                driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
                                driver.get(model.href);
                            }
                        } catch (TimeoutException e) {
                            driver.quit();
                            driver = null;
                            e.printStackTrace();
                            extraList.add(model);
                            continue;
                        }
                        break;
                    }

                    File saveDir = new File(dir, model.daiMa + name);
                    if (!saveDir.exists() || !saveDir.isDirectory()) {
                        saveDir.mkdirs();
                    }
                    List<WebElement> tab = driver.findElement(By.className("changeFenshiTab")).findElements(By.tagName("span"));
                    tab.remove(0);
                    WebElement element;
                    Actions action;
                    Dimension size;
                    File file;
                    BufferedImage image;
                    BufferedImage subImage;
                    File tmpFile;
                    List<String> filePath = new ArrayList<>();
                    for (int i = 0; i < tab.size(); i++) {
                        try {
                            SeleniumEngine.getInstance().desktopClick(tab.get(i));
                        } catch (Exception e) {
                        }
                        element = driver.findElement(By.id("emchart-0"));
                        action = new Actions(driver);
                        action.moveToElement(element).perform();
                        element = driver.findElement(By.id("emchart-0"));
                        size = element.getSize();
                        file = driver.getScreenshotAs(FILE);
                        image = ImageIO.read(file);
                        subImage = image.getSubimage(metric[0], metric[1], metric[2], metric[3]);
                        tmpFile = new File(saveDir, String.format("emchart-%s.png", i));
                        if (!tmpFile.exists()) {
                            tmpFile.createNewFile();
                        }
                        filePath.add(tmpFile.getAbsolutePath());
                        ImageIO.write(subImage, "png", tmpFile);
                    }

                    tab = driver.findElement(By.id("changektab")).findElements(By.tagName("span"));
                    for (int i = 0; i < 3; i++) {
                        try {
                            SeleniumEngine.getInstance().desktopClick(tab.get(i));
                        } catch (Exception e) {
                        }
                        element = driver.findElement(By.id("emchartk"));
                        action = new Actions(driver);
                        action.moveToElement(element).perform();
                        new Actions(driver).moveByOffset(0, -metric[7] / 2).perform();
                        file = driver.getScreenshotAs(FILE);
                        image = ImageIO.read(file);
                        subImage = image.getSubimage(metric[4], metric[5], metric[6], metric[7]);
                        tmpFile = new File(saveDir, String.format("emchartk%s.png", i));
                        if (!tmpFile.exists()) {
                            tmpFile.createNewFile();
                        }
                        filePath.add(tmpFile.getAbsolutePath());
                        ImageIO.write(subImage, "png", tmpFile);

                    }

                    merge(saveDir, filePath);

                } catch (NoSuchWindowException e) {
                    e.printStackTrace();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
        driver.quit();
        System.out.println("system exit");
    }

    public static void merge(File parent, List<String> filePath) throws IOException {
        int width = 0;
        int height = 0;
        BufferedImage[] imgs = new BufferedImage[filePath.size()];
        for (int i = 0; i < filePath.size(); i++) {
            String path = filePath.get(i);
            imgs[i] = ImageIO.read(new File(path));
            width += imgs[i].getWidth();
            if (height < imgs[i].getHeight()) {
                height = imgs[i].getHeight();
            }
        }
        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D gd = (Graphics2D) out.getGraphics();
        gd.setBackground(new Color(255, 255, 255, 0));
        int x = 0;
        for (BufferedImage img : imgs) {
            gd.drawImage(img, x, 0, null);
            x += img.getWidth();
        }
        File tmpFile = new File(parent, "merge.png");
        if (!tmpFile.exists()) {
            tmpFile.createNewFile();
        }
        gd.dispose();
        ImageIO.write(out, "png", tmpFile);
    }

    static OutputType<File> FILE = new OutputType<File>() {
        public File convertFromBase64Png(String base64Png) {
            return this.save((byte[]) BYTES.convertFromBase64Png(base64Png));
        }

        public File convertFromPngBytes(byte[] data) {
            return this.save(data);
        }

        private File save(byte[] data) {
            FileOutputStream stream = null;

            File var4;
            try {
                File tmpFile = new File("res", "tmp.png");
                stream = new FileOutputStream(tmpFile);
                stream.write(data);
                var4 = tmpFile;
            } catch (IOException var13) {
                throw new WebDriverException(var13);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException var12) {
                    }
                }

            }

            return var4;
        }

        public String toString() {
            return "OutputType.FILE";
        }
    };
}
