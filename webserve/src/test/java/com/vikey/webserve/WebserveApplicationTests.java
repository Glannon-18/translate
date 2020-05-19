package com.vikey.webserve;

import com.vikey.webserve.service.IAnnexe_taskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WebserveApplicationTests {

    @Autowired
    private IAnnexe_taskService IAnnexe_taskService;

    @Test
    void contextLoads() {
        IAnnexe_taskService.getById(1l);

    }

}
