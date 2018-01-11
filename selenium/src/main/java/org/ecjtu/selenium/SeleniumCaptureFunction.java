package org.ecjtu.selenium;

import org.openqa.selenium.chrome.ChromeDriver;

public class SeleniumCaptureFunction {

    public static void main(String[] args) {
        SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);
        ChromeDriver driver = SeleniumEngine.getInstance().newDestopChromeDriver();
        driver.get("http://quote.eastmoney.com/center/list.html#33");

    }
}
