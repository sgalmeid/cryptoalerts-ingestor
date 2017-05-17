package de.jverhoelen.cryptoalerts.ingestion.subscriber;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.jverhoelen.cryptoalerts.ingestion.ElasticsearchIndexClient;
import de.jverhoelen.cryptoalerts.ingestion.TickerPlot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import rx.functions.Action1;
import ws.wamp.jawampa.PubSubData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class TickerSubscriber implements Action1<PubSubData> {

    private static final String TICKER_INDEX = "poloniex/ticker";
    private static final Logger LOGGER = LoggerFactory.getLogger(TickerSubscriber.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private List<String> combinations;
    private ElasticsearchIndexClient elasticsearchClient;

    public TickerSubscriber(List<String> combinations, ElasticsearchIndexClient elasticsearchClient) {
        this.combinations = combinations;
        this.elasticsearchClient = elasticsearchClient;
    }

    @Override
    @Async
    public void call(PubSubData s) {
        callWithPlainMessage(s.arguments().toString());
    }

    public void callWithPlainMessage(String message) {
        try {
            String[] raw = objectMapper.readValue(message, new TypeReference<String[]>() {
            });
            TickerPlot plot = TickerPlot.from(raw);

            if (combinations.contains(plot.getCurrencyCombination())) {
                elasticsearchClient.putIntoIndex(plot, TICKER_INDEX, plot.getId());
            }
        } catch (IOException e) {
            LOGGER.error("Could not deserialize plot from ticker", e);
            e.printStackTrace();
        } catch (URISyntaxException e) {
            LOGGER.error("URI could not be built for ticker topic", e);
            e.printStackTrace();
        }
    }
}
