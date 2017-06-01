package de.jverhoelen.cryptoalerts.ingestion.starter;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import de.jverhoelen.cryptoalerts.ingestion.IncomingMessageProcessor;
import de.jverhoelen.cryptoalerts.ingestion.ticker.indicator.TickerIndicatorsProcessor;
import de.jverhoelen.cryptoalerts.ingestion.ticker.TickerSubscriber;
import de.jverhoelen.cryptoalerts.ingestion.TrollboxSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampClientBuilder;
import ws.wamp.jawampa.connection.IWampConnectorProvider;
import ws.wamp.jawampa.transport.netty.NettyWampClientConnectorProvider;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class PoloniexSubscriptionStarter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PoloniexSubscriptionStarter.class);

    private IncomingMessageProcessor incomingMessageProcessor;
    private TickerIndicatorsProcessor tickerIndicatorsProcessor;

    @Value("${ingest.trollbox}")
    private boolean ingestTrollbox;

    @Value("${ingest.ticker}")
    private boolean ingestTicker;

    @Autowired
    public PoloniexSubscriptionStarter(TickerIndicatorsProcessor tickerIndicatorsProcessor,
                                       IncomingMessageProcessor incomingMessageProcessor) {
        this.tickerIndicatorsProcessor = tickerIndicatorsProcessor;
        this.incomingMessageProcessor = incomingMessageProcessor;
    }

    @PostConstruct
    public void startConsumption() throws Exception {
        if (ingestTicker) {
            setupTickerIngestion();
        }

        if (ingestTrollbox) {
            setupTrollboxIngestion();
        }
    }

    private void setupTickerIngestion() {
        WampClient client;
        try {
            WampClientBuilder builder = new WampClientBuilder();
            IWampConnectorProvider connectorProvider = new NettyWampClientConnectorProvider();
            builder.withConnectorProvider(connectorProvider)
                    .withUri("wss://api.poloniex.com")
                    .withRealm("realm1")
                    .withInfiniteReconnects()
                    .withReconnectInterval(5, TimeUnit.SECONDS);
            client = builder.build();

            client.statusChanged().subscribe(action -> {
                if (action instanceof WampClient.ConnectedState) {
                    LOGGER.info("Started ingesting ticker...");
                    client.makeSubscription("ticker")
                            .subscribe(new TickerSubscriber(tickerIndicatorsProcessor));

                    // Old way of consuming the trollbox. Poloniex changed the host. New temporary procedure, see setupTrollboxIngestion()
//                        LOGGER.info("Started ingesting trollbox...");
//                        client.makeSubscription("trollbox")
//                                .subscribe(new TrollboxSubscriber(elasticsearchClient, positiveTerms, negativeTerms));
                }
            });
            client.open();

        } catch (Exception e) {
            LOGGER.error("Error while consuming the ticker", e);
        }
    }

    private void setupTrollboxIngestion() throws IOException, WebSocketException {
        WebSocketFactory factory = new WebSocketFactory();
        TrollboxSubscriber subscriber = new TrollboxSubscriber(incomingMessageProcessor);

        WebSocket ws = factory.createSocket("wss://api2.poloniex.com", 5000);
        ws.addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket websocket, String message) throws Exception {
                if (message.contains("[1001,")) {
                    subscriber.callWithPlainMessage(message);
                }
            }

            @Override
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                super.onConnected(websocket, headers);
                websocket.sendText("{\"command\":\"subscribe\",\"channel\":1001}");
            }
        });
        ws.connect();
    }
}
