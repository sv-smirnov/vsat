package ru.rtrn.vsat.services;

import org.springframework.stereotype.Service;
import ru.rtrn.vsat.entities.Sdk;
import ru.rtrn.vsat.entities.Station;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class StationService {

    ArrayList<Station> stations;
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
            stations.add(new Station(split[0], split[1], " ", " "));
            line = reader.readLine();
        }
    }

    public void startUpdate() throws IOException {
        ExecutorService threadPool = Executors.newFixedThreadPool(11);
        threadPool.submit(() -> {
            updateValue(0, 20);
        });
        threadPool.submit(() -> {
            updateValue(20, 40);
        });
        threadPool.submit(() -> {
            updateValue(40, 60);
        });
        threadPool.submit(() -> {
            updateValue(60, 80);
        });
        threadPool.submit(() -> {
            updateValue(80, 100);
        });
        threadPool.submit(() -> {
            updateValue(100, 120);
        });
        threadPool.submit(() -> {
            updateValue(120, 140);
        });
        threadPool.submit(() -> {
            updateValue(140, 160);
        });
        threadPool.submit(() -> {
            updateValue(160, 180);
        });
        threadPool.submit(() -> {
            updateValue(180, 200);
        });
        threadPool.submit(() -> {
            updateValue(200, 216);
        });
    }

    private void updateValue(int start, int stop) {
        while (true) {
            try {
                for (int i = start; i < stop; i++) {
                    if (stations.get(i).getIp().equals("10.2.2.1")) System.out.println(LocalDateTime.now());
                    snmpSevice = new SnmpSevice();
                    String val = snmpSevice.snmpGet(stations.get(i).getIp());
                    stations.get(i).setValue(val);
                    if (snmpSevice.checkValue(val)) {
                        if (Double.parseDouble(val) >= 11) stations.get(i).setStatus("Ok");
                        if (Double.parseDouble(val) < 11) stations.get(i).setStatus("Low level");
                    } else stations.get(i).setStatus("Time Out");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Station> getStations() {
        return stations;
    }

    public void startWashing(Station station) {
        try {
            SnmpSevice snmpSeviceSdk = new SnmpSevice(new Sdk());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
