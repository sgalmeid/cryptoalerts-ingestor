package de.jverhoelen.cryptoalerts.ingestion.subscriber;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.jverhoelen.cryptoalerts.currency.CryptoCurrency;
import de.jverhoelen.cryptoalerts.ingestion.ElasticsearchIndexClient;
import de.jverhoelen.cryptoalerts.ingestion.TrollboxMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.functions.Action1;
import ws.wamp.jawampa.PubSubData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TrollboxSubscriber implements Action1<PubSubData> {

    private static final String TROLLBOX_INDEX = "poloniex/trollbox";
    private static final Logger LOGGER = LoggerFactory.getLogger(TrollboxSubscriber.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private ElasticsearchIndexClient elasticsearchClient;

    public TrollboxSubscriber(ElasticsearchIndexClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    @Override
    public void call(PubSubData s) {
        try {
            String[] raw = objectMapper.readValue(s.arguments().toString(), new TypeReference<String[]>() {
            });
            long messageNumber = Long.parseLong(raw[1]);
            String messageText = raw[3];
            Set<String> topics = extractInterestingCurrencies(messageText);

            if (!topics.isEmpty()) {
                TrollboxMessage message = TrollboxMessage.from(messageText, messageNumber);
                message.setTopics(topics);

                elasticsearchClient.putIntoIndex(message, TROLLBOX_INDEX, message.getId() + "");
                System.out.println("'" + messageText + "' ✅ (" + topics.size() + " currency names found)");
            }

        } catch (IOException e) {
            LOGGER.error("Could not deserialize plot from ticker");
            e.printStackTrace();
        } catch (URISyntaxException e) {
            LOGGER.error("URI could not be built for trollbox topic", e);
            e.printStackTrace();
        }
    }

    private Set<String> extractInterestingCurrencies(String message) {
        String lcMessage = message.toLowerCase();

        return Arrays.stream(CryptoCurrency.values())
                .filter(isCryptoShortOrFullNameInMessage(lcMessage))
                .map(crypto -> crypto.name())
                .collect(Collectors.toSet());
    }

    private Predicate<CryptoCurrency> isCryptoShortOrFullNameInMessage(String message) {
        return crypto ->
                message.indexOf(crypto.getFullName().toLowerCase()) > -1 || message.indexOf(crypto.name().toLowerCase()) > -1;
    }
}
