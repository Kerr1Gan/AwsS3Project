package org.ecjtu.selenium;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CrawlEastMoneyCenter {

    public static void main(String[] args) throws IOException {
        SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);
        ChromeDriver driver = SeleniumEngine.getInstance().newDesktopChromeDriver();
        driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
        try {
            int c = 0;
            while (true) {
                try {
                    if (c++ == 0) {
                        driver.get("http://quote.eastmoney.com/center/index.html");
                    } else {
                        driver = SeleniumEngine.getInstance().newDesktopChromeDriver();
                        driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
                        driver.get("http://quote.eastmoney.com/center/index.html");
                    }
                } catch (TimeoutException e) {
                    driver.quit();
                    e.printStackTrace();
                    continue;
                }
                break;
            }
            String source = driver.getPageSource();
            Document jsoup = Jsoup.parse(source);
            Elements tbodys = jsoup.getElementsByTag("tbody");
            Element table = tbodys.get(0);
            Elements tr = table.getElementsByTag("tr");
            tr.remove(0);
            List<CrawlEastMoney.GuPiaoModel> models = new ArrayList<>();
            for (int i = 0; i < tr.size(); i++) {
                Element row = tr.get(i);
                CrawlEastMoney.GuPiaoModel model = new CrawlEastMoney.GuPiaoModel();
                model.daiMa = row.child(0).text();
                model.name = row.child(1).text();
                model.zuiXinJia = row.child(2).text();
                model.zhanDieE = row.child(3).text();
                model.zhanDieFu = row.child(4).text();
                model.chenJiaoLiang = row.child(5).text();
                model.chenJiaoE = row.child(6).text();
                model.zuoShou = row.child(7).text();
                model.jinKai = row.child(8).text();
                model.zuiGao = row.child(9).text();
                model.zuiDi = row.child(10).text();
                model.href = row.child(1).getElementsByTag("a").attr("href");
                models.add(model);
            }
            toJson(models);
        } catch (Exception e) {
            e.printStackTrace();
        }
        driver.quit();
    }

    @SuppressWarnings("Duplicates")
    public static void toJson(List<CrawlEastMoney.GuPiaoModel> list) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("."+File.separator+"res"+File.separator+"eastmoney_center"))) {
            os.writeObject(list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonArray jArr = new JsonArray();
        for (CrawlEastMoney.GuPiaoModel model : list) {
            JsonObject jObj = new JsonObject();
            jObj.addProperty("代码", model.daiMa);
            jObj.addProperty("名字", model.name);
            jObj.addProperty("最新价", model.zuiXinJia);
            jObj.addProperty("涨跌额", model.zhanDieE);
            jObj.addProperty("涨跌幅", model.zhanDieFu);
            jObj.addProperty("成交量", model.chenJiaoLiang);
            jObj.addProperty("成交额", model.chenJiaoE);
            jObj.addProperty("昨收", model.zuoShou);
            jObj.addProperty("今开", model.jinKai);
            jObj.addProperty("最高", model.zuiGao);
            jObj.addProperty("最低", model.zuiDi);
            jObj.addProperty("href", model.href);
            jArr.add(jObj);
        }
        try (FileOutputStream os = new FileOutputStream(new File("."+File.separator+"res"+File.separator+"eastmoney_center.json"))) {
            os.write(jArr.toString().getBytes("utf-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
