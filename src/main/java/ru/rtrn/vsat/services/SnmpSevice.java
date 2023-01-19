package ru.rtrn.vsat.services;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.stereotype.Service;
import ru.rtrn.vsat.entities.Device;
import ru.rtrn.vsat.entities.Vsat;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SnmpSevice {
    private int retries = 2;
    private int timeout = 1000;
    private int version = SnmpConstants.version2c;
    private TransportMapping transport;
    private Snmp snmp;
    private CommunityTarget target;
    private PDU responsePDU;
    private Device device;


    public SnmpSevice() throws IOException {
        transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();
        target = new CommunityTarget();
        PDU responsePDU = null;
        device = new Vsat();
    }

    public SnmpSevice(Device device) throws IOException {
        transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();
        target = new CommunityTarget();
        PDU responsePDU = null;
        this.device = device;
    }

    public String snmpGet(String ip) {
        String value = "";
        try {
            Address address = new UdpAddress(ip + "/" + device.getPort());
            target.setCommunity(new OctetString(device.getCommunity()));
            target.setAddress(address);
            target.setRetries(retries);
            target.setTimeout(timeout);
            target.setVersion(version);
            OID oid = new OID(device.getOid());
            PDU request = new PDU();
            request.setType(PDU.GET);
            request.add(new VariableBinding(oid));
            responsePDU = null;
//            log.info("GET REQUEST : " + selectedStation.getName() + "/" + selectedDevice.getClass().getSimpleName() + " - "+ target.getAddress() + " - " + oid);
            ResponseEvent responseEvent = snmp.send(request, target);
            if (responseEvent != null) {
                responsePDU = responseEvent.getResponse();
                if (responsePDU != null) {
                    int errorStatus = responsePDU.getErrorStatus();
                    String errorStatusText = responsePDU.getErrorStatusText();
                    if (errorStatus == PDU.noError) {
                        value = responsePDU.getVariable(oid).toString();
//                        log.info("GET RESPONSE: " + selectedStation.getName() + "/" + selectedDevice.getClass().getSimpleName() + " - " + target.getAddress() + " - " + responsePDU.getVariableBindings());
                    } else {
                        value = errorStatusText;
//                        log.error("GET RESPONSE: " + target.getAddress() + " - " + errorStatusText);
                    }
                } else {
                    value = "Error: Response PDU is null";
//                    log.error("GET RESPONSE: " + target.getAddress() + " - " + "Response PDU is null");
                }
            } else {
                value = "Error: Agent Timeout... ";
//                log.error("GET RESPONSE: " + target.getAddress() + " - " + "Agent Timeout... ");
            }
        } catch (IOException ee) {
            ee.printStackTrace();
        }

        if (checkValue(value)) {
            value = String.valueOf(Double.parseDouble(value) / 100);
        }
        return value;
    }

    public boolean checkValue(String value) {
        String regex = "^[0-9,.]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public void snmpClose() throws IOException {
        snmp.close();
    }

    public String snmpSet(String ip, int newValue) {
        String value = "";
        try {
            Address address = new UdpAddress(ip + "/" + device.getPort());
            target.setCommunity(new OctetString(device.getCommunity()));
            target.setAddress(address);
            target.setRetries(retries);
            target.setTimeout(timeout);
            target.setVersion(version);
            OID oid = new OID(device.getOid());
            PDU request = new PDU();
            request.setType(PDU.SET);
            Variable var = new Integer32(newValue);
            request.add(new VariableBinding(oid, var));
            responsePDU = null;
//            log.info("GET REQUEST : " + selectedStation.getName() + "/" + selectedDevice.getClass().getSimpleName() + " - "+ target.getAddress() + " - " + oid);
            ResponseEvent responseEvent = snmp.send(request, target);
            if (responseEvent != null) {
                responsePDU = responseEvent.getResponse();
                if (responsePDU != null) {
                    int errorStatus = responsePDU.getErrorStatus();
                    String errorStatusText = responsePDU.getErrorStatusText();
                    if (errorStatus == PDU.noError) {
                        value = responsePDU.getVariable(oid).toString();
//                        log.info("SET RESPONSE: " + selectedStation.getName() + "/" + selectedDevice.getClass().getSimpleName() + " - " + target.getAddress() + " - " + responsePDU.getVariableBindings());
                    } else {
                        value = errorStatusText;
//                        log.error("SET RESPONSE: " + target.getAddress() + " - " + errorStatusText);
                    }
                } else {
                    value = "Error: Response PDU is null";
//                    log.error("SET RESPONSE: " + target.getAddress() + " - " + "Response PDU is null");
                }
            } else {
                value = "Error: Agent Timeout... ";
//                log.error("SET RESPONSE: " + target.getAddress() + " - " + "Agent Timeout... ");
            }
        } catch (IOException ee) {
            ee.printStackTrace();
        }

        if (checkValue(value)) {
            value = String.valueOf(Double.parseDouble(value) / 100);
        }
        return value;
    }

}
