package de.jverhoelen.cryptoalerts.ingestion.subscriber;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TrollboxSubscriberTest {

    @Test
    public void call() throws Exception {
    }

    @Test
    public void containsTermAsWord() throws Exception {
        assertTrue(TrollboxSubscriber.containsTermAsWord("I am a terrible programmer", "programmer"));
        assertFalse(TrollboxSubscriber.containsTermAsWord("Litecoin is nice", "Lite"));
        assertFalse(TrollboxSubscriber.containsTermAsWord("Litecoin is nice", "ni"));
        assertFalse(TrollboxSubscriber.containsTermAsWord("Litecoin is nice", "ce"));
    }
}