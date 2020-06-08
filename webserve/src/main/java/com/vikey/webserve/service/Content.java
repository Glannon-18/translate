package com.vikey.webserve.service;

import java.io.File;
import java.io.IOException;

public interface Content {

    String getContent() throws IOException;

    void write(String content, File file) throws IOException;

}
