package de.jverhoelen.cryptoalerts.ingestion.ticker.indicator;

import com.google.common.base.MoreObjects;
import de.jverhoelen.cryptoalerts.ingestion.ticker.plot.SimplePlot;

import java.math.BigDecimal;
import java.util.Date;

public class Candle {

    private String currencyCombination;
    private Date start;
    private Date end;

    // open & close
    private BigDecimal open;
    private BigDecimal close;

    // emas
    private BigDecimal firstEma;
    private BigDecimal secondEma;

    // plot results
    private BigDecimal macd;
    private BigDecimal signal;

    public Candle(SimplePlot start) {
        this.currencyCombination = start.getCurrencyCombination();
        this.open = start.getOpen();
        this.start = start.getTime();
    }

    public Candle finishOnEnd(SimplePlot end) {
        this.close = end.getClose();
        this.end = end.getTime();
        return this;
    }

    private BigDecimal calculateMacd() {
        if (secondEma != null && secondEma.doubleValue() > 0) {
            return new BigDecimal(firstEma.doubleValue() - secondEma.doubleValue());
        }

        return BigDecimal.ZERO;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public void calculateAndSetMacd() {
        this.macd = calculateMacd();
    }

    public String getCurrencyCombination() {
        return currencyCombination;
    }

    public void setCurrencyCombination(String currencyCombination) {
        this.currencyCombination = currencyCombination;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getFirstEma() {
        return firstEma;
    }

    public void setFirstEma(BigDecimal firstEma) {
        this.firstEma = firstEma;
    }

    public BigDecimal getSecondEma() {
        return secondEma;
    }

    public void setSecondEma(BigDecimal secondEma) {
        this.secondEma = secondEma;
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("currencyCombination", currencyCombination)
                .add("start", start)
                .add("end", end)
                .add("open", open)
                .add("close", close)
                .add("firstEma", firstEma)
                .add("secondEma", secondEma)
                .add("macd", macd)
                .add("signal", signal)
                .toString();
    }
}
