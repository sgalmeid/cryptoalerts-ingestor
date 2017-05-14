package de.jverhoelen.cryptoalerts.ingestion.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.jverhoelen.cryptoalerts.ingestion.ElasticsearchIndexClient;
import de.jverhoelen.cryptoalerts.ingestion.TickerPlot;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import ws.wamp.jawampa.PubSubData;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TickerSubscriberTest {

    List<String> combinations = Arrays.asList("BTC_DASH", "BTC_XRP");
    ObjectMapper mapper = new ObjectMapper();
    TickerSubscriber subscriber;
    ElasticsearchIndexClient esClient;

    @Before
    public void setup() {
        esClient = Mockito.mock(ElasticsearchIndexClient.class);
        subscriber = new TickerSubscriber(combinations, esClient);
    }

    @Test
    public void call_plotIngested() throws Exception {
        ArrayNode arrayNode = Mockito.mock(ArrayNode.class);
        when(arrayNode.toString())
                .thenReturn(
                        mapper.writeValueAsString(new String[]{
                                "BTC_DASH",
                                "3",
                                "0",
                                "0",
                                "0",
                                "3000",
                                "30",
                                "0",
                                "0",
                                "5",
                                "3",
                        }));

        subscriber.call(new PubSubData(null, arrayNode, null));

        verify(esClient).putIntoIndex(any(TickerPlot.class), eq("poloniex/ticker"), anyString());
    }

    @Test
    public void call_plotNotIngestedContainsNoCurrencyCombin() throws Exception {
        ArrayNode arrayNode = Mockito.mock(ArrayNode.class);
        when(arrayNode.toString())
                .thenReturn(
                        mapper.writeValueAsString(new String[]{
                                "Something_weird",
                                "3",
                                "0",
                                "0",
                                "0",
                                "3000",
                                "30",
                                "0",
                                "0",
                                "5",
                                "3",
                        }));

        subscriber.call(new PubSubData(null, arrayNode, null));
        verify(esClient, times(0)).putIntoIndex(any(TickerPlot.class), eq("poloniex/ticker"), anyString());
    }
}