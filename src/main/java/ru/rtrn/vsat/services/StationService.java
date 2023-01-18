package ru.rtrn.vsat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.rtrn.vsat.entities.Station;

import java.io.*;
import java.util.ArrayList;

@Service
public class StationService {

    ArrayList<Station> stations;
    SnmpSevice snmpSevice;

    public StationService() throws IOException {
        snmpSevice = new SnmpSevice();
        stations = new ArrayList<>();
        loadStations();
        updateValues();
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

    public void updateValues() {
        Thread thread = new Thread(
                () -> {
                    while (true) {
                        for (Station station : stations
                        ) {
                            String val = snmpSevice.snmpGet(station.getIp());
                            station.setValue(val);
                            if (snmpSevice.checkValue(val)){
                                if (Double.parseDouble(val) >= 11) station.setStatus("Ok");
                                if (Double.parseDouble(val) < 11) station.setStatus("Low level");
                            }
                            else station.setStatus("Time Out");
                        }
                    }
                }
        );
        thread.start();
    }

    public ArrayList<Station> getStations() {
        return stations;
    }
}
