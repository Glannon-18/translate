package com.vikey.webserve.service;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;

public interface Content {

    String getContent() throws IOException, MessagingException;

    void write(String content, File file) throws IOException;

}
