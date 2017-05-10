package de.jverhoelen.cryptoalerts.ingestion;

import de.jverhoelen.cryptoalerts.currency.combination.IndexedCurrencyCombination;
import de.jverhoelen.cryptoalerts.currency.combination.IndexedCurrencyCombinationService;
import de.jverhoelen.cryptoalerts.ingestion.subscriber.TickerSubscriber;
import de.jverhoelen.cryptoalerts.ingestion.subscriber.TrollboxSubscriber;
import de.jverhoelen.cryptoalerts.sentiment.SentimentTermKind;
import de.jverhoelen.cryptoalerts.sentiment.SentimentTermService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampClientBuilder;
import ws.wamp.jawampa.connection.IWampConnectorProvider;
import ws.wamp.jawampa.transport.netty.NettyWampClientConnectorProvider;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PoloniexSubscriptionStarter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PoloniexSubscriptionStarter.class);

    private IndexedCurrencyCombinationService currencyCombinations;
    private ElasticsearchIndexClient elasticsearchClient;
    private SentimentTermService sentimentTerms;

    @Autowired
    public PoloniexSubscriptionStarter(IndexedCurrencyCombinationService currencyCombinations,
                                       ElasticsearchIndexClient elasticsearchClient,
                                       SentimentTermService sentimentTerms) {
        this.currencyCombinations = currencyCombinations;
        this.elasticsearchClient = elasticsearchClient;
        this.sentimentTerms = sentimentTerms;
    }

    @PostConstruct
    public void startConsumption() throws Exception {
        List<IndexedCurrencyCombination> all = currencyCombinations.getAll();
        List<String> combinationStrings = all.stream().map(cc -> cc.toApiKey()).collect(Collectors.toList());

        List<String> positiveTerms = sentimentTerms.findByKind(SentimentTermKind.NEGATIVE);
        List<String> negativeTerms = sentimentTerms.findByKind(SentimentTermKind.POSITIVE);

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
                            .subscribe(new TrollboxSubscriber(elasticsearchClient, positiveTerms, negativeTerms));
                }
            });
            client.open();

        } catch (Exception e) {
            LOGGER.error("Error while consuming the ticker", e);
            return;
        }
    }
}
