package ru.rtrn.vsat.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;
import ru.rtrn.vsat.httpReq.SimpleRequest;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
public class Controller {
    @Autowired
    private SimpleRequest simpleRequest;

    @GetMapping("/vsat")
    public Double getAllVsat() throws URISyntaxException, IOException, InterruptedException, ParserConfigurationException, SAXException {
        return simpleRequest.getVsatEbN0("10.2.218.1");
    }

}
