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
        ChromeDriver driver = new ChromeDriver(capabilities);
        driver.get("http://xkorean.cam/best-asian-body-ever-wc17090704/");
        System.out.println(driver.getPageSource());
    }
}
