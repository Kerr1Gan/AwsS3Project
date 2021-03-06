package org.ecjtu.selenium.eastmoney;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.ecjtu.selenium.SeleniumEngine;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CrawlEastMoney {

    public static void main(String[] args) throws IOException {
        SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);
        ChromeDriver driver = SeleniumEngine.getInstance().newDesktopChromeDriver();
        try {
            driver.get("http://quote.eastmoney.com/center/list.html#33");
            String source = driver.getPageSource();
            Document doc = Jsoup.parse(source);
            Element body = doc.body();

            List<GuPiaoModel> modelList = null;
//            try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(".\\res\\eastmoney"))) {
//                modelList = (List<GuPiaoModel>) is.readObject();
//            } catch (Exception e) {
//            }
            if (modelList == null) {
                modelList = new ArrayList<>();
            }
            List<Element> aTags = body.getElementsByClass("pagination").get(0).getElementsByTag("a");
            int curPage = 1;
            int page = Integer.valueOf(aTags.get(aTags.size() - 2).text());
            String oldText = "";
            for (int i = 0; i < page; i++) {
                if (curPage <= page) {
                    source = driver.getPageSource();
                    doc = Jsoup.parse(source);
                    body = doc.body();
                    List<Element> table = body.getElementsByTag("tbody").get(0).getElementsByTag("tr");
                    String newText = table.get(0).text();
                    int retryCount = 0;
                    while (newText.equals(oldText)) {
                        Thread.sleep(300);
                        source = driver.getPageSource();
                        doc = Jsoup.parse(source);
                        body = doc.body();
                        table = body.getElementsByTag("tbody").get(0).getElementsByTag("tr");
                        newText = table.get(0).text();
                        if (retryCount++ > 3) {
                            oldText = "";
                        }
                    }
                    oldText = newText;
                    for (Element tr : table) {
                        GuPiaoModel model = new GuPiaoModel();
                        model.daiMa = tr.child(1).text();
                        model.name = tr.child(2).text();
                        model.zuiXinJia = tr.child(4).text();
                        model.zhanDieE = tr.child(5).text();
                        model.zhanDieFu = tr.child(6).text();
                        model.zhengFu = tr.child(7).text();
                        model.chenJiaoLiang = tr.child(8).text();
                        model.chenJiaoE = tr.child(9).text();
                        model.zuoShou = tr.child(10).text();
                        model.jinKai = tr.child(11).text();
                        model.zuiGao = tr.child(12).text();
                        model.zuiDi = tr.child(13).text();
                        model.fiveFenZhongZhangDie = tr.child(14).text();
                        model.href = tr.child(1).getElementsByTag("a").attr("href");
                        modelList.add(model);
                    }
                    curPage++;
                } else {
                    break;
                }
                WebElement next = null;
                try {
                    next = driver.findElement(By.id("common_table_next"));
                } catch (Exception e) {
                    e.printStackTrace();
                    i--;
                    if (curPage == 175) {
                        break;
                    }
                    continue;
                }
                try {
                    next.click();
                } catch (Exception e) {
                }
            }
            toJson(modelList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        driver.quit();
    }

    public static void toJson(List<GuPiaoModel> list) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("."+File.separator+"res"+File.separator+"eastmoney"))) {
            os.writeObject(list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonArray jArr = new JsonArray();
        for (GuPiaoModel model : list) {
            JsonObject jObj = new JsonObject();
            jObj.addProperty("代码", model.daiMa);
            jObj.addProperty("名字", model.name);
            jObj.addProperty("最新价", model.zuiXinJia);
            jObj.addProperty("涨跌额", model.zhanDieE);
            jObj.addProperty("涨跌幅", model.zhengFu);
            jObj.addProperty("成交量", model.chenJiaoLiang);
            jObj.addProperty("成交额", model.chenJiaoE);
            jObj.addProperty("昨收", model.zuoShou);
            jObj.addProperty("今开", model.jinKai);
            jObj.addProperty("最高", model.zuiGao);
            jObj.addProperty("最低", model.zuiDi);
            jObj.addProperty("5分钟涨跌额", model.fiveFenZhongZhangDie);
            jObj.addProperty("href", model.href);
            jArr.add(jObj);
        }
        try (FileOutputStream os = new FileOutputStream(new File("."+File.separator+"res"+File.separator+"eastmoney.json"))) {
            os.write(jArr.toString().getBytes("utf-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static class GuPiaoModel implements Serializable {
        private static final long serialVersionUID = 1L;
        public String daiMa;
        public String name;
        public String zuiXinJia;
        public String zhanDieE;
        public String zhanDieFu;
        public String zhengFu;
        public String chenJiaoLiang;
        public String chenJiaoE;
        public String zuoShou;
        public String jinKai;
        public String zuiGao;
        public String zuiDi;
        public String fiveFenZhongZhangDie;
        public String href;
    }
}
