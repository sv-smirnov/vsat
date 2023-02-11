package ru.rtrn.vsat.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.rtrn.vsat.entities.Device;
import ru.rtrn.vsat.entities.Sdk;
import ru.rtrn.vsat.entities.Station;
import ru.rtrn.vsat.entities.Vsat;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class StationService {



    ArrayList<Station> stations;
    Logger log = LoggerFactory.getLogger(StationService.class);

    @Autowired
    SnmpSevice snmpSevice;


    public StationService() throws IOException {
        stations = new ArrayList<>();
        loadStations();
        startUpdate();
    }

    public void loadStations() throws IOException {
        File file = new File("Stations.txt");
        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader);
        String line = reader.readLine();
        while (line != null) {
            String[] split = line.split(" ");
            stations.add(new Station(split[0], split[1], split[2], " ", " ", " "));
            line = reader.readLine();
        }
    }

    public void startUpdate() throws IOException {
        int n = 10;
        ExecutorService threadPool = Executors.newFixedThreadPool(n);
        int dN = stations.size() / n;
        int dNR = stations.size() % n;

//        for(int i = 0; i < n - 1; i++) {
//            int start = i;
//            int stop = i + 1;
//            threadPool.submit(() -> {
//                updateValue(start*dN,stop*dN);
//            });
//        }
//        threadPool.submit(() -> {
//            updateValue((n-1)*dN,((n)*dN+dNR));
//        });
    }

    private void updateValue(int start, int stop) {
        try {
            snmpSevice = new SnmpSevice();
            Device vsat = new Vsat();
            while (true) {
                for (int i = start; i < stop; i++) {
                    String val = snmpSevice.snmpGet(vsat, stations.get(i).getIp());
                    stations.get(i).setValue(val);
                    if (snmpSevice.checkValue(val)) {
                        if (Double.parseDouble(val) >= 11) {
                            stations.get(i).setStatus("Ok");
                        }
                        if (Double.parseDouble(val) < 11) {
                            if (stations.get(i).getStatus().equals("Washing...")) {
                                continue;
                            }
                            stations.get(i).setStatus("Low level");
//  TODO для полноценной работы нужно убрать проверку "10.2.27.1"
                            if (stations.get(i).getIp().equals("10.2.27.1")) {
                                stations.get(i).setStatus("Washing...");
                                startWashing(stations.get(i));
                            }
                        }
                    } else {
                        log.warn(stations.get(i).getIp() + " - " + val);
                        stations.get(i).setStatus("Time Out");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Station> getStations() {
        return stations;
    }

    public void startWashing(Station alarmStation) throws InterruptedException {
        final SnmpSevice[] snmpSeviceSdk = new SnmpSevice[1];
        Thread washProcess = new Thread(() -> {
            try {
                snmpSeviceSdk[0] = new SnmpSevice();
                Device sdk = new Sdk();
                String newIp = alarmStation.getIp().substring(0, alarmStation.getIp().length() - 1) + "2";
                String releStatus = snmpSeviceSdk[0].snmpGet(sdk, newIp);
                if (releStatus.equals("0")) {

                    washing(alarmStation, snmpSeviceSdk[0], sdk, newIp, 1, ": start washing");
                    Thread.sleep(20000);
                    washing(alarmStation, snmpSeviceSdk[0], sdk, newIp, 0, ": stop washing");
                    Thread.sleep(120000);

                } else {
                    releStatus = snmpSeviceSdk[0].snmpSet(sdk, newIp, 0);
                    alarmStation.setRele(parseReleStatus(releStatus));
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                log.info(alarmStation.getIp() + " - " + alarmStation.getValue() + ": finish washing");
                alarmStation.setStatus("Ready");
                try {
                    snmpSeviceSdk[0].snmpClose();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Thread.currentThread().interrupt();
            }
        });
        washProcess.start();
        washProcess.join();
    }

    private void washing(Station alarmStation, SnmpSevice snmpSeviceSdk, Device sdk, String newIp, int releValue, String logInfo) throws InterruptedException {
        String releStatus;
        releStatus = snmpSeviceSdk.snmpSet(sdk, newIp, releValue);
        log.info(alarmStation.getIp() + " - " + alarmStation.getValue() + logInfo);
        alarmStation.setRele(parseReleStatus(releStatus));
    }

    public String parseReleStatus(String val) {
        String s = " ";
        if (val.equals("0")) s = "Off";
        else if (val.equals("1")) s = "On";
        else s = "Error";
        return s;
    }
}
