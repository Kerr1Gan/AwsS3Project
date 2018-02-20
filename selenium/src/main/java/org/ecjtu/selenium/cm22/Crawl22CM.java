package org.ecjtu.selenium.cm22;

import org.ecjtu.selenium.SeleniumEngine;
import org.openqa.selenium.chrome.ChromeDriver;

public class Crawl22CM {

    public static void main(String[] args) {
        SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);
        ChromeDriver chromeDriver = SeleniumEngine.getInstance().newDesktopChromeDriver();
        chromeDriver.get("https://www.2022cm2.com/videos/4319/843fccb4d5c3a6ec3c5669b367ae0b66/");
        System.out.println(chromeDriver.getPageSource());


    }

//        System.setProperty("phantomjs.binary.path","C:\\selenium\\phantomjs.exe");// set phantomjs exe path
//        System.setProperty("phantomjs.binary.path", "selenium\\libs\\phantomjs.exe");// set phantomjs exe path
//        DesiredCapabilities desiredCapabilities = DesiredCapabilities.phantomjs();
//        desiredCapabilities.setCapability("loadImages", false);
//
//        RemoteWebDriver driver = new RemoteWebDriver(desiredCapabilities);
//        driver.get("http://www.baidu.com");
//        System.out.println(driver.getTitle());
//        driver.close();
//        driver.quit();
//    public static WebDriver getPhantomJs() {
//        String osname = System.getProperties().getProperty("os.name");
//        if (osname.equals("Linux")) {//判断系统的环境win or Linux
//            System.setProperty("phantomjs.binary.path", "/usr/bin/phantomjs");
//        } else {
//            System.setProperty("phantomjs.binary.path", "./phantomjs/win/phantomjs.exe");//设置PhantomJs访问路径
//        }
//        DesiredCapabilities desiredCapabilities = DesiredCapabilities.phantomjs(); //设置参数
//        desiredCapabilities.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
//        desiredCapabilities.setCapability("phantomjs.page.customHeaders.User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
////        if (false) {//是否使用代理
////            org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
////            proxy.setProxyType(org.openqa.selenium.Proxy.ProxyType.MANUAL);
////            proxy.setAutodetect(false);
////            String proxyStr = "";
////            do {
////                proxyStr = ProxyUtil.getProxy();//自定义函数，返回代理ip及端口
////            } while (proxyStr.length() == 0);
////            proxy.setHttpProxy(proxyStr);
////            desiredCapabilities.setCapability(CapabilityType.PROXY, proxy);
////        }
//        return new PhantomJSDriver(desiredCapabilities);
//    }

//    获取方式　　
//
//    try
//    {
//　　　　WebDriver webDriver = PhantomJsUtil.getPhantomJs();
//　　　　webDriver.get(url);
//　　　　SleepUtil.sleep(Constant.SEC_5);
//　　　　PhantomJsUtil.screenshot(webDriver);
//　　　　WebDriverWait wait = new WebDriverWait(webDriver, 10);
//　　　　wait.until(ExpectedConditions.presenceOfElementLocated(By.id(inputId)));//开始打开网页，等待输入元素出现
//　　　　Document document = Jsoup.parse(webDriver.getPageSource());
//
//　　　　//TODO 　剩下页面的获取就按照Jsoup获取方式来做
//
//　　}finally
//    {
//　　　　if (webDriver != null) {
//　　　　　　webDriver.quit();
//　　　　}
//　　}
}
