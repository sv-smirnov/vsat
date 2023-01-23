package ru.rtrn.vsat.entities;

import lombok.Data;
import org.snmp4j.smi.Variable;
import org.springframework.stereotype.Component;


@Data
@Component
public class Vsat extends Device {
    private String Oid = ".1.3.6.1.4.1.7352.3.5.10.16.8.0";
    private String port = "161";
    private String getCommunity = "public";
    private String setCommunity = "private";
//    private Variable var;
}
