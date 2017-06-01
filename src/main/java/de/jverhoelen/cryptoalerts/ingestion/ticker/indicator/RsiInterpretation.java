package de.jverhoelen.cryptoalerts.ingestion.ticker.indicator;

import java.math.BigDecimal;

public enum RsiInterpretation {
    NONE(0),
    OVER_BOUGHT(70.0),
    OVER_SOLD(30.0);

    private double value;

    RsiInterpretation(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public static RsiInterpretation interpretRsi(BigDecimal rsi) {
        if (rsi == null || rsi.doubleValue() == 0) {
            return NONE;
        }
        double value = rsi.doubleValue();

        if (value >= OVER_BOUGHT.getValue()) {
            return OVER_BOUGHT;
        }
        if (value <= OVER_SOLD.getValue()) {
            return OVER_SOLD;
        }

        return NONE;
    }
}
