package de.jverhoelen.cryptoalerts.sentiment;

import com.google.common.base.MoreObjects;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

public class SentimentedMessage {

    private String id;
    private String occurrenceTimestamp;
    private String message;
    private SentimentTermKind sentimentKind;
    private Set<String> topics;
    private IncomingMessageSource source;

    public static SentimentedMessage from(String message, long id, IncomingMessageSource source) {
        SentimentedMessage msg = new SentimentedMessage();

        msg.setSource(source);
        msg.setId(id + "-" + source.name());
        msg.setMessage(message);

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());

        msg.setOccurrenceTimestamp(nowAsISO);

        return msg;
    }

    public SentimentTermKind getSentimentKind() {
        return sentimentKind;
    }

    public void setSentimentKind(SentimentTermKind sentimentKind) {
        this.sentimentKind = sentimentKind;
    }

    public Set<String> getTopics() {
        return topics;
    }

    public void setTopics(Set<String> topics) {
        this.topics = topics;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOccurrenceTimestamp() {
        return occurrenceTimestamp;
    }

    public void setOccurrenceTimestamp(String occurrenceTimestamp) {
        this.occurrenceTimestamp = occurrenceTimestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSource(IncomingMessageSource source) {
        this.source = source;
    }

    public IncomingMessageSource getSource() {
        return source;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("occurrenceTimestamp", occurrenceTimestamp)
                .add("message", message)
                .add("sentimentKind", sentimentKind)
                .add("topics", topics)
                .toString();
    }
}
