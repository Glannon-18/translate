package com.vikey.webserve.utils;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DocxUtils {

    public static String readDocx(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        XWPFDocument docx = new XWPFDocument(fileInputStream);
        XWPFWordExtractor extractor = new XWPFWordExtractor(docx);
        return extractor.getText();
    }

}
