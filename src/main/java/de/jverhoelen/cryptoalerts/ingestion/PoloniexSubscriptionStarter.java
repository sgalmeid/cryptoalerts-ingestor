package de.jverhoelen.cryptoalerts.ingestion;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import de.jverhoelen.cryptoalerts.currency.combination.IndexedCurrencyCombination;
import de.jverhoelen.cryptoalerts.currency.combination.IndexedCurrencyCombinationService;
import de.jverhoelen.cryptoalerts.ingestion.processor.IncomingMessageProcessor;
import de.jverhoelen.cryptoalerts.ingestion.subscriber.TickerSubscriber;
import de.jverhoelen.cryptoalerts.ingestion.subscriber.TrollboxSubscriber;
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
import java.util.stream.Collectors;

@Service
public class PoloniexSubscriptionStarter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PoloniexSubscriptionStarter.class);

    private IndexedCurrencyCombinationService currencyCombinations;
    private ElasticsearchIndexClient elasticsearchClient;
    private IncomingMessageProcessor incomingMessageProcessor;

    @Value("${ingest.trollbox}")
    private boolean ingestTrollbox;

    @Value("${ingest.ticker}")
    private boolean ingestTicker;

    @Autowired
    public PoloniexSubscriptionStarter(IndexedCurrencyCombinationService currencyCombinations,
                                       ElasticsearchIndexClient elasticsearchClient,
                                       IncomingMessageProcessor incomingMessageProcessor) {
        this.currencyCombinations = currencyCombinations;
        this.elasticsearchClient = elasticsearchClient;
        this.incomingMessageProcessor = incomingMessageProcessor;
    }

    @PostConstruct
    public void startConsumption() throws Exception {
        List<IndexedCurrencyCombination> all = currencyCombinations.getAll();
        List<String> combinationStrings = all.stream().map(cc -> cc.toApiKey()).collect(Collectors.toList());

        if (ingestTicker) {
            setupTickerIngestion(combinationStrings);
        }

        if (ingestTrollbox) {
            setupTrollboxIngestion();
        }
    }

    private void setupTickerIngestion(List<String> combinationStrings) {
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
                            .subscribe(new TickerSubscriber(combinationStrings, elasticsearchClient));

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
