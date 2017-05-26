package de.jverhoelen.cryptoalerts.ingestion;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class ElasticsearchIndexClientTest {

    private ElasticsearchIndexClient client;
    private MockRestServiceServer server;

    @Before
    public void before() {
        RestTemplate restTemplate = new RestTemplate();

        server = MockRestServiceServer.bindTo(restTemplate).build();
        client = new ElasticsearchIndexClient(restTemplate, "elasticsearch");
    }

    @Test
    public void putIntoIndex() throws Exception {
        server.expect(ExpectedCount.times(1), requestTo("https://elasticsearch/trollbox/123"))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        client.putIntoIndex(SentimentedMessage.from("Some message", 123L, IncomingMessageSource.POLONIEX_TROLLBOX), "trollbox", "123");

        server.verify();
    }
}