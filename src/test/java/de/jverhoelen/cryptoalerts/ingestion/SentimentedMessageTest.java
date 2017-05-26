package de.jverhoelen.cryptoalerts.ingestion;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class SentimentedMessageTest {

    @Test
    public void from() throws Exception {
        SentimentedMessage msg = SentimentedMessage.from("Some example message containing nothing interesting", 300, IncomingMessageSource.POLONIEX_TROLLBOX);
        assertThat(msg.getOccurrenceTimestamp().length(), is(17));
        assertThat(msg.getId(), is("300-POLONIEX_TROLLBOX"));
    }
}