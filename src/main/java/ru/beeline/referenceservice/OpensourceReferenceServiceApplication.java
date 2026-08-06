/*
 * Copyright (c) 2024 PJSC VimpelCom
 */
package ru.beeline.referenceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class OpensourceReferenceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpensourceReferenceServiceApplication.class, args);
    }
}
