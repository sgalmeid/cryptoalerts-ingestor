package de.jverhoelen.cryptoalerts.ingestion.ticker.indicator;

import de.jverhoelen.cryptoalerts.ingestion.ticker.plot.PriceChangeCalculationResult;
import de.jverhoelen.cryptoalerts.ingestion.ticker.plot.SimplePlot;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class StatefulPlotIndicatorsCalculator {

    private final int candlestickSize;
    private final ChronoUnit candlestickUnit;
    private PlotIndicatorsCalculator calculator;
    private Map<String, State> temp = new HashMap<>();

    @Autowired
    public StatefulPlotIndicatorsCalculator(PlotIndicatorsCalculator calculator,
                                            @Value("${ingest.ticker.candlestick.size}") int candlestickSize,
                                            @Value("${ingest.ticker.candlestick.unit}") String candlestickUnit) {
        this.calculator = calculator;
        this.candlestickSize = candlestickSize;
        this.candlestickUnit = ChronoUnit.valueOf(candlestickUnit);
    }

    public PriceChangeCalculationResult processPriceChange(SimplePlot plot) {
        temp.putIfAbsent(plot.getCurrencyCombination(), new State());

        Candle candleWithIndicators = null;

        // get state, entry number and link with previous price
        State state = temp.get(plot.getCurrencyCombination());
        linkFromPrevious(plot, state);
        long plotNumber = state.assignPlotNumber();

        if (state.newCandleShouldStart()) {
            Candle previousCandle = state.getPreviousCandle();
            Candle finishedCandle = state.startNewCandle(plot);

            if (finishedCandle != null) {
                long candleNumber = state.assignCandleNumber();

                candleWithIndicators = calculator.calculateMacdIndicators(new ArrayList<>(state.previousCandles), candleNumber, previousCandle, finishedCandle);
            }
        }

        // calculate RSI for all price changes
        calculator.calculateRsi(plotNumber, state.previousPrice, plot);
        state.rememberPrice(plot);

        return PriceChangeCalculationResult.from(plot, candleWithIndicators);
    }

    private void linkFromPrevious(SimplePlot currentPlot, State currencyIngestionState) {
        SimplePlot previous = currencyIngestionState.previousPrice;
        if (previous != null) {
            currentPlot.setOpen(previous.getClose());
        } else {
            currentPlot.setOpen(currentPlot.getClose());
        }
        currentPlot.registerGainsLosses(previous);
    }

    private class State {

        private LocalDateTime candleStartTime;

        private Queue<Candle> previousCandles;
        private Candle currentCandle;
        private AtomicLong candleCounter;

        private SimplePlot previousPrice;
        private AtomicLong plotCounter;

        public State() {
            this.previousCandles = new CircularFifoQueue<>(40);
            this.candleCounter = new AtomicLong(0);
            this.plotCounter = new AtomicLong(0);
        }

        long assignPlotNumber() {
            return plotCounter.incrementAndGet();
        }

        long assignCandleNumber() {
            return candleCounter.incrementAndGet();
        }

        boolean newCandleShouldStart() {
            return candleStartTime == null || LocalDateTime.now().isAfter(candleStartTime.plus(candlestickSize, candlestickUnit));
        }

        void rememberPrice(SimplePlot plot) {
            previousPrice = plot;
        }

        Candle startNewCandle(SimplePlot withEndPlot) {
            Candle finished = null;
            if (currentCandle != null) {
                finished = currentCandle.finishOnEnd(withEndPlot);
                previousCandles.add(finished);
            }

            currentCandle = new Candle(withEndPlot);
            candleStartTime = LocalDateTime.now();

            return finished;
        }

        public Candle getPreviousCandle() {
            if (previousCandles.isEmpty()) {
                return null;
            }

            List<Candle> candles = new ArrayList<>(previousCandles);

            return candles.get(candles.size() - 1);
        }
    }
}
