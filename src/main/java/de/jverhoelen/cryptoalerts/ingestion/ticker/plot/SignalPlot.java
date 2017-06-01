package de.jverhoelen.cryptoalerts.ingestion.ticker.plot;

import de.jverhoelen.cryptoalerts.datautils.Utils;
import de.jverhoelen.cryptoalerts.ingestion.ticker.indicator.Candle;

import java.math.BigDecimal;

public class SignalPlot {

    private String id;
    private String currencyCombination;
    private String occurrenceTimestamp;

    private BigDecimal value;

    public SignalPlot(Candle candle) {
        String nowAsISO = Utils.getIsoTimestamp(candle.getEnd());

        // basic info
        occurrenceTimestamp = nowAsISO;
        id = "signal-" + candle.getCurrencyCombination() + "-" + nowAsISO;
        currencyCombination = candle.getCurrencyCombination();

        // signal
        value = candle.getMacd();
    }

    public String getId() {
        return id;
    }

    public String getCurrencyCombination() {
        return currencyCombination;
    }

    public String getOccurrenceTimestamp() {
        return occurrenceTimestamp;
    }

    public BigDecimal getValue() {
        return value;
    }

}
