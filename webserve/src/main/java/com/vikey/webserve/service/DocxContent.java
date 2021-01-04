package com.vikey.webserve.service;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class DocxContent implements Content {

    private File docxFile;

    public DocxContent(File docxFile) {
        this.docxFile = docxFile;
    }

    @Override
    public String getContent() throws IOException {
        FileInputStream fileInputStream = new FileInputStream(this.docxFile);
        XWPFDocument docx = new XWPFDocument(fileInputStream);
        XWPFWordExtractor extractor = new XWPFWordExtractor(docx);
        return extractor.getText();
    }

    @Override
    public void write(String content, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        XWPFDocument docx = new XWPFDocument();
        XWPFParagraph firstParagraph = docx.createParagraph();
        XWPFRun run = firstParagraph.createRun();
        run.setText(content);
        run.setColor("696969");
        run.setFontSize(16);
        docx.write(fileOutputStream);
    }
}
