package org.ecjtu.selenium;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
        ChromeDriver driver = SeleniumEngine.getInstance().newDestopChromeDriver();
        driver.get("http://quote.eastmoney.com/center/list.html#33");
        String source = driver.getPageSource();
        Document doc = Jsoup.parse(source);
        Element body = doc.body();

        List<GuPiaoModel> modelList = null;
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(".\\res\\eastmoney"))) {
            modelList = (List<GuPiaoModel>) is.readObject();
        } catch (Exception e) {
        }
        if (modelList == null) {
            modelList = new ArrayList<>();
        }
        List<Element> aTags = body.getElementById("pagenav").getElementsByTag("a");
        int curPage = 1;
        for (int i = 0; i < aTags.size(); i++) {
            Element a = aTags.get(i);
            int page = Integer.parseInt(a.attr("data-page"));
            if (curPage < page) {
                WebElement next = driver.findElement(By.id("pagenav")).findElement(By.linkText(String.valueOf(page)));
                try {
                    SeleniumEngine.getInstance().desktopClick(next);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                curPage = page;
                i = -1;

                source = driver.getPageSource();
                aTags = body.getElementById("pagenav").getElementsByTag("a");
                doc = Jsoup.parse(source);
                body = doc.body();
                Element table = body.getElementById("fixed");
                table.getElementsByTag("tr");
                List<Element> trList = table.getElementsByTag("tr");
                trList.remove(0);
                for (Element tr : trList) {
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
                    modelList.add(model);
                }
            }
        }
        toJson(modelList);
        driver.quit();
    }

    public static void toJson(List<GuPiaoModel> list) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(".\\res\\eastmoney"))) {
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
            jArr.add(jObj);
        }
        try (FileOutputStream os = new FileOutputStream(new File(".\\res\\eastmoney.json"))) {
            os.write(jArr.toString().getBytes("utf-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static class GuPiaoModel implements Serializable {
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
    }
}