package org.ecjtu.htmlunit;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;

public class Crawl22CM {

    public static void main(String[] args) {
        // https://www.2022cm2.com/videos/4319/843fccb4d5c3a6ec3c5669b367ae0b66/
        //创建web客户端
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(false);
        //设置链接地址
        try {
            HtmlPage page = webClient.getPage("https://www.2022cm2.com/videos/4319/843fccb4d5c3a6ec3c5669b367ae0b66/");
            System.out.println(page.asXml());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
