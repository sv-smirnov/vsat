package ru.rtrn.vsat.entities;

import lombok.Data;
import org.snmp4j.smi.Variable;
import org.springframework.stereotype.Component;


@Data
@Component
public abstract class Device {
    private String Oid;
    private String port;
    private String getCommunity;
    private String setCommunity;
//    private Variable var;
}
