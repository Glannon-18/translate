package com.vikey.webserve.utils;

import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.core.FileURIResolver;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.ByteArrayOutputStream;
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

    public static String docx2Html(String fileName) throws IOException {
        XWPFDocument document = new XWPFDocument(new FileInputStream(fileName));
        XHTMLOptions options = XHTMLOptions.create().indent(4);
        // 导出图片
        File imageFolder = new File("d://docx_img");
        options.setExtractor(new FileImageExtractor(imageFolder));
        // URI resolver
        options.URIResolver(new FileURIResolver(imageFolder));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XHTMLConverter.getInstance().convert(document, byteArrayOutputStream, options);
        return new String((byteArrayOutputStream.toByteArray()), "utf-8");
    }

}
