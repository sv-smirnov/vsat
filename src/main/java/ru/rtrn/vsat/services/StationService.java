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
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class StationService {

    ArrayList<Station> stations;
    HashMap<String, Station> mapStations;

    Logger log = LoggerFactory.getLogger(StationService.class);

    @Autowired
    SnmpSevice snmpSevice;


    public StationService() throws IOException {
        stations = new ArrayList<>();
        mapStations = new HashMap<>();
        loadStations();
        startUpdate();
    }

    public void loadStations() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("Stations.txt"))) {
            String line = reader.readLine();
            while (line != null) {
                String[] split = line.split(" ");
                Station station = new Station(split[0], split[1], split[2], " ", " ", " ");
                stations.add(station);
                mapStations.put(split[0], station);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // FileReader fileReader = new FileReader(file);
        // BufferedReader reader = new BufferedReader(fileReader);
        // String line = reader.readLine();
        // while (line != null) {
        //     String[] split = line.split(" ");
        //     Station station = new Station(split[0], split[1], split[2], " ", " ", " ");
        //     stations.add(station);
        //     mapStations.put(split[0], station);
        //     line = reader.readLine();
        // }
    }

    public void startUpdate() throws IOException {
        int n = 10;
        ExecutorService threadPool = Executors.newFixedThreadPool(n);
        int dN = mapStations.size() / n;
        int dNR = mapStations.size() % n;

       for(int i = 0; i < n - 1; i++) {
           int start = i;
           int stop = i + 1;
           threadPool.submit(() -> {
               updateValue(start*dN,stop*dN);
           });
       }
       threadPool.submit(() -> {
           updateValue((n-1)*dN,((n)*dN+dNR+1));
       });
    }

    private void updateValue(int start, int stop) {
        try {
            snmpSevice = new SnmpSevice();
            Device vsat = new Vsat();
            while (true) {
                for (int i = start; i < stop; i++) {
                    if (i == 0) {
                        i++;
                    }
                    String n = String.valueOf(i);
                    String val = snmpSevice.snmpGet(vsat, mapStations.get(n).getIp());
                    mapStations.get(n).setValue(val);
                    if (snmpSevice.checkValue(val)) {
                        if (Double.parseDouble(val) >= 11) {
                            setStationStatus(n, "Ok");
                        }
                        if (Double.parseDouble(val) < 11) {
                            if (!mapStations.get(n).getStatus().equals("Washing...") || !mapStations.get(n).getStatus().equals("Sleep")) {
                            
                                setStationStatus(n, "Low level");
    //  TODO для полноценной работы нужно убрать проверку "10.2.27.1"
                                if (mapStations.get(n).getIp().equals("10.2.27.1")) {
                                    setStationStatus(n, "Washing...");
                                    startWashing(mapStations.get(n));
                                }
                            }
                        }
                    } else {
                        if (!mapStations.get(n).getStatus().equals("Sleep")) {
                            log.warn(mapStations.get(n).getIp() + " - " + val);
                            setStationStatus(n, "Time Out");
                        }
                            
                        
                        
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setStationStatus(String i, String text) {
        mapStations.get(i).setStatus(text);
    }

    public ArrayList<Station> getStations() {
        return stations;
    }

    public HashMap<String, Station> getMapStations() {
        return mapStations;
    }


    public void startWashing(Station alarmStation) throws InterruptedException {
        final SnmpSevice[] snmpSeviceSdk = new SnmpSevice[1];
        Thread washProcess = new Thread(() -> {
            try {
                snmpSeviceSdk[0] = new SnmpSevice();
                Device sdk = new Sdk();
                String releStatus = snmpSeviceSdk[0].snmpGet(sdk, getNewIp(alarmStation));
                if (releStatus.equals("0")) {

                    washing(alarmStation, snmpSeviceSdk[0], sdk, 1, ": start washing");
                    Thread.sleep(20000);
                    washing(alarmStation, snmpSeviceSdk[0], sdk, 0, ": stop washing");
                    Thread.sleep(120000);

                } else {
                    releStatus = snmpSeviceSdk[0].snmpSet(sdk, getNewIp(alarmStation), 0);
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

    public void stopWashing(String number) throws IOException, InterruptedException {
        if (mapStations.get(number).getStatus().equals("Sleep")) {
            setStationStatus(number, "Ready");
        } else {
            Station alarmStation = mapStations.get(number);
            SnmpSevice[] snmpSeviceSdk = new SnmpSevice[1];
            snmpSeviceSdk[0] = new SnmpSevice();
            Device sdk = new Sdk();
            setStationStatus(alarmStation.getId(), "Sleep");
            washing(alarmStation, snmpSeviceSdk[0], sdk, 0, ": stop washing");
        }
    }

    private static String getNewIp(Station alarmStation) {
        return alarmStation.getIp().substring(0, alarmStation.getIp().length() - 1) + "2";
    }

    private void washing(Station alarmStation, SnmpSevice snmpSeviceSdk, Device sdk, int releValue, String logInfo) throws InterruptedException {
        String releStatus;
        releStatus = snmpSeviceSdk.snmpSet(sdk, getNewIp(alarmStation), releValue);
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
