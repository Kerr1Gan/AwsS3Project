package org.ecjtu.selenium;

import java.io.File;

public class Test {
    public static void main(String[] args) {
        File file = new File("res\\videoImage");
        if (!file.isDirectory()) {
            file.mkdirs();
        }
        for (File item : file.listFiles()) {
            int count = appearNumber(item.getName(), "_");
            if (item.getName().startsWith("image")) {
                item.renameTo(new File(String.format("firststorage0001_" + item.getName())));
            }
        }
    }

    public static int appearNumber(String srcText, String findText) {
        int count = 0;
        int index = 0;
        while ((index = srcText.indexOf(findText, index)) != -1) {
            index = index + findText.length();
            count++;
        }
        return count;
    }
}
