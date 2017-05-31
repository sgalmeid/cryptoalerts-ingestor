package de.jverhoelen.cryptoalerts.ingestion.processor;

import de.jverhoelen.cryptoalerts.currency.CryptoCurrency;
import de.jverhoelen.cryptoalerts.ingestion.ElasticsearchIndexClient;
import de.jverhoelen.cryptoalerts.ingestion.IncomingMessageSource;
import de.jverhoelen.cryptoalerts.ingestion.SentimentedMessage;
import de.jverhoelen.cryptoalerts.sentiment.SentimentTermKind;
import de.jverhoelen.cryptoalerts.sentiment.SentimentTermService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class IncomingMessageProcessor {

    private static final String TROLLBOX_INDEX = "poloniex/trollbox";
    private static final Logger LOGGER = LoggerFactory.getLogger(IncomingMessageProcessor.class);

    private SentimentTermService sentimentTerms;
    private ElasticsearchIndexClient elasticsearchClient;

    private List<String> positiveTerms;
    private List<String> negativeTerms;

    public IncomingMessageProcessor(ElasticsearchIndexClient elasticsearchClient, SentimentTermService sentimentTerms) {
        this.elasticsearchClient = elasticsearchClient;
        this.sentimentTerms = sentimentTerms;
    }

    @PostConstruct
    public void initializeTerms() {
        positiveTerms = sentimentTerms.findByKind(SentimentTermKind.POSITIVE);
        negativeTerms = sentimentTerms.findByKind(SentimentTermKind.NEGATIVE);
    }

    public void processMessage(long messageNumber, String messageText, IncomingMessageSource source) {
        try {
            Set<String> topics = extractInterestingCurrencies(messageText);

            if (!topics.isEmpty()) {
                SentimentedMessage message = SentimentedMessage.from(messageText, messageNumber, source);
                message.setTopics(topics);

                if (topics.size() == 1) {
                    message.setSentimentKind(analyseIntention(messageText));
                } else {
                    message.setSentimentKind(SentimentTermKind.NEUTRAL);
                }

                elasticsearchClient.putIntoIndex(message, TROLLBOX_INDEX, message.getId() + "");
            }

        } catch (URISyntaxException e) {
            LOGGER.error("URI could not be built for trollbox topic", e);
            e.printStackTrace();
        }
    }

    SentimentTermKind analyseIntention(String message) {
        String lcMessage = message.toLowerCase();
        long positiveMatches = positiveTerms.stream().filter(term -> containsTermAsWord(lcMessage, term)).count();
        long negativeMatches = negativeTerms.stream().filter(term -> containsTermAsWord(lcMessage, term)).count();

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

    Set<String> extractInterestingCurrencies(String message) {
        String lcMessage = message.toLowerCase();

        return Arrays.stream(CryptoCurrency.values())
                .filter(isCryptoShortOrFullNameInMessage(lcMessage))
                .map(crypto -> crypto.name())
                .collect(Collectors.toSet());
    }

    private Predicate<CryptoCurrency> isCryptoShortOrFullNameInMessage(String message) {
        return crypto ->
                containsTermAsWord(message, crypto.getFullName().toLowerCase()) ||
                        containsTermAsWord(message, crypto.name().toLowerCase());
    }
}
