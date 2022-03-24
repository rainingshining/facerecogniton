package com.hc.facerecogniton.utils;

import org.springframework.util.StringUtils;

import java.io.*;
import java.math.BigDecimal;

public class CompoundUtils {

    public static int plusHundred(Float value) {
        BigDecimal target = new BigDecimal(value);
        BigDecimal hundred = new BigDecimal(100f);
        return target.multiply(hundred).intValue();
    }

    public static String base64Process(String base64Str) {
        if (!StringUtils.isEmpty(base64Str)) {
            String photoBase64 = base64Str.substring(0, 30).toLowerCase();
            int indexOf = photoBase64.indexOf("base64,");
            if (indexOf > 0) {
                base64Str = base64Str.substring(indexOf + 7);
            }

            return base64Str;
        } else {
            return "";
        }
    }

    public static String getAbsolutePath(String file) {
        return getAbsolutePath(new File(file));
    }

    public static String getAbsolutePath(File file) {
        return file.getAbsolutePath();
    }

    //getResourceAsStream以JAR中根路径为开始点
    private synchronized static void loadLib(String libName) throws IOException {

        String systemType = System.getProperty("os.name");
        String libExtension = (systemType.toLowerCase().indexOf("win") != -1) ? ".dll" : ".so";
        String libFullName = libName + libExtension;
        String nativeTempDir = System.getProperty("java.io.tmpdir");
        InputStream in = null;
        BufferedInputStream reader = null;
        FileOutputStream writer = null;
        File extractedLibFile = new File(nativeTempDir + File.separator + libFullName);

        if (!extractedLibFile.exists()) {
            try {
                in = CompoundUtils.class.getResourceAsStream(libFullName);
                if (in == null){
                    in = CompoundUtils.class.getResourceAsStream(libFullName);
                }
                CompoundUtils.class.getResource(libFullName);
                reader = new BufferedInputStream(in);
                writer = new FileOutputStream(extractedLibFile);
                byte[] buffer = new byte[1024];
                while (reader.read(buffer) > 0) {
                    writer.write(buffer);
                    buffer = new byte[1024];
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null){
                    in.close();
                }
                if (writer != null){
                    writer.close();
                }
            }
        }
        System.load(extractedLibFile.toString());
    }
}
