package de.jverhoelen.cryptoalerts.ingestion.processor.indicator;

import com.google.common.base.MoreObjects;

import java.math.BigDecimal;
import java.util.Date;

public class SimplePlot {

    private Date time;
    private String currencyCombination;

    // open & close
    private BigDecimal open;
    private BigDecimal close;
    private BigDecimal baseVolume;

    // emas
    private BigDecimal ema12;
    private BigDecimal ema26;

    // indicator results
    private BigDecimal macd;
    private BigDecimal signal;
    private BigDecimal rsi;

    // gains and losses
    private BigDecimal gainLoss;
    private BigDecimal accumulatedGains;
    private BigDecimal averageGains;
    private BigDecimal accumulatedLosses;
    private BigDecimal averageLosses;

    public SimplePlot(String currencyCombination, Date time, BigDecimal open, BigDecimal close, BigDecimal baseVolume) {
        this.currencyCombination = currencyCombination;
        this.time = time;
        this.open = open;
        this.close = close;
        this.baseVolume = baseVolume;
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

    private BigDecimal calculateMacd() {
        if (ema26 != null && ema26.doubleValue() > 0) {
            return new BigDecimal(ema12.doubleValue() - ema26.doubleValue());
        }

        return BigDecimal.ZERO;
    }

    public void calculateAndSetMacd() {
        this.macd = calculateMacd();
    }

    public BigDecimal getBaseVolume() {
        return baseVolume;
    }

    public void setBaseVolume(BigDecimal baseVolume) {
        this.baseVolume = baseVolume;
    }

    public String getCurrencyCombination() {
        return currencyCombination;
    }

    public void setCurrencyCombination(String currencyCombination) {
        this.currencyCombination = currencyCombination;
    }

    public BigDecimal getMacd() {
        return macd;
    }

    public void setMacd(BigDecimal macd) {
        this.macd = macd;
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

    public BigDecimal getSignal() {
        return signal;
    }

    public void setSignal(BigDecimal signal) {
        this.signal = signal;
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
                .add("baseVolume", baseVolume)
                .add("ema12", ema12)
                .add("ema26", ema26)
                .add("macd", macd)
                .add("signal", signal)
                .add("rsi", rsi)
                .add("gainLoss", gainLoss)
                .add("accumulatedGains", accumulatedGains)
                .add("averageGains", averageGains)
                .add("accumulatedLosses", accumulatedLosses)
                .add("averageLosses", averageLosses)
                .toString();
    }
}
