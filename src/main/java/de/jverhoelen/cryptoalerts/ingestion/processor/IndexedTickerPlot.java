package de.jverhoelen.cryptoalerts.ingestion.processor;

import de.jverhoelen.cryptoalerts.ingestion.processor.indicator.SimplePlot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class IndexedTickerPlot {

    private String id;
    private String currencyCombination;
    private String occurrenceTimestamp;
    private double last;
    private double baseVolume;

    // emas
    private double twelveDaysEma;
    private double twentySixDaysEma;

    // indicator results
    private double macd;
    private double signal;
    private double rsi;

    public IndexedTickerPlot(SimplePlot plot) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(plot.getTime());
        occurrenceTimestamp = nowAsISO;

        id = plot.getCurrencyCombination() + "-" + nowAsISO;
        currencyCombination = plot.getCurrencyCombination();

        last = plot.getClose().doubleValue();
        baseVolume = plot.getBaseVolume().doubleValue();
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

    public double getLast() {
        return last;
    }

    public void setLast(double last) {
        this.last = last;
    }

    public double getBaseVolume() {
        return baseVolume;
    }

    public void setBaseVolume(double baseVolume) {
        this.baseVolume = baseVolume;
    }

    public double getTwelveDaysEma() {
        return twelveDaysEma;
    }

    public void setTwelveDaysEma(double twelveDaysEma) {
        this.twelveDaysEma = twelveDaysEma;
    }

    public double getTwentySixDaysEma() {
        return twentySixDaysEma;
    }

    public void setTwentySixDaysEma(double twentySixDaysEma) {
        this.twentySixDaysEma = twentySixDaysEma;
    }

    public double getMacd() {
        return macd;
    }

    public void setMacd(double macd) {
        this.macd = macd;
    }

    public double getSignal() {
        return signal;
    }

    public void setSignal(double signal) {
        this.signal = signal;
    }

    public double getRsi() {
        return rsi;
    }

    public void setRsi(double rsi) {
        this.rsi = rsi;
    }
}
