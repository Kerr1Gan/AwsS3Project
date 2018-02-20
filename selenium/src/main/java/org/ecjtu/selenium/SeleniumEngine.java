package org.ecjtu.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeleniumEngine {

    public static final String DRIVE_PATH;
    public static String FILE_SEPERATOR;

    static {
        String suffix;
        if (File.separator.equals("\\")) {
            suffix = "chromedriver.exe";
        } else {
            suffix = "chromedriver";
        }
        DRIVE_PATH = String.format(".%sselenium%ssrc%slibs%s%s", File.separator, File.separator, File.separator, File.separator, suffix);
        System.out.println(System.getProperty("os.name") + " file separator " + File.separator + " driver path " + DRIVE_PATH);
    }

    private static class INNER {
        private static final SeleniumEngine INSTANCE = new SeleniumEngine();
    }

    public static void initEngine(String drivePath) {
        System.setProperty("webdriver.chrome.driver", drivePath);

//        if(platform == null || platform.equals("")){
//            FILE_SEPERATOR = "";
//        } else if(platform.equals("linux")){
//            FILE_SEPERATOR = "/";
//        }else{
//            FILE_SEPERATOR = "\\";
//        }
    }

    public static SeleniumEngine getInstance() {
        return INNER.INSTANCE;
    }

    private ChromeDriver mChromeDriver;

    private SeleniumEngine() {
//        Map<String, String> mobileEmulation = new HashMap<>();
//        //设置设备,例如:Google Nexus 7/Apple iPhone 6
//        //mobileEmulation.put("deviceName", "Google Nexus 7");
//        mobileEmulation.put("deviceName", "Nexus 6P");
//        Map<String, Object> chromeOptions = new HashMap<>();
//        chromeOptions.put("mobileEmulation", mobileEmulation);
//        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
//        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
//        mChromeDriver = new ChromeDriver(capabilities);
    }

    public ChromeDriver getChromeDriver() {
        if (mChromeDriver == null) {
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
        return mChromeDriver;
    }

    public void releaseChromeDriver() {
        if (mChromeDriver != null) {
            try {
                mChromeDriver.quit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mChromeDriver = null;
            }
        }
    }

    public ChromeDriver newMobileDriver() {
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

    public ChromeDriver newDesktopChromeDriver() {
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        return new ChromeDriver(capabilities);
    }

    public void openNewTabJs(ChromeDriver webDriver, String url) {
        webDriver.executeScript("window.open('" + url + "')");
    }

    public void openNewTabKeys(ChromeDriver webDriver) {
        webDriver.findElement(By.tagName("body")).sendKeys(Keys.CONTROL + "t");
        webDriver.findElement(By.tagName("body")).sendKeys(Keys.ENTER);
    }

    public void switchTab(ChromeDriver webDriver, int index) {
        List<String> tabs = new ArrayList<>(webDriver.getWindowHandles());
        webDriver.switchTo().window(tabs.get(index)); //switches to new tab
    }

    public void closeCurrentTabByJs(ChromeDriver driver) {
        driver.executeScript("window.opener=null;window.close();");
    }

    public int getCurrentTabIndex(ChromeDriver webDriver) {
        return new ArrayList<>(webDriver.getWindowHandles()).indexOf(webDriver.getWindowHandle());
    }

    public void desktopClick(WebElement webElement) {
        webElement.click();
        webElement.sendKeys(Keys.ENTER);
    }

    public void mobileClick(ChromeDriver webDriver, WebElement webElement, String js) {
//        String js = "var sub = document.getElementById(\"login\"); " + "sub.click();";
        webDriver.executeScript(js);
        webElement.sendKeys(Keys.ENTER);
    }

    public void mobileClickById(ChromeDriver webDriver, WebElement webElement, String id) {
        mobileClick(webDriver, webElement, String.format("var target = document.getElementById('%s');target.focus();target.click();", id));
    }

    public void mobileClickByClassName(ChromeDriver webDriver, WebElement webElement, String className, int index) {
        mobileClick(webDriver, webElement, String.format("var target = document.getElementsByClassName('%s')[%d];target.focus();target.click();", className, index));
    }
}
