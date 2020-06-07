package com.vikey.filetranslateserve;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class FileTranslateServeApplicationTests {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    void test0() {

    }

}
