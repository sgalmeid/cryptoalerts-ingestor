package de.jverhoelen.cryptoalerts.ingestion;

import de.jverhoelen.cryptoalerts.currency.CryptoCurrency;
import de.jverhoelen.cryptoalerts.currency.combination.IndexedCurrencyCombination;
import de.jverhoelen.cryptoalerts.currency.combination.IndexedCurrencyCombinationService;
import de.jverhoelen.cryptoalerts.ingestion.subscriber.TickerSubscriber;
import de.jverhoelen.cryptoalerts.ingestion.subscriber.TrollboxSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampClientBuilder;
import ws.wamp.jawampa.connection.IWampConnectorProvider;
import ws.wamp.jawampa.transport.netty.NettyWampClientConnectorProvider;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class PoloniexSubscriber {

    private static final Logger LOGGER = LoggerFactory.getLogger(PoloniexSubscriber.class);

    @Autowired
    private IndexedCurrencyCombinationService currencyCombinations;

    @Autowired
    private ElasticsearchIndexClient elasticsearchClient;

    @PostConstruct
    public void startConsumption() throws Exception {
        List<IndexedCurrencyCombination> all = currencyCombinations.getAll();
        List<String> combinationStrings = all.stream().map(cc -> cc.toApiKey()).collect(Collectors.toList());

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
                    client.makeSubscription("ticker")
                            .subscribe(new TickerSubscriber(combinationStrings, elasticsearchClient));
                    client.makeSubscription("trollbox")
                            .subscribe(new TrollboxSubscriber(elasticsearchClient));
                }
            });
            client.open();

        } catch (Exception e) {
            LOGGER.error("Error while consuming the ticker", e);
            return;
        }
    }
}
