package org.ecjtu.selenium;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SeleniumCaptureFunction {

    public static void main(String[] args) throws IOException {
        SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);
        ChromeDriver driver = SeleniumEngine.getInstance().newDesktopChromeDriver();
        try {
            //        driver.manage().window().maximize();
            driver.get("http://quote.eastmoney.com/sh600162.html");
            System.out.println(driver.getPageSource());
            WebElement element = driver.findElement(By.id("emchart-0"));
            Actions action = new Actions(driver);
            action.moveToElement(element).perform();
            element = driver.findElement(By.id("emchart-0"));
            driver.executeScript("return document.body.clientHeight;");
//            Long top = (Long) driver.executeScript("var box = document.getElementById(\"emchart-0\"); var pos = box.getBoundingClientRect();return pos.top");
//            Double left = (Double) driver.executeScript("var box = document.getElementById(\"emchart-0\"); var pos = box.getBoundingClientRect();return pos.left");
            Dimension size = element.getSize();
            File file = driver.getScreenshotAs(FILE);
            BufferedImage image = ImageIO.read(file);
            BufferedImage subImage = image.getSubimage(309, 474, 700, 350);
            File tmpFile = new File("res", "screenshot.png");
            ImageIO.write(subImage, "png", tmpFile);
        }catch (Exception e){
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
