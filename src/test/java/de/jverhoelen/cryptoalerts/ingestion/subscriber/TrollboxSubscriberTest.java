package de.jverhoelen.cryptoalerts.ingestion.subscriber;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TrollboxSubscriberTest {
    @Test
    public void containsTermAsWord() throws Exception {
        boolean contained = TrollboxSubscriber.containsTermAsWord("I am a terrible programmer", "programmer");
        assertTrue(contained);
    }
}