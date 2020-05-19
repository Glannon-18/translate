package com.vikey.webserve;

import com.vikey.webserve.entity.User;
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

      User u=  IUserService.selectUserWithRoles(new Long("1"));
        System.out.println(u.toString());

//        IUserService.getById(1l);

    }


}
