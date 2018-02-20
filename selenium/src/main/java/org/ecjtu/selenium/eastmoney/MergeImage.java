package org.ecjtu.selenium.eastmoney;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MergeImage {

    public static void main(String[] args) {
        List<String> filePath = new ArrayList<>();
        List<Integer> rows = new ArrayList<>();
        String output = args[0];
        for (int i = 1; i < args.length; i++) {
            if (i % 2 != 0) {
                filePath.add(args[i]);
            } else {
                rows.add(Integer.parseInt(args[i]));
            }
        }
        try {
            merge(new File(output), filePath, rows);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("system exit");
    }

    public static void merge(File output, List<String> filePath, List<Integer> rows) throws IOException {
        int width = 0;
        int height = 0;
        int totalHeight = 0;
        int totalWidth = 0;
        BufferedImage[] imgs = new BufferedImage[filePath.size()];
        int curRow = 1;
        for (int i = 0; i < filePath.size(); i++) {
            String path = filePath.get(i);
            int row = 1;
            if (i < rows.size()) {
                row = rows.get(i);
            }
            imgs[i] = ImageIO.read(new File(path));
            width += imgs[i].getWidth();
            if (height < imgs[i].getHeight()) {
                height = imgs[i].getHeight();
            }
            if (curRow < row) {
                totalHeight += height;
                width = 0;
                curRow = row;
            } else if (totalHeight < height) {
                totalHeight = height;
            }
            if (totalWidth < width) {
                totalWidth = width;
            }
        }
        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D gd = (Graphics2D) out.getGraphics();
        gd.setBackground(new Color(255, 255, 255, 0));
        int x = 0;
        for (BufferedImage img : imgs) {
            gd.drawImage(img, x, 0, null);
            x += img.getWidth();
        }
        File tmpFile = new File(output.getParent(), output.getAbsolutePath());
        if (!tmpFile.exists()) {
            tmpFile.createNewFile();
        }
        gd.dispose();
        ImageIO.write(out, "png", tmpFile);
    }
}
