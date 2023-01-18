package ru.rtrn.vsat.entities;

import lombok.Data;


@Data
public abstract class Device {
    private String Oid;
    private String port;
    private String community;

}
