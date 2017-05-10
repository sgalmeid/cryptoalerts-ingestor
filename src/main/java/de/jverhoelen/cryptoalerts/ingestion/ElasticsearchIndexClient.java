package de.jverhoelen.cryptoalerts.ingestion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class ElasticsearchIndexClient {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${es.host}")
    private String elasticsearchHost;

    public <T> void putIntoIndex(T entry, String index, String id) throws URISyntaxException {
        String url = "https://" + elasticsearchHost + "/" + index + "/" + id;
        restTemplate.exchange(new RequestEntity<>(entry, HttpMethod.PUT, new URI(url)), String.class);
    }
}
