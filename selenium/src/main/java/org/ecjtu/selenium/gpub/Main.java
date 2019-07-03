package org.ecjtu.selenium.gpub;

import org.ecjtu.selenium.SeleniumEngine;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);
        ChromeDriver driver = new ChromeDriver();
        try {
            driver.get("https://play.google.com/apps/publish/");
//            WebDriverWait wait = new WebDriverWait(driver,1,100);
//            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("gwt-uid-260")));
            while (true) {
                try {
                    WebElement webElement = driver.findElement(By.id("gwt-uid-260"));
                    if (webElement != null) {
                        int x = 0;
                        x++;
                        fillStoreListing(driver);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Thread.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void uploadFile(String path) {
        try {
            Process process = Runtime.getRuntime().exec("F:\\Projects\\AwsS3Project\\selenium\\src\\libs\\upload.exe " + path);
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void fillStoreListing(WebDriver driver) {
        // short description
        driver.findElements(By.tagName("textarea")).get(0).sendKeys("short description");

        // full description
        driver.findElements(By.tagName("textarea")).get(1).sendKeys("full description");

        driver.findElements(By.className("gwt-TextBox")).get(2).sendKeys("");

        // choose applicaion app
        driver.findElements(By.className("A1JAINC-Q-a")).get(0).click();
        driver.findElements(By.className("A1JAINC-P-k")).get(0).click();

        // choose finance app
        driver.findElements(By.className("A1JAINC-Q-a")).get(3).click();
        driver.findElements(By.className("A1JAINC-P-k")).get(0).click();

        // enter mail
        driver.findElements(By.className("gwt-TextBox")).get(4).sendKeys("ethanxiang95@gmail.com");

        // enter privacy url
        driver.findElements(By.className("gwt-TextBox")).get(6).sendKeys("https://www.baidu.com");

        // upload 512 logo
        driver.findElements(By.tagName("input")).get(4).sendKeys("C:\\Users\\xiang\\Desktop\\SB\\512.png");

        // upload screen capture index 7 - 14
        driver.findElements(By.tagName("input")).get(7).sendKeys("C:\\Users\\xiang\\Desktop\\SB\\512.png");

        // upload 1024 * 500 7+5
        driver.findElements(By.tagName("input")).get(13).sendKeys("C:\\Users\\xiang\\Desktop\\SB\\512.png");

        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // save store detail
            driver.findElement(By.id("gwt-uid-642")).click();
        }
    }

    public static void fillApkInfo(WebDriver driver) {
        // upload apk
        driver.findElements(By.tagName("input")).get(17).sendKeys("C:\\Users\\xiang\\Desktop\\SB\\SB.zip");

        // set publish message
        driver.findElements(By.tagName("textarea")).get(1).clear();
        driver.findElements(By.tagName("textarea")).get(1).sendKeys("<en-US>\n请在此处输入或粘贴en-US版的版本说明\n</en-US>");

        // save app release
        driver.findElements(By.tagName("button")).get(78).click();
    }

    public static void fillAppContent(WebDriver driver) {

    }
}
