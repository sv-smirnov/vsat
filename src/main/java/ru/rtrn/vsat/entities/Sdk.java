package ru.rtrn.vsat.entities;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Sdk extends Device {
    private String Oid = ".1.3.6.1.4.1.31339.33000.2.1.2.1.3.1.0";
    private String port = "5001";
    private String getCommunity = "public";
    private String setCommunity = "private";
//    private Variable var;
}
