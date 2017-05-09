package de.jverhoelen.cryptoalerts;

import com.google.common.base.MoreObjects;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TrollboxMessage {

    private long id;
    private String timestamp;
    private String message;

    public static TrollboxMessage from(String message, long id) {
        TrollboxMessage msg = new TrollboxMessage();

        msg.setId(id);
        msg.setMessage(message);
        msg.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return msg;
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
                .toString();
    }
}
