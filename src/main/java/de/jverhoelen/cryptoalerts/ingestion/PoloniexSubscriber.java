package de.jverhoelen.cryptoalerts.ingestion;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.jverhoelen.cryptoalerts.currency.CryptoCurrency;
import de.jverhoelen.cryptoalerts.currency.combination.IndexedCurrencyCombination;
import de.jverhoelen.cryptoalerts.currency.combination.IndexedCurrencyCombinationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import rx.functions.Action1;
import ws.wamp.jawampa.PubSubData;
import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampClientBuilder;
import ws.wamp.jawampa.connection.IWampConnectorProvider;
import ws.wamp.jawampa.transport.netty.NettyWampClientConnectorProvider;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class PoloniexSubscriber {

    private static final String TICKER_INDEX = "poloniex/ticker";
    private static final String TROLLBOX_INDEX = "poloniex/trollbox";

    private static final Logger LOGGER = LoggerFactory.getLogger(PoloniexSubscriber.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private RestTemplate restTemplate = new RestTemplate();

    @Value("${es.host}")
    private String elasticsearchHost;

    @Autowired
    private IndexedCurrencyCombinationService currencyCombinations;

    private List<String> knownCryptoCurrencyNames = Arrays.stream(CryptoCurrency.values())
            .flatMap(crypto ->
                    Arrays.stream(new String[]{crypto.getFullName().toLowerCase(), crypto.name().toLowerCase()})
            )
            .collect(Collectors.toList());

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
                            .subscribe(tickerSubscriber(combinationStrings));
                    client.makeSubscription("trollbox")
                            .subscribe(trollboxSubscriber());
                }
            });
            client.open();

        } catch (Exception e) {
            LOGGER.error("Error while consuming the ticker", e);
            return;
        }
    }

    private Action1<? super PubSubData> trollboxSubscriber() {
        return (PubSubData s) -> {
            try {
                String[] raw = objectMapper.readValue(s.arguments().toString(), new TypeReference<String[]>() {
                });
                long messageNumber = Long.parseLong(raw[1]);
                String messageText = raw[3];
                String[] topics = extractInterestingCurrencies(messageText);

                if (topics.length > 0) {
                    TrollboxMessage message = TrollboxMessage.from(messageText, messageNumber);
                    message.setTopics(topics);

                    putIntoIndex(message, TROLLBOX_INDEX, message.getId() + "");
                    System.out.println("'" + messageText + "' ✅ (" + topics.length + " currency names found)");
                }

            } catch (IOException e) {
                LOGGER.error("Could not deserialize plot from ticker");
                e.printStackTrace();
            } catch (URISyntaxException e) {
                LOGGER.error("URI could not be built for trollbox topic", e);
                e.printStackTrace();
            }
        };
    }

    private String[] extractInterestingCurrencies(String message) {
        String lcMessage = message.toLowerCase();

        List<String> terms = knownCryptoCurrencyNames.stream()
                .filter(cryptoTerm -> !StringUtils.isEmpty(cryptoTerm) && lcMessage.indexOf(cryptoTerm) > -1)
                .collect(Collectors.toList());

        return terms.toArray(new String[terms.size()]);
    }

    private Action1<PubSubData> tickerSubscriber(List<String> combinations) {
        return (PubSubData s) -> {
            try {
                String[] raw = objectMapper.readValue(s.arguments().toString(), new TypeReference<String[]>() {
                });
                TickerPlot plot = TickerPlot.from(raw);

                if (combinations.contains(plot.getCurrencyCombination())) {
                    putIntoIndex(plot, TICKER_INDEX, plot.getId());
                }
            } catch (IOException e) {
                LOGGER.error("Could not deserialize plot from ticker", e);
                e.printStackTrace();
            } catch (URISyntaxException e) {
                LOGGER.error("URI could not be built for ticker topic", e);
                e.printStackTrace();
            }
        };
    }

    private <T> void putIntoIndex(T entry, String index, String id) throws URISyntaxException {
        String url = "https://" + elasticsearchHost + "/" + index + "/" + id;
        restTemplate.exchange(new RequestEntity<>(entry, HttpMethod.PUT, new URI(url)), String.class);
    }
}
