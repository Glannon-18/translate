package com.vikey.webserve;

import com.vikey.webserve.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WebserveApplicationTests {

    @Autowired
    private IUserService IUserService;

    @Test
    void contextLoads() {

        IUserService.selectUserWithRoles(new Long("1"));
//        IUserService.getById(1l);

    }


}
