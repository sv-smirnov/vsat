package ru.rtrn.vsat.controllers;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ru.rtrn.vsat.entities.Station;
import ru.rtrn.vsat.services.StationService;

@RestController
public class StationsController {

    @Autowired
    private StationService stationService;

    // @CrossOrigin
    @GetMapping("/list")
    public Map<String, Station> getAll() {
        
        return stationService.getMapStations();
    }

    @PutMapping(path="{id}")
    public String sleep(
            @PathVariable("id") String id,
            @RequestBody String station
    ) throws IOException, InterruptedException {
        stationService.stopWashing(id);
        System.out.println("Method update");
        System.out.println(id);
        System.out.println(station);
        return stationService.getMapStations().get(id).toString();
    }
    
}
