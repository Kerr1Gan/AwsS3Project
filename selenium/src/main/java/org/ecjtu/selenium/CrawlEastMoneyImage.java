package org.ecjtu.selenium;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CrawlEastMoneyImage {
    public static void main(String[] args) {
        SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);
        ChromeDriver driver = SeleniumEngine.getInstance().newDestopChromeDriver();
//        driver.manage().timeouts().pageLoadTimeout(8, TimeUnit.SECONDS);
//        driver.manage().timeouts().setScriptTimeout(3, TimeUnit.SECONDS);
        try {
            //        driver.manage().window().maximize();
            try {
                driver.get("http://quote.eastmoney.com/sh600162.html");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                Actions actions = new Actions(driver);
                actions.sendKeys(Keys.ESCAPE).perform();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                System.out.println(driver.getPageSource());
            } catch (Exception e) {
                e.printStackTrace();
            }
            WebElement element = driver.findElement(By.id("emchart-0"));
            Actions action = new Actions(driver);
            action.moveToElement(element).perform();
            element = driver.findElement(By.id("emchart-0"));
            Dimension size = element.getSize();
            File file = driver.getScreenshotAs(FILE);
            BufferedImage image = ImageIO.read(file);
            BufferedImage subImage = image.getSubimage(202, 570, size.width, size.height);
            File tmpFile = new File("res", "screenshot.png");
            ImageIO.write(subImage, "png", tmpFile);

//            element = driver.findElement(By.id("emchartk"));
//            action = new Actions(driver);
//            action.moveToElement(element).perform();
//            size = element.getSize();
//            file = driver.getScreenshotAs(FILE);
//            image = ImageIO.read(file);
//            subImage = image.getSubimage(309, 474, size.width, size.height);
//            tmpFile = new File("res", "screenshot2.png");
//            ImageIO.write(subImage, "png", tmpFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        driver.quit();
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
                File tmpFile = new File("res", "screenshot.png");
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
