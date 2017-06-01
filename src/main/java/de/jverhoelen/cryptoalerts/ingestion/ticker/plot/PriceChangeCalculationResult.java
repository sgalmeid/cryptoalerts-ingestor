package de.jverhoelen.cryptoalerts.ingestion.ticker.plot;

import de.jverhoelen.cryptoalerts.ingestion.ticker.indicator.Candle;

public class PriceChangeCalculationResult {

    private SimplePlot plotWithIndicators;
    private Candle finishedCandle;

    private PriceChangeCalculationResult(SimplePlot plotWithIndicators, Candle finishedCandle) {
        this.plotWithIndicators = plotWithIndicators;
        this.finishedCandle = finishedCandle;
    }

    public static PriceChangeCalculationResult from(SimplePlot plot, Candle candle) {
        return new PriceChangeCalculationResult(plot, candle);
    }

    public SimplePlot getPlotWithIndicators() {
        return plotWithIndicators;
    }

    public Candle getFinishedCandle() {
        return finishedCandle;
    }
}
