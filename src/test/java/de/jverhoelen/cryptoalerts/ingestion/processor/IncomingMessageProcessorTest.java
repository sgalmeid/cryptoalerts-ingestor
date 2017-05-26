package de.jverhoelen.cryptoalerts.ingestion.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.jverhoelen.cryptoalerts.ingestion.ElasticsearchIndexClient;
import de.jverhoelen.cryptoalerts.sentiment.SentimentTermKind;
import de.jverhoelen.cryptoalerts.sentiment.SentimentTermService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class IncomingMessageProcessorTest {

    List<String> positiveTerms = Arrays.asList("good", "well", "going up", "trending up");
    List<String> negativeTerms = Arrays.asList("verrry bad", "not good");
    ObjectMapper mapper = new ObjectMapper();

    IncomingMessageProcessor processor;
    ElasticsearchIndexClient esClient;
    SentimentTermService sentimentTerms;

    @Before
    public void setup() {
        esClient = Mockito.mock(ElasticsearchIndexClient.class);
        sentimentTerms = Mockito.mock(SentimentTermService.class);

        when(sentimentTerms.findByKind(SentimentTermKind.POSITIVE)).thenReturn(positiveTerms);
        when(sentimentTerms.findByKind(SentimentTermKind.NEGATIVE)).thenReturn(negativeTerms);

        processor = new IncomingMessageProcessor(esClient, sentimentTerms);
        processor.initializeTerms();
    }

//    @Test
//    public void call_plotIngested() throws Exception {
//        ArrayNode arrayNode = Mockito.mock(ArrayNode.class);
//        String messageText = "This is the message text about ETH and it says something good, verrry bad but it's trending up";
//        when(arrayNode.toString())
//                .thenReturn(
//                        mapper.writeValueAsString(new String[]{
//                                "",
//                                "123456",
//                                "",
//                                messageText
//                        }));
//
//        processor.(new PubSubData(null, arrayNode, null));
//
//        ArgumentCaptor<SentimentedMessage> capturedTroll = ArgumentCaptor.forClass(SentimentedMessage.class);
//        verify(esClient).putIntoIndex(capturedTroll.capture(), eq("poloniex/trollbox"), anyString());
//
//        SentimentedMessage msg = capturedTroll.getValue();
//        assertTrue(msg != null);
//        assertThat(msg.getId(), is(123456L));
//        assertThat(msg.getMessage(), is(messageText));
//        assertThat(msg.getSentimentKind(), is(SentimentTermKind.POSITIVE));
//        assertTrue(msg.getTopics().contains("ETH"));
//    }

    @Test
    public void containsTermAsWord() throws Exception {
        assertTrue(IncomingMessageProcessor.containsTermAsWord("I am a terrible programmer", "programmer"));
        assertFalse(IncomingMessageProcessor.containsTermAsWord("Litecoin is nice", "Lite"));
        assertFalse(IncomingMessageProcessor.containsTermAsWord("Litecoin is nice", "ni"));
        assertFalse(IncomingMessageProcessor.containsTermAsWord("Litecoin is nice", "ce"));
    }

    @Test
    public void analyseIntention() throws Exception {
        SentimentTermKind pos = processor.analyseIntention("This is a cool message and it describes a coin going up");
        assertThat(pos, is(SentimentTermKind.POSITIVE));

        SentimentTermKind neut = processor.analyseIntention("This is way too neutral to be judged");
        assertThat(neut, is(SentimentTermKind.NEUTRAL));

        SentimentTermKind neg = processor.analyseIntention("That's actually verrry bad");
        assertThat(neg, is(SentimentTermKind.NEGATIVE));
    }

    @Test
    public void extractInterestingCurrencies() throws Exception {
        Set<String> currs = processor.extractInterestingCurrencies("This is a message about bitcoin but also about ETH");
        assertTrue(currs.contains("ETH"));
        assertTrue(currs.contains("BTC"));
    }

}