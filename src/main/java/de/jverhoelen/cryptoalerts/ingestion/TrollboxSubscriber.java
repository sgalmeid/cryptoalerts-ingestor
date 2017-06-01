package de.jverhoelen.cryptoalerts.ingestion;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.jverhoelen.cryptoalerts.sentiment.IncomingMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import rx.functions.Action1;
import ws.wamp.jawampa.PubSubData;

import java.io.IOException;

public class TrollboxSubscriber implements Action1<PubSubData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrollboxSubscriber.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<String[]> MSG_TYPE_REFERENCE = new TypeReference<String[]>() {
    };

    private IncomingMessageProcessor processor;

    public TrollboxSubscriber(IncomingMessageProcessor processor) {
        this.processor = processor;
    }

    @Override
    @Async
    public void call(PubSubData s) {
        callWithPlainMessage(s.arguments().toString());
    }

    public void callWithPlainMessage(String plainMessage) {
        try {
            String[] raw = OBJECT_MAPPER.readValue(plainMessage, MSG_TYPE_REFERENCE);
            long messageNumber = Long.parseLong(raw[1]);
            String messageText = raw[3];

            processor.processMessage(messageNumber, messageText, IncomingMessageSource.POLONIEX_TROLLBOX);
        } catch (IOException e) {
            LOGGER.error("Could not parse incoming trollbox message to String[]");
        }
    }
}
