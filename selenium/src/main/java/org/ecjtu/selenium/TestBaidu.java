package org.ecjtu.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class TestBaidu {
    public static void main(String[] args) {
        SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);
        ChromeDriver chromeDriver = SeleniumEngine.getInstance().getChromeDriver();
        chromeDriver.get("http://www.baidu.com");
        WebElement ele = chromeDriver.findElement(By.id("login"));
        SeleniumEngine.getInstance().mobileClickById(chromeDriver, ele, "login");

        SeleniumEngine.getInstance().openNewTabJs(chromeDriver,"");
        SeleniumEngine.getInstance().switchTab(chromeDriver,1);
        chromeDriver.get("http://www.baidu.com");
        SeleniumEngine.getInstance().closeCurrentTabByJs(chromeDriver);
//        chromeDriver.get("http://www.baidu.com");
//        SeleniumEngine.getInstance().switchTab(chromeDriver,0);
    }
}
