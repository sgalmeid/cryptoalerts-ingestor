package de.jverhoelen.cryptoalerts.ingestion.subscriber;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.jverhoelen.cryptoalerts.currency.CryptoCurrency;
import de.jverhoelen.cryptoalerts.ingestion.ElasticsearchIndexClient;
import de.jverhoelen.cryptoalerts.ingestion.TrollboxMessage;
import de.jverhoelen.cryptoalerts.sentiment.SentimentTermKind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import rx.functions.Action1;
import ws.wamp.jawampa.PubSubData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TrollboxSubscriber implements Action1<PubSubData> {

    private static final String TROLLBOX_INDEX = "poloniex/trollbox";
    private static final Logger LOGGER = LoggerFactory.getLogger(TrollboxSubscriber.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private ElasticsearchIndexClient elasticsearchClient;
    private List<String> positiveTerms;
    private List<String> negativeTerms;

    public TrollboxSubscriber(ElasticsearchIndexClient elasticsearchClient, List<String> positiveTerms, List<String> negativeTerms) {
        this.elasticsearchClient = elasticsearchClient;
        this.positiveTerms = positiveTerms;
        this.negativeTerms = negativeTerms;
    }

    @Override
    @Async
    public void call(PubSubData s) {
        try {
            String[] raw = objectMapper.readValue(s.arguments().toString(), new TypeReference<String[]>() {
            });
            long messageNumber = Long.parseLong(raw[1]);
            String messageText = raw[3];
            String lowerCaseMessage = messageText.toLowerCase();
            Set<String> topics = extractInterestingCurrencies(lowerCaseMessage);

            if (!topics.isEmpty()) {
                TrollboxMessage message = TrollboxMessage.from(messageText, messageNumber);
                message.setTopics(topics);

                if (topics.size() == 1) {
                    message.setSentimentKind(analyseIntention(lowerCaseMessage));
                } else {
                    message.setSentimentKind(SentimentTermKind.NEUTRAL);
                }

                elasticsearchClient.putIntoIndex(message, TROLLBOX_INDEX, message.getId() + "");
            }

        } catch (IOException e) {
            LOGGER.error("Could not deserialize plot from ticker");
            e.printStackTrace();
        } catch (URISyntaxException e) {
            LOGGER.error("URI could not be built for trollbox topic", e);
            e.printStackTrace();
        }
    }

    private SentimentTermKind analyseIntention(String message) {
        long positiveMatches = positiveTerms.stream().filter(term -> containsTermAsWord(message, term)).count();
        long negativeMatches = negativeTerms.stream().filter(term -> containsTermAsWord(message, term)).count();

        if (positiveMatches > negativeMatches) {
            return SentimentTermKind.POSITIVE;
        } else if (negativeMatches > positiveMatches) {
            return SentimentTermKind.NEGATIVE;
        }

        return SentimentTermKind.NEUTRAL;
    }

    static boolean containsTermAsWord(String message, String term) {
        return message.matches(".*\\b" + term + "\\b.*");
    }

    private Set<String> extractInterestingCurrencies(String message) {
        return Arrays.stream(CryptoCurrency.values())
                .filter(isCryptoShortOrFullNameInMessage(message))
                .map(crypto -> crypto.name())
                .collect(Collectors.toSet());
    }

    private Predicate<CryptoCurrency> isCryptoShortOrFullNameInMessage(String message) {
        return crypto ->
                message.indexOf(crypto.getFullName().toLowerCase()) > -1 || message.indexOf(crypto.name().toLowerCase()) > -1;
    }
}
