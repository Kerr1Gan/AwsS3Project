package org.ecjtu.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@SuppressWarnings("Duplicates")
public class CrawlEastMoneySize {
    public static void main(String[] args) throws IOException {
        SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);
        ChromeDriver driver = SeleniumEngine.getInstance().newDestopChromeDriver();
        try {
            driver.get("http://quote.eastmoney.com/sh600162.html");
            WebElement element = driver.findElement(By.id("emchart-0"));
            Actions action = new Actions(driver);
            action.moveToElement(element).perform();

            SeleniumEngine.getInstance().openNewTabJs(driver, "");
            SeleniumEngine.getInstance().switchTab(driver, 1);
            driver.get("http://quote.eastmoney.com/sh600162.html");
            element = driver.findElement(By.id("emchartk"));
            action = new Actions(driver);
            action.moveToElement(element).perform();
            while (!Thread.interrupted()) {
                Thread.sleep(500);
                driver.getPageSource();
            }
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
