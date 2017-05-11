package de.jverhoelen.cryptoalerts.ingestion;

import com.google.common.base.MoreObjects;
import de.jverhoelen.cryptoalerts.sentiment.SentimentTermKind;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

public class TrollboxMessage {

    private long id;
    private String occurrenceTimestamp;
    private String message;
    private SentimentTermKind sentimentKind;
    private Set<String> topics;

    public static TrollboxMessage from(String message, long id) {
        TrollboxMessage msg = new TrollboxMessage();

        msg.setId(id);
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
