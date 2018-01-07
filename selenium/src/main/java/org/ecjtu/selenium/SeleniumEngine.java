package org.ecjtu.selenium;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeleniumEngine {

    public static final String DRIVE_PATH = ".\\selenium\\src\\libs\\chromedriver.exe";

    private static class INNER {
        private static final SeleniumEngine INSTANCE = new SeleniumEngine();
    }

    public static void initEngine(String drivePath) {
        System.setProperty("webdriver.chrome.driver", drivePath);
    }

    public static SeleniumEngine getInstance() {
        return INNER.INSTANCE;
    }

    private ChromeDriver mChromeDriver;

    private SeleniumEngine() {
        Map<String, String> mobileEmulation = new HashMap<>();
        //设置设备,例如:Google Nexus 7/Apple iPhone 6
        //mobileEmulation.put("deviceName", "Google Nexus 7");
        mobileEmulation.put("deviceName", "Nexus 6P");
        Map<String, Object> chromeOptions = new HashMap<>();
        chromeOptions.put("mobileEmulation", mobileEmulation);
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        mChromeDriver = new ChromeDriver(capabilities);
    }

    public ChromeDriver getChromeDriver() {
        return mChromeDriver;
    }

    public ChromeDriver newChromeDriver() {
        Map<String, String> mobileEmulation = new HashMap<>();
        //设置设备,例如:Google Nexus 7/Apple iPhone 6
        //mobileEmulation.put("deviceName", "Google Nexus 7");
        mobileEmulation.put("deviceName", "Nexus 6P");
        Map<String, Object> chromeOptions = new HashMap<>();
        chromeOptions.put("mobileEmulation", mobileEmulation);
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        return new ChromeDriver(capabilities);
    }

    public void openNewTab(ChromeDriver webDriver, String url) {
        webDriver.executeScript("window.open('" + url + "')");
    }

    public void switchTab(ChromeDriver webDriver, int index) {
        List<String> tabs = new ArrayList<>(webDriver.getWindowHandles());
        webDriver.switchTo().window(tabs.get(index)); //switches to new tab
    }
}
