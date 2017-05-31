package de.jverhoelen.cryptoalerts.ingestion.processor;

import com.google.common.base.MoreObjects;
import de.jverhoelen.cryptoalerts.ingestion.processor.indicator.SimplePlot;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class IndexedTickerPlot {

    private String id;
    private String currencyCombination;
    private String occurrenceTimestamp;

    // course
    private BigDecimal last;
    private BigDecimal baseVolume;

    // emas
    private BigDecimal ema12;
    private BigDecimal ema26;

    // indicator results
    private BigDecimal macd;
    private BigDecimal signal;
    private BigDecimal rsi;

    public IndexedTickerPlot(SimplePlot plot) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(plot.getTime());

        // basic info
        occurrenceTimestamp = nowAsISO;
        id = plot.getCurrencyCombination() + "-" + nowAsISO;
        currencyCombination = plot.getCurrencyCombination();

        // course
        last = plot.getClose();
        baseVolume = plot.getBaseVolume();

        // emas
        ema12 = plot.getEma12();
        ema26 = plot.getEma26();

        // indicator results
        macd = plot.getMacd();
        signal = plot.getSignal();
        rsi = plot.getRsi();
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

    public BigDecimal getLast() {
        return last;
    }

    public void setLast(BigDecimal last) {
        this.last = last;
    }

    public BigDecimal getBaseVolume() {
        return baseVolume;
    }

    public void setBaseVolume(BigDecimal baseVolume) {
        this.baseVolume = baseVolume;
    }

    public BigDecimal getEma12() {
        return ema12;
    }

    public void setEma12(BigDecimal ema12) {
        this.ema12 = ema12;
    }

    public BigDecimal getEma26() {
        return ema26;
    }

    public void setEma26(BigDecimal ema26) {
        this.ema26 = ema26;
    }

    public BigDecimal getMacd() {
        return macd;
    }

    public void setMacd(BigDecimal macd) {
        this.macd = macd;
    }

    public BigDecimal getSignal() {
        return signal;
    }

    public void setSignal(BigDecimal signal) {
        this.signal = signal;
    }

    public BigDecimal getRsi() {
        return rsi;
    }

    public void setRsi(BigDecimal rsi) {
        this.rsi = rsi;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("currencyCombination", currencyCombination)
                .add("occurrenceTimestamp", occurrenceTimestamp)
                .add("last", last)
                .add("baseVolume", baseVolume)
                .add("ema12", ema12)
                .add("ema26", ema26)
                .add("macd", macd)
                .add("signal", signal)
                .add("rsi", rsi)
                .toString();
    }
}
