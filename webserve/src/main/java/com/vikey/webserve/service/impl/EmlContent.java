package com.vikey.webserve.service.impl;

import com.vikey.webserve.service.Content;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmlContent implements Content {

    private File emlFile;

    private EmlContent() {

    }

    public EmlContent(File emlFile) {
        this.emlFile = emlFile;
    }


    @Override
    public String getContent() throws IOException, MessagingException {
        Properties props = System.getProperties();
        props.put("mail.host", "smtp.dummydomain.com");
        props.put("mail.transport.protocol", "smtp");

        Session mailSession = Session.getDefaultInstance(props, null);
        InputStream source = new FileInputStream(emlFile);
        MimeMessage message = new MimeMessage(mailSession, source);
        System.out.println("Subject : " + message.getSubject());
        System.out.println("From : " + message.getFrom()[0]);
        System.out.println("--------------");
        System.out.println("Body : " + message.getContent());
        return null;
    }

    @Override
    public void write(String content, File file) throws IOException {

    }
}
