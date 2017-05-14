package de.jverhoelen.cryptoalerts.ingestion.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.jverhoelen.cryptoalerts.ingestion.ElasticsearchIndexClient;
import de.jverhoelen.cryptoalerts.ingestion.TrollboxMessage;
import de.jverhoelen.cryptoalerts.sentiment.SentimentTerm;
import de.jverhoelen.cryptoalerts.sentiment.SentimentTermKind;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ws.wamp.jawampa.PubSubData;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TrollboxSubscriberTest {

    List<String> positiveTerms = Arrays.asList("good", "well", "going up", "trending up");
    List<String> negativeTerms = Arrays.asList("verrry bad", "not good");
    ObjectMapper mapper = new ObjectMapper();
    TrollboxSubscriber subscriber;
    ElasticsearchIndexClient esClient;

    @Before
    public void setup() {
        esClient = Mockito.mock(ElasticsearchIndexClient.class);
        subscriber = new TrollboxSubscriber(esClient, positiveTerms, negativeTerms);
    }

    @Test
    public void call_plotIngested() throws Exception {
        ArrayNode arrayNode = Mockito.mock(ArrayNode.class);
        String messageText = "This is the message text about ETH and it says something good, verrry bad but it's trending up";
        when(arrayNode.toString())
                .thenReturn(
                        mapper.writeValueAsString(new String[]{
                                "",
                                "123456",
                                "",
                                messageText
                        }));

        subscriber.call(new PubSubData(null, arrayNode, null));

        ArgumentCaptor<TrollboxMessage> capturedTroll = ArgumentCaptor.forClass(TrollboxMessage.class);
        verify(esClient).putIntoIndex(capturedTroll.capture(), eq("poloniex/trollbox"), anyString());

        TrollboxMessage msg = capturedTroll.getValue();
        assertTrue(msg != null);
        assertThat(msg.getId(), is(123456L));
        assertThat(msg.getMessage(), is(messageText));
        assertThat(msg.getSentimentKind(), is(SentimentTermKind.POSITIVE));
        assertTrue(msg.getTopics().contains("ETH"));
    }

    @Test
    public void containsTermAsWord() throws Exception {
        assertTrue(TrollboxSubscriber.containsTermAsWord("I am a terrible programmer", "programmer"));
        assertFalse(TrollboxSubscriber.containsTermAsWord("Litecoin is nice", "Lite"));
        assertFalse(TrollboxSubscriber.containsTermAsWord("Litecoin is nice", "ni"));
        assertFalse(TrollboxSubscriber.containsTermAsWord("Litecoin is nice", "ce"));
    }

    @Test
    public void analyseIntention() throws Exception {
        SentimentTermKind pos = subscriber.analyseIntention("This is a cool message and it describes a coin going up");
        assertThat(pos, is(SentimentTermKind.POSITIVE));

        SentimentTermKind neut = subscriber.analyseIntention("This is way too neutral to be judged");
        assertThat(neut, is(SentimentTermKind.NEUTRAL));

        SentimentTermKind neg = subscriber.analyseIntention("That's actually verrry bad");
        assertThat(neg, is(SentimentTermKind.NEGATIVE));
    }

    @Test
    public void extractInterestingCurrencies() throws Exception {
        Set<String> currs = subscriber.extractInterestingCurrencies("This is a message about bitcoin but also about ETH");
        assertTrue(currs.contains("ETH"));
        assertTrue(currs.contains("BTC"));
    }
}