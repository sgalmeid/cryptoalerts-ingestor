package de.jverhoelen.cryptoalerts.ingestion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.jverhoelen.cryptoalerts.ingestion.ticker.plot.TickerPlot;
import de.jverhoelen.cryptoalerts.ingestion.ticker.indicator.TickerIndicatorsProcessor;
import de.jverhoelen.cryptoalerts.ingestion.ticker.TickerSubscriber;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import ws.wamp.jawampa.PubSubData;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TickerSubscriberTest {

    ObjectMapper mapper = new ObjectMapper();
    TickerSubscriber subscriber;
    TickerIndicatorsProcessor tickerIndicatorsProcessor;

    @Before
    public void setup() {
        tickerIndicatorsProcessor = Mockito.mock(TickerIndicatorsProcessor.class);
        subscriber = new TickerSubscriber(tickerIndicatorsProcessor);
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

        verify(tickerIndicatorsProcessor).processNewPlot(any(TickerPlot.class));
    }
}