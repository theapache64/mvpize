package com.theah64.mvpize.utils;

import java.io.*;

public class FileUtils {
    public static String read(File file) throws IOException {
        final BufferedReader br = new BufferedReader(new FileReader(file));
        final StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();
        return sb.toString();
    }
}
