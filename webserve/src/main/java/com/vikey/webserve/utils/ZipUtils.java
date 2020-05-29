package com.vikey.webserve.utils;

import com.vikey.webserve.entity.Annexe;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    public static void toZip(List<Annexe> as, OutputStream out, String Prefix) throws RuntimeException {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            for (Annexe a : as) {
                byte[] buf = new byte[1024];
                zos.putNextEntry(new ZipEntry(a.getPath()));
                int len;
                FileInputStream in = new FileInputStream(Prefix + File.separator + a.getPath());
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
