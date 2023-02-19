package ru.rtrn.vsat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import ru.rtrn.vsat.gui.Gui;

@SpringBootApplication
public class VsatApplication {

    public static void main(String[] args) {
//        SpringApplication.run(VsatApplication.class, args);
        new SpringApplicationBuilder(VsatApplication.class)
                .headless (false)
                .run (args);
    }

}
