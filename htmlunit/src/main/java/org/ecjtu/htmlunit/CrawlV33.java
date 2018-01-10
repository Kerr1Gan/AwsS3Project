package org.ecjtu.htmlunit;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CrawlV33 {
    public static void main(String[] args) throws IOException {
        //创建web客户端
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(false);
        //设置链接地址
        HtmlPage page = webClient.getPage("http://v.33k.im");

        //获取页面的Xml代码
        final String pageAsXml = page.asXml();
        System.out.println(pageAsXml);
        Document jsoup = Jsoup.parse(pageAsXml);
        Element body = jsoup.body();
        Elements childs = body.getElementsByClass("hm");
        List<V33Model> result = new ArrayList<>();
        for (int i = 0; i < childs.size(); i++) {
            Element item = childs.get(i);
            Element aTag = item.getElementsByTag("a").get(0);
            Element imgTag = item.getElementsByTag("img").get(0);
            String url = aTag.attr("href");
            String imgUrl = imgTag.attr("name");
            String title = imgTag.attr("alt");
            V33Model model = new V33Model();
            model.setBaseUrl(page.getBaseURI() + url);
            model.setImageUrl(imgUrl);
            model.setTitle(title);
            result.add(model);
        }

        page = webClient.getPage("http://v.33k.im/ps444");
        jsoup = Jsoup.parse(page.asXml());
        body = jsoup.body();
        System.out.println(body.toString());
        Element video = body.getElementById("nb");
        //关闭所有窗口
        webClient.close();
    }
}
