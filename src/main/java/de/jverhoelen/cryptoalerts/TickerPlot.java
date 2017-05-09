package de.jverhoelen.cryptoalerts;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TickerPlot {

    private String id;

    private String currencyCombination;
    private String timestamp;
    private double last;
    private double lowestAsk;
    private double highestBid;
    private double percentChange;
    private double baseVolume;
    private double quoteVolume;
    private int isFrozen;
    private double dayHigh;
    private double dayLow;

    public static TickerPlot from(String[] arr) {
        TickerPlot p = new TickerPlot();

        p.setCurrencyCombination(arr[0]);
        p.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        p.setLast(Double.parseDouble(arr[1]));
        p.setLowestAsk(Double.parseDouble(arr[2]));
        p.setHighestBid(Double.parseDouble(arr[3]));
        p.setPercentChange(Double.parseDouble(arr[4]));
        p.setBaseVolume(Double.parseDouble(arr[5]));
        p.setQuoteVolume(Double.parseDouble(arr[6]));
        p.setIsFrozen(Integer.parseInt(arr[7]));
        p.setDayHigh(Double.parseDouble(arr[8]));
        p.setDayLow(Double.parseDouble(arr[9]));

        p.setId(p.getCurrencyCombination() + "-" + p.getTimestamp());

        return p;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    public double getLast() {
        return last;
    }

    public void setLast(double last) {
        this.last = last;
    }

    public double getLowestAsk() {
        return lowestAsk;
    }

    public void setLowestAsk(double lowestAsk) {
        this.lowestAsk = lowestAsk;
    }

    public double getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(double highestBid) {
        this.highestBid = highestBid;
    }

    public double getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(double percentChange) {
        this.percentChange = percentChange;
    }

    public double getBaseVolume() {
        return baseVolume;
    }

    public void setBaseVolume(double baseVolume) {
        this.baseVolume = baseVolume;
    }

    public double getQuoteVolume() {
        return quoteVolume;
    }

    public void setQuoteVolume(double quoteVolume) {
        this.quoteVolume = quoteVolume;
    }

    public int getIsFrozen() {
        return isFrozen;
    }

    public void setIsFrozen(int isFrozen) {
        this.isFrozen = isFrozen;
    }

    public double getDayHigh() {
        return dayHigh;
    }

    public void setDayHigh(double dayHigh) {
        this.dayHigh = dayHigh;
    }

    public double getDayLow() {
        return dayLow;
    }

    public void setDayLow(double dayLow) {
        this.dayLow = dayLow;
    }

    @Override
    public String toString() {
        return com.google.common.base.MoreObjects.toStringHelper(this)
                .add("currencyCombination", currencyCombination)
                .add("last", last)
                .add("lowestAsk", lowestAsk)
                .add("highestBid", highestBid)
                .add("percentChange", percentChange)
                .add("baseVolume", baseVolume)
                .add("quoteVolume", quoteVolume)
                .add("isFrozen", isFrozen)
                .add("dayHigh", dayHigh)
                .add("dayLow", dayLow)
                .toString();
    }
}
