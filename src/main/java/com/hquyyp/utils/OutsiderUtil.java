package com.hquyyp.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Pattern;

public class OutsiderUtil {
    public static String getCpuId() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("wmic cpu get ProcessorId");
            process.getOutputStream().close();
            Scanner sc = new Scanner(process.getInputStream());
            String property = sc.next();
            String serial = sc.next();
            return serial;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String numTransCode(int i) {
        StringBuilder sb = new StringBuilder();
        while (true) {
            i--;
            char ch = (char) (65 + i % 26);
            sb.insert(0, ch);
            i /= 26;
            if (i <= 0)
                return sb.toString();
        }
    }

    public static int codeTransNum(String code) {
        char[] codeChar = code.toCharArray();
        int ans = 0;
        int now = 1;
        for (int i = codeChar.length - 1; i >= 0; i--) {
            ans += (codeChar[i] - 65 + 1) * now;
            now *= 26;
        }
        return ans;
    }

    public static String getUUID() {
        String s = UUID.randomUUID().toString();
        return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
    }


    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }


    public static void streamToFile(InputStream is, FileOutputStream fos) {
        byte[] b = new byte[1024];
        try {
            int length;
            while ((length = is.read(b)) > 0) {
                fos.write(b, 0, length);
            }
            is.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String intToBase64(int data){
        if (data>=255) data=255;
        byte[] DATA=new byte[1];
         DATA[0] = (byte) (data & 0xFF);
        return Base64.encodeBase64String(DATA);
    }


    public static boolean isNotBlank(String rangeString) {
        boolean equals = rangeString.equals(null);
        return equals;
    }
}


/* Location:              F:\freeDemo\士兵对抗系统191.218_V1.0\源代码\scds.jar!\BOOT-INF\classes\com\outsider\hit\scd\\utils\OutsiderUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */