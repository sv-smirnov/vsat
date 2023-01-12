package ru.rtrn.vsat.httpReq;

import lombok.RequiredArgsConstructor;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

@RequiredArgsConstructor
@Service
public class SimpleRequest {

    public Double getVsatEbN0(String ip) throws URISyntaxException, IOException, InterruptedException, ParserConfigurationException, SAXException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http");
        builder.setHost(ip);
        builder.setPath("/cgi-bin/vsat.cgi");
        builder.addParameter("action", "telemetry");
        URL url = builder.build().toURL();

        Document doc = Jsoup.connect(url.toString()).get();
        Element ebn0 = doc.getElementById("RxEbN0");
        String value = ebn0.attr("val");
        return Double.parseDouble(value);
    }


}
