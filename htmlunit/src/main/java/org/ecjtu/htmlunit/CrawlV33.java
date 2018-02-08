package org.ecjtu.htmlunit;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CrawlV33 {

    private static Map<String, List<V33Model>> sMap;

    public static void main(String[] args) throws IOException {
        try {
            Field field = BrowserVersion.CHROME.getClass().getDeclaredField("userAgent_");
            field.setAccessible(true);
            field.set(BrowserVersion.CHROME, "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E)  Chrome/60.0.3112.90 Mobile Safari/537.36");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(new File(".\\res\\v33a")))) {
            sMap = (Map<String, List<V33Model>>) is.readObject();
        } catch (Exception e) {
        } finally {
            if (sMap == null) {
                sMap = new LinkedHashMap<>();
            }
        }

        //创建web客户端
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
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
        sMap.put("main", result);

        // crawl by category
        Element elements = body.getElementById("dh1");
        Elements aTags = elements.getElementsByTag("a");
        List<String> urls = new ArrayList<>();
        for (int i = 0; i < aTags.size(); i++) {
            String url = aTags.get(i).attr("href");
            if (!url.equals("") && !url.contains("http") && aTags.get(i).attr("style").equals("")) {
                urls.add(page.getBaseURI() + url.replace("/", ""));
            }
        }
        for (String url : urls) {
            try {
                releaseWebClient(webClient, page);
                webClient = createWebClient();
                  page = webClient.getPage(url);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            body = Jsoup.parse(page.asXml()).body();
            childs = body.getElementsByClass("hm");
            result = new ArrayList<>();
            Element item = childs.get(0);
            Element aTag = item.getElementsByTag("a").get(0);
            Element imgTag = item.getElementsByTag("img").get(0);
            String href = aTag.attr("href");
            String imgUrl = imgTag.attr("name");
            String title = imgTag.attr("alt");
            V33Model model = new V33Model();
            int index;
            while (true) {
                try {
                    index = Integer.parseInt(href);
                    break;
                } catch (Exception e) {
                    href = href.substring(1);
                }
            }
            model.setBaseUrl(page.getBaseURI() + index);
            model.setImageUrl(imgUrl);
            model.setTitle(title);
            result.add(model);
            String key = title.split(" ")[0];

            for (int i = index - 1; i > 0; i--) {
                model = new V33Model();
                model.setBaseUrl(page.getBaseURI() + i);
                String base = page.getBaseURI().substring(page.getBaseURI().lastIndexOf("/"));
                model.setImageUrl(imgUrl.substring(0, imgUrl.lastIndexOf("/")) + base + i + "+350");
                model.setTitle(null);
                result.add(model);
            }
            sMap.put(key, result);
        }

        Thread thread = applyDaemon();
        // crawl video url
        for (Map.Entry<String, List<V33Model>> item : sMap.entrySet()) {
            for (V33Model model : item.getValue()) {
                if (model.getVideoUrl() != null && !model.getVideoUrl().equals("")) {
                    continue;
                }
                try {
                    releaseWebClient(webClient, page);
                    webClient = createWebClient();
                    page = webClient.getPage(model.getBaseUrl());
                    jsoup = Jsoup.parse(page.asXml());
                    body = jsoup.body();
                    Element video = body.getElementById("nb");
                    String videoUrl = video.getElementsByTag("source").get(0).attr("src");
                    String title = body.getElementById("pt1").text();
                    if (model.getTitle() == null) {
                        model.setTitle(title);
                    }
                    model.setVideoUrl(videoUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
        //关闭所有窗口
        webClient.close();
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
        }
        System.out.println("exit");
    }

    private static Thread applyDaemon() {
        Thread thread = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                File cache = new File(".\\res\\v33a_tmp");
                if (cache.exists()) {
                    cache.delete();
                }
                try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(cache))) {
                    os.writeObject(ObjectUtil.deepCopy(sMap));
                    FileUtils.copyFile(cache, new File(".\\res\\v33a"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    public static void releaseWebClient(WebClient client, HtmlPage page) {
        page.remove();
        page.removeAllChildren();
        page.cleanUp();
        client.getCurrentWindow().getJobManager().removeAllJobs();
        client.close();
    }

    public static WebClient createWebClient() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(false);
        return webClient;
    }
}
