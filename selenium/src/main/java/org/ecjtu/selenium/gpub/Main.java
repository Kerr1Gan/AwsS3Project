package org.ecjtu.selenium.gpub;

import org.ecjtu.selenium.SeleniumEngine;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.function.Function;

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
            Process process = Runtime.getRuntime().exec("F:\\Projects\\AwsS3Project\\selenium\\src\\libs\\upload.exe "+path);
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
