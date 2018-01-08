package org.ecjtu.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", ".\\selenium\\src\\libs\\chromedriver.exe");
        Map<String, String> mobileEmulation = new HashMap<>();
        //设置设备,例如:Google Nexus 7/Apple iPhone 6
        //mobileEmulation.put("deviceName", "Google Nexus 7");
        mobileEmulation.put("deviceName", "Nexus 6P");
        Map<String, Object> chromeOptions = new HashMap<>();
        chromeOptions.put("mobileEmulation", mobileEmulation);
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
//        ChromeDriver driver = new ChromeDriver(capabilities);
//        ChromeDriver driver = SeleniumEngine.getInstance().newDestopChromeDriver();
        ChromeDriver driver = SeleniumEngine.getInstance().getChromeDriver();
        driver.get("https://openload.co/embed/-VOmp6i4RD4/wc17121306.mp4");
        SeleniumEngine.getInstance().mobileClickById(driver, driver.findElement(By.id("videooverlay")), "videooverlay");
//        SeleniumEngine.getInstance().mobileClickByClassName(driver, driver.findElement(By.id("vjs-big-play-button")), "vjs-big-play-button");
//        SeleniumEngine.getInstance().desktopClick(driver.findElement(By.id("videooverlay")));
        System.out.println(driver.getPageSource());
    }
}
