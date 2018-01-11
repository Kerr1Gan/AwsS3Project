package org.ecjtu.selenium;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUtil {
    //定义下载路径
    private String path;
    //指定所下载的文件的保存位置
    private String targetFile;
    //定义下载线程的数量
    private int threadNum;
    //定义下载线程的对象
    private DownThread[] threads;
    //下载文件的总大小
    private int fileSize;

    public DownloadUtil(String path, String targetFile, int threadNum) {
        this.path = path;
        this.targetFile = targetFile;
        this.threadNum = threadNum;
        threads = new DownThread[threadNum];
        this.targetFile = targetFile;
    }

    public void downLoad() throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        //设置 URL 请求的方法， GET POST HEAD OPTIONS PUT DELETE TRACE 以上方法之一是合法的，具体取决于协议的限制。
        conn.setRequestMethod("GET");
        //设置一般请求属性。如果已存在具有该关键字的属性，则用新值改写其值。
        conn.setRequestProperty("Accept",
                "image/gif,image/jpeg,image/pjpeg,image/pjpeg, "
                        + "application/x-shockwave-flash, application/xaml+xml, "
                        + "application/vnd.ms-xpsdocument, application/x-ms-xbap"
                        + "application/x-ms-application,application/vnd.ms-excel"
                        + "application/vnd.ms-powerpoint, application/msword,*/*");
        conn.setRequestProperty("Accept-Language", "zh_CN");
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty("Connection", "Keep-Alive");
//        conn.setRequestProperty("User-Agent",);

        //得到文件大小
        fileSize = conn.getContentLength();
        conn.disconnect();
        int currentPartSize = fileSize / threadNum + 1;
        RandomAccessFile file = new RandomAccessFile(targetFile, "rw");
        //设置本地文件大小
        file.setLength(fileSize);
        file.close();
        for (int i = 0; i < threadNum; i++) {
            //计算每个线程的下载位置
            int startPos = i * currentPartSize;
            //每个线程使用一个RandomAccessFile进行下载
            RandomAccessFile currentPart = new RandomAccessFile(targetFile, "rw");
            //定位该线程的下载位置
            currentPart.seek(startPos);
            //创建下载线程
            threads[i] = new DownThread(startPos, currentPartSize, currentPart);
            threads[i].start();
        }
    }

    public double getCompleteRate() {
        int sumSize = 0;
        for (int i = 0; i < threadNum; i++) {
            sumSize += threads[i].length;
        }
        return sumSize * 1.0 / fileSize;
    }

    private class DownThread extends Thread {
        //当前线程的下载位置
        private int startPos;
        //定义当前线程负责下载的文件大小
        private int currentPartSize;
        //当前线程需要下载的文件块,此类的实例支持对随机访问文件的读取和写入。
        private RandomAccessFile currentPart;
        //定义该线程已下载的字节数
        public int length;

        private DownThread(int startPos, int currentPartSize, RandomAccessFile currentPart) {
            this.startPos = startPos;
            this.currentPartSize = currentPartSize;
            this.currentPart = currentPart;
        }

        public void run() {
            try {
                URL url = new URL(path);
                //url.openConnection():返回一个 URLConnection 对象，它表示到 URL 所引用的远程对象的连接。
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5 * 1000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "image/gif,image/jpeg,image/pjpeg,image/pjpeg, "
                        + "application/x-shockwave-flash, application/xaml+xml, "
                        + "application/vnd.ms-xpsdocument, application/x-ms-xbap"
                        + "application/x-ms-application,application/vnd.ms-excel"
                        + "application/vnd.ms-powerpoint, application/msword,*/*");
                conn.setRequestProperty("Accept-Language", "zh_CN");
                conn.setRequestProperty("Charset", "UTF-8");
                InputStream inputStream = conn.getInputStream();
                //inputStream.skip(n);跳过和丢弃此输入流中数据的 n 个字节
                inputStream.skip(this.startPos);
                byte[] buffer = new byte[1024];
                int hasRead = 0;
                //读取网络数据写入本地
                while (length < currentPartSize && (hasRead = inputStream.read(buffer)) != -1) {
                    currentPart.write(buffer, 0, hasRead);
                    length += hasRead;
                }
                currentPart.close();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        final DownloadUtil downUtil = new DownloadUtil("https://ss0.baidu.com/"
                + "6ONWsjip0QIZ8tyhnq/it/u=1927822194,1885130936&fm=80&w=179&h=119&img.JPEG", "11.JPEG", 4);
        downUtil.downLoad();
        new Thread(() -> {
            while (downUtil.getCompleteRate() < 1) {
                System.out.println("已完成: " + downUtil.getCompleteRate());
                try {//每隔0.1秒查询一次任务
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("已完成: " + downUtil.getCompleteRate());
        }).start();
    }
}