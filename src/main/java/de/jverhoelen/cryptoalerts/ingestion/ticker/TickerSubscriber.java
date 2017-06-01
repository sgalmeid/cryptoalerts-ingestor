package de.jverhoelen.cryptoalerts.ingestion.ticker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.jverhoelen.cryptoalerts.ingestion.ticker.indicator.TickerIndicatorsProcessor;
import de.jverhoelen.cryptoalerts.ingestion.ticker.plot.TickerPlot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import rx.functions.Action1;
import ws.wamp.jawampa.PubSubData;

import java.io.IOException;

public class TickerSubscriber implements Action1<PubSubData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TickerSubscriber.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private TickerIndicatorsProcessor processor;

    public TickerSubscriber(TickerIndicatorsProcessor processor) {
        this.processor = processor;
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

            processor.processNewPlot(plot);
        } catch (IOException e) {
            LOGGER.error("Could not deserialize plot from ticker", e);
            e.printStackTrace();
        }
    }
}
