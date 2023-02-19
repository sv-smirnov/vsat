package ru.rtrn.vsat.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
    
}
