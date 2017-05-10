package de.jverhoelen.cryptoalerts.ingestion;

import com.google.common.base.MoreObjects;
import de.jverhoelen.cryptoalerts.sentiment.SentimentTermKind;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class TrollboxMessage {

    private long id;
    private String timestamp;
    private String message;
    private SentimentTermKind sentimentKind;
    private Set<String> topics;

    public static TrollboxMessage from(String message, long id) {
        TrollboxMessage msg = new TrollboxMessage();

        msg.setId(id);
        msg.setMessage(message);
        msg.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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
                .add("timestamp", timestamp)
                .add("message", message)
                .add("sentimentKind", sentimentKind)
                .add("topics", topics)
                .toString();
    }
}
