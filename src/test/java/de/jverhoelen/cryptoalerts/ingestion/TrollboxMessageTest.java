package de.jverhoelen.cryptoalerts.ingestion;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class TrollboxMessageTest {

    @Test
    public void from() throws Exception {
        TrollboxMessage msg = TrollboxMessage.from("Some example message containing nothing interesting", 300);
        assertThat(msg.getOccurrenceTimestamp().length(), is(17));
        assertThat(msg.getId(), is(300L));
    }
}