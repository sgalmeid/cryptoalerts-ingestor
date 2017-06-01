package de.jverhoelen.cryptoalerts.ingestion.ticker.plot;

import com.google.common.base.MoreObjects;
import de.jverhoelen.cryptoalerts.datautils.Utils;

import java.math.BigDecimal;

public class RsiPlot {

    private String id;
    private String currencyCombination;
    private String occurrenceTimestamp;
    private BigDecimal value;

    public RsiPlot(SimplePlot plot) {
        String nowAsISO = Utils.getIsoTimestamp(plot.getTime());

        // basic info
        occurrenceTimestamp = nowAsISO;
        id = "rsi-" + plot.getCurrencyCombination() + "-" + nowAsISO;
        currencyCombination = plot.getCurrencyCombination();

        // rsi
        value = plot.getRsi();
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("currencyCombination", currencyCombination)
                .add("occurrenceTimestamp", occurrenceTimestamp)
                .add("value", value)
                .toString();
    }
}
