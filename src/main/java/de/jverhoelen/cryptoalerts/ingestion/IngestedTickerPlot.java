package de.jverhoelen.cryptoalerts.ingestion;

import com.google.common.base.MoreObjects;

import java.util.Date;

public class IngestedTickerPlot {

    private String currencyCombination;
    private Date timestamp;
    private double last;
    private double baseVolume;

    public static IngestedTickerPlot from(String[] arr) {
        IngestedTickerPlot p = new IngestedTickerPlot();

        p.setTimestamp(new Date());
        p.setCurrencyCombination(arr[0]);
        p.setLast(Double.parseDouble(arr[1]));
        p.setBaseVolume(Double.parseDouble(arr[5]));

        return p;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
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

    public double getBaseVolume() {
        return baseVolume;
    }

    public void setBaseVolume(double baseVolume) {
        this.baseVolume = baseVolume;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("currencyCombination", currencyCombination)
                .add("last", last)
                .add("baseVolume", baseVolume)
                .toString();
    }
}
