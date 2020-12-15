package com.vikey.webserve.utils;

import com.vikey.webserve.entity.Annexe;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    public static void toZip(List<Annexe> as, OutputStream out, String Prefix) throws IOException {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            for (Annexe a : as) {
                byte[] buf = new byte[1024];
//                zos.putNextEntry(new ZipEntry(a.getPath()));
                String name = a.getName();
                int n_name = name.lastIndexOf(".");
                if (n_name > 0)
                    name = name.substring(0, n_name) + "--译文" + name.substring(n_name);
                zos.putNextEntry(new ZipEntry(name));
                int len;
                FileInputStream in = new FileInputStream(Prefix + File.separator + a.getTranslate_path());
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("选中的下载文件右部分已经丢失");
        } catch (IOException e) {
            throw new IOException("文件流异常");
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
