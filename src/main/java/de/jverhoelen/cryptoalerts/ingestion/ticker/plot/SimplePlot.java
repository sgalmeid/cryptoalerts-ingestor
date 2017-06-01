package de.jverhoelen.cryptoalerts.ingestion.ticker.plot;

import com.google.common.base.MoreObjects;

import java.math.BigDecimal;
import java.util.Date;

public class SimplePlot {

    private Date time;
    private String currencyCombination;

    // open & close
    private BigDecimal open;
    private BigDecimal close;

    // plot results
    private BigDecimal rsi;

    // gains and losses
    private BigDecimal gainLoss;
    private BigDecimal accumulatedGains;
    private BigDecimal averageGains;
    private BigDecimal accumulatedLosses;
    private BigDecimal averageLosses;

    public SimplePlot(String currencyCombination, Date time, BigDecimal open, BigDecimal close) {
        this.currencyCombination = currencyCombination;
        this.time = time;
        this.open = open;
        this.close = close;
        this.gainLoss = close.subtract(open);
    }

    public void registerGainsLosses(SimplePlot previousPlot) {
        BigDecimal previousGains = previousPlot == null ? BigDecimal.ZERO : previousPlot.getAccumulatedGains();
        BigDecimal previousLosses = previousPlot == null ? BigDecimal.ZERO : previousPlot.getAccumulatedLosses();

        if (gainLoss.doubleValue() > 0) {
            accumulatedGains = new BigDecimal(previousGains.doubleValue() + gainLoss.doubleValue());
            accumulatedLosses = previousLosses;
        } else if (gainLoss.doubleValue() < 0) {
            accumulatedLosses = new BigDecimal(previousLosses.doubleValue() + (gainLoss.doubleValue() * -1));
            accumulatedGains = previousGains;
        } else if (gainLoss.doubleValue() == 0) {
            accumulatedLosses = previousLosses;
            accumulatedGains = previousGains;
        }
    }

    public String getCurrencyCombination() {
        return currencyCombination;
    }

    public void setCurrencyCombination(String currencyCombination) {
        this.currencyCombination = currencyCombination;
    }

    public void setAverageGains(BigDecimal averageGains) {
        this.averageGains = averageGains;
    }

    public void setAverageLosses(BigDecimal averageLosses) {
        this.averageLosses = averageLosses;
    }

    public BigDecimal getAverageGains() {
        return averageGains;
    }

    public BigDecimal getAverageLosses() {
        return averageLosses;
    }

    public BigDecimal getAccumulatedGains() {
        return accumulatedGains;
    }

    public void setAccumulatedGains(BigDecimal accumulatedGains) {
        this.accumulatedGains = accumulatedGains;
    }

    public BigDecimal getAccumulatedLosses() {
        return accumulatedLosses;
    }

    public void setAccumulatedLosses(BigDecimal accumulatedLosses) {
        this.accumulatedLosses = accumulatedLosses;
    }

    public BigDecimal getGainLoss() {
        return gainLoss;
    }

    public BigDecimal getRsi() {
        return rsi;
    }

    public void setRsi(BigDecimal rsi) {
        this.rsi = rsi;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
        this.gainLoss = this.close.subtract(this.open);
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
        this.gainLoss = this.close.subtract(this.open);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimplePlot that = (SimplePlot) o;

        return time != null ? time.equals(that.time) : that.time == null;
    }

    @Override
    public int hashCode() {
        return time != null ? time.hashCode() : 0;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("time", time)
                .add("currencyCombination", currencyCombination)
                .add("open", open)
                .add("close", close)
                .add("rsi", rsi)
                .add("gainLoss", gainLoss)
                .add("accumulatedGains", accumulatedGains)
                .add("averageGains", averageGains)
                .add("accumulatedLosses", accumulatedLosses)
                .add("averageLosses", averageLosses)
                .toString();
    }
}
