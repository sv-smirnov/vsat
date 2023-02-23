package ru.rtrn.vsat;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class VsatApplication {

    public static void main(String[] args) {
//        SpringApplication.run(VsatApplication.class, args);
        new SpringApplicationBuilder(VsatApplication.class)
                .headless (false)
                .run (args);
    }

}
