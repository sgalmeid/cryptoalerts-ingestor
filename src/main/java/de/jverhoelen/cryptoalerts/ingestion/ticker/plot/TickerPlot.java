package de.jverhoelen.cryptoalerts.ingestion.ticker.plot;

import com.google.common.base.MoreObjects;
import de.jverhoelen.cryptoalerts.datautils.Utils;

import java.math.BigDecimal;
import java.util.Date;

public class TickerPlot {

    private String id;
    private String currencyCombination;
    private Date eventTimestamp;
    private String occurrenceTimestamp;

    private BigDecimal value;
    private BigDecimal baseVolume;

    public static TickerPlot from(String[] arr) {
        TickerPlot p = new TickerPlot();

        p.setEventTimestamp(new Date());
        p.setOccurrenceTimestamp(Utils.getIsoTimestamp(p.getEventTimestamp()));
        p.setCurrencyCombination(arr[0]);
        p.setValue(new BigDecimal(arr[1]));
        p.setBaseVolume(new BigDecimal(arr[5]));
        p.setId("ticker-" + p.getCurrencyCombination() + "-" + System.currentTimeMillis());

        return p;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrencyCombination() {
        return currencyCombination;
    }

    public void setCurrencyCombination(String currencyCombination) {
        this.currencyCombination = currencyCombination;
    }

    public Date getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(Date eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public String getOccurrenceTimestamp() {
        return occurrenceTimestamp;
    }

    public void setOccurrenceTimestamp(String occurrenceTimestamp) {
        this.occurrenceTimestamp = occurrenceTimestamp;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getBaseVolume() {
        return baseVolume;
    }

    public void setBaseVolume(BigDecimal baseVolume) {
        this.baseVolume = baseVolume;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("currencyCombination", currencyCombination)
                .add("value", value)
                .add("baseVolume", baseVolume)
                .toString();
    }
}
