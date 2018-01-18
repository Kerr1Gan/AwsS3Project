package org.ecjtu.selenium;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CrawlEastMoneyImage {
    private static final SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat("yyyy-M-d");

    public static void main(String[] args) throws IOException {
        SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);
        ChromeDriver driver = SeleniumEngine.getInstance().newDestopChromeDriver();
        BufferedReader reader = new BufferedReader(new FileReader(new File("res\\config.txt")));
        String line = reader.readLine();
        String[] params = line.split(" ");
        int[] metric = new int[params.length];
        int index = 0;
        for (String param : params) {
            metric[index++] = Integer.parseInt(param);
        }
        List<CrawlEastMoney.GuPiaoModel> modelList = null;
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(".\\res\\eastmoney"))) {
            modelList = (List<CrawlEastMoney.GuPiaoModel>) is.readObject();
        } catch (Exception e) {
        }
        if (modelList != null) {
            File dir = new File(".\\res\\", sSimpleDateFormat.format(new Date()));
            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdir();
            }
            for (CrawlEastMoney.GuPiaoModel model : modelList) {
                File image0 = new File(dir.getAbsolutePath() + "\\" + model.daiMa + model.name, "emchart-0.png");
                File image1 = new File(dir.getAbsolutePath() + "\\" + model.daiMa + model.name, "emchartk.png");
                if (image0.exists() && image1.exists()) {
                    continue;
                }
                try {
                    try {
                        driver.get(model.href);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
//                    try {
//                        Actions actions = new Actions(driver);
//                        actions.sendKeys(Keys.ESCAPE).perform();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    File saveDir = new File(dir, model.daiMa + model.name);
                    if (!saveDir.exists() || !saveDir.isDirectory()) {
                        saveDir.mkdirs();
                    }

                    WebElement element = driver.findElement(By.id("emchart-0"));
                    Actions action = new Actions(driver);
                    action.moveToElement(element).perform();
                    element = driver.findElement(By.id("emchart-0"));
                    Dimension size = element.getSize();
                    File file = driver.getScreenshotAs(FILE);
                    BufferedImage image = ImageIO.read(file);
                    BufferedImage subImage = image.getSubimage(metric[0], metric[1], metric[2], metric[3]);
                    File tmpFile = new File(saveDir, "emchart-0.png");
                    if (!tmpFile.exists()) {
                        tmpFile.createNewFile();
                    }
                    ImageIO.write(subImage, "png", tmpFile);

                    element = driver.findElement(By.id("emchartk"));
                    action = new Actions(driver);
                    action.moveToElement(element).perform();
                    new Actions(driver).moveByOffset(0, -metric[7] / 2).perform();
                    file = driver.getScreenshotAs(FILE);
                    image = ImageIO.read(file);
                    subImage = image.getSubimage(metric[4], metric[5], metric[6], metric[7]);
                    tmpFile = new File(saveDir, "emchartk.png");
                    if (!tmpFile.exists()) {
                        tmpFile.createNewFile();
                    }
                    ImageIO.write(subImage, "png", tmpFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
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
                File tmpFile = new File("res", "tmp.png");
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
