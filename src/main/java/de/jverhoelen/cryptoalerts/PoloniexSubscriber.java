package de.jverhoelen.cryptoalerts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
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
import java.util.concurrent.TimeUnit;

@Service
public class PoloniexSubscriber {

    @Value("${es.host}")
    private String elasticsearchHost;

    private static final String TICKER_INDEX = "poloniex/ticker";
    private static final String TROLLBOX_INDEX = "poloniex/trollbox";

    private static final Logger LOGGER = LoggerFactory.getLogger(PoloniexSubscriber.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void startConsumption() throws Exception {
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
                            .subscribe(tickerSubscriber());
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

                TrollboxMessage message = TrollboxMessage.from(messageText, messageNumber);
                putIntoIndex(message, TROLLBOX_INDEX, message.getId() + "");

            } catch (IOException e) {
                LOGGER.error("Could not deserialize plot from ticker");
                e.printStackTrace();
            } catch (URISyntaxException e) {
                LOGGER.error("URI could not be built for trollbox topic", e);
                e.printStackTrace();
            }
        };
    }

    private Action1<PubSubData> tickerSubscriber() {
        return (PubSubData s) -> {
            try {
                String[] raw = objectMapper.readValue(s.arguments().toString(), new TypeReference<String[]>() {
                });

                TickerPlot plot = TickerPlot.from(raw);
                putIntoIndex(plot, TICKER_INDEX, plot.getId());

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
