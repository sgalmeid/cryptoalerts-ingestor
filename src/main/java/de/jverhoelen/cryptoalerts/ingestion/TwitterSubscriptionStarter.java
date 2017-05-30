package de.jverhoelen.cryptoalerts.ingestion;


import de.jverhoelen.cryptoalerts.currency.CryptoCurrency;
import de.jverhoelen.cryptoalerts.currency.combination.IndexedCurrencyCombinationService;
import de.jverhoelen.cryptoalerts.ingestion.processor.IncomingMessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TwitterSubscriptionStarter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterSubscriptionStarter.class);

    @Value("${twitter.oauth.consumer.key}")
    private String oAuthConsumerKey;

    @Value("${twitter.oauth.consumer.secret}")
    private String oAuthConsumerSecret;

    @Value("${twitter.oauth.access.token}")
    private String oAuthAccessToken;

    @Value("${twitter.oauth.access.token.secret}")
    private String oAuthAccessTokenSecret;

    @Value("${ingest.twitter}")
    private boolean enableTwitterIngestion;

    @Autowired
    private IncomingMessageProcessor processor;

    @Autowired
    private IndexedCurrencyCombinationService currencyCombinations;

    @PostConstruct
    public void startTwitterIngestion() {
        if (enableTwitterIngestion) {
            List<String> cryptoNames = getInterestingTerms();

            Configuration config = buildConfig();
            TwitterStream twitterStream = new TwitterStreamFactory(config).getInstance();
            StatusListener statusListener = new OwnStatusListener();

            twitterStream.addListener(statusListener);
            twitterStream.filter(
                    new FilterQuery()
                            .track(cryptoNames.toArray(new String[cryptoNames.size()]))
                            .language()
            );
        }
    }

    private List<String> getInterestingTerms() {
        List<String> cryptoNames = Arrays.stream(CryptoCurrency.values())
                .flatMap(cc -> {
                    String hashtag = "#" + cc.getFullName().toLowerCase();
                    String currencyTag = "$" + cc.name().toUpperCase();
                    return Arrays.asList(hashtag, currencyTag).stream();
                })
                .collect(Collectors.toList());
        cryptoNames.add("#cryptocurrencies");
        cryptoNames.add("#cryptocurrency");
        cryptoNames.add("#kryptowährung");

        return cryptoNames;
    }

    private Configuration buildConfig() {
        return new ConfigurationBuilder()
                .setDebugEnabled(true)
                .setOAuthConsumerKey(oAuthConsumerKey)
                .setOAuthConsumerSecret(oAuthConsumerSecret)
                .setOAuthAccessToken(oAuthAccessToken)
                .setOAuthAccessTokenSecret(oAuthAccessTokenSecret).build();
    }

    private class OwnStatusListener implements StatusListener {
        @Override
        public void onStatus(Status status) {
            processor.processMessage(status.getId(), status.getText(), IncomingMessageSource.TWITTER);
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice sdn) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void onTrackLimitationNotice(int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void onScrubGeo(long l, long l1) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void onStallWarning(StallWarning sw) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void onException(Exception ex) {
            System.out.println("onException");
        }
    }
}
