package de.jverhoelen.cryptoalerts.ingestion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class ElasticsearchIndexClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchIndexClient.class);
    private RestTemplate restTemplate;
    private String elasticsearchHost;

    @Autowired
    public ElasticsearchIndexClient(
            RestTemplate restTemplate,
            @Value("${es.host}") String elasticsearchHost) {
        this.restTemplate = restTemplate;
        this.elasticsearchHost = elasticsearchHost;
    }

    @Async
    public <T> void putIntoIndex(T entry, String index, String id) throws URISyntaxException {
        String url = "https://" + elasticsearchHost + "/" + index + "/" + id;
        ResponseEntity<String> response = restTemplate.exchange(new RequestEntity<>(entry, HttpMethod.PUT, new URI(url)), String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            LOGGER.error(
                    "Request to put entry of type {} with ID {} into index {} failed with status code {}",
                    entry.getClass().toString(),
                    id, index, response.getStatusCode().value()
            );
        }
    }
}
