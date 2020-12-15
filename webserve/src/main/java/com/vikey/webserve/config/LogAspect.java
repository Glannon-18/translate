package com.vikey.webserve.config;


import com.vikey.webserve.controller.LibraryController;
import com.vikey.webserve.entity.User;
import com.vikey.webserve.utils.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);


    @Pointcut("@annotation(com.vikey.webserve.config.Log)")
    public void log() {

    }

    @After("log()")
    public void doAfter(JoinPoint joinPoint) {


        User user = SecurityUtils.getCurrentUser();
        LOGGER.info("操作人id：" + user.getId());
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Log annotation = signature.getMethod().getAnnotation(Log.class);
        LOGGER.info("操作模块" + annotation.operModul());
        LOGGER.info("操作类型" + annotation.operType());
        LOGGER.info("操作说明" + annotation.operDesc());

        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            LOGGER.info(arg.getClass().getName());
            LOGGER.info(arg.getClass().getTypeName());
            LOGGER.info(arg.getClass().getCanonicalName());
            LOGGER.info(arg.getClass().getSimpleName());

        }


    }

}
