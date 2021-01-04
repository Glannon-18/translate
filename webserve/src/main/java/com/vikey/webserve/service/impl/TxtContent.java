package com.vikey.webserve.service.impl;

import com.vikey.webserve.service.Content;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class TxtContent implements Content {

    private File translate_file;

    public TxtContent(File translate_file) {
        this.translate_file = translate_file;
    }

    @Override
    public String getContent() throws IOException {
        return FileUtils.readFileToString(translate_file, "utf-8");
    }

    @Override
    public void write(String content, File file) throws IOException {
        FileUtils.write(file, content, "utf-8");
    }
}
