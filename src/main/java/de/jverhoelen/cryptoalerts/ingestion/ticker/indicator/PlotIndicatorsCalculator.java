package de.jverhoelen.cryptoalerts.ingestion.ticker.indicator;

import de.jverhoelen.cryptoalerts.ingestion.ticker.plot.SimplePlot;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.CEILING;

@Component
public class PlotIndicatorsCalculator {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100.0);
    private static final BigDecimal THIRTEEN = new BigDecimal(13.0);
    private static final BigDecimal FOURTEEN = new BigDecimal(14.0);

    private static final int FIRST_EMA = 12;
    private static final int SECOND_EMA = 26;
    private static final int SIGNAL_MACD_EMA = 9;
    private static final int SIGNAL_START = SECOND_EMA + SIGNAL_MACD_EMA;
    private static final int RSI_START = 15;

    public Candle calculateMacdIndicators(List<Candle> allCandles, long plotNumber, Candle previous, Candle current) {
        // ema 12
        if (plotNumber == FIRST_EMA) {
            current.setFirstEma(calculateStartEma(FIRST_EMA, allCandles));
        } else if (plotNumber > FIRST_EMA) {
            current.setFirstEma(calculateFurtherEma(FIRST_EMA, current.getClose(), previous.getFirstEma()));
        }

        // ema 26
        if (plotNumber == SECOND_EMA) {
            current.setSecondEma(calculateStartEma(SECOND_EMA, allCandles));
        } else if (plotNumber > SECOND_EMA) {
            current.setSecondEma(calculateFurtherEma(SECOND_EMA, current.getFirstEma(), previous.getSecondEma()));
        }
        current.calculateAndSetMacd();

        // signal on and from plot 35 on
        if (plotNumber == SIGNAL_START) {
            current.setSignal(calculateStartSignal(allCandles));
        } else if (plotNumber > SIGNAL_START) {
            current.setSignal(calculateFurtherSignal(current.getMacd(), previous.getSignal()));
        }

        return current;
    }

    public void calculateRsi(long plotNumber, SimplePlot previous, SimplePlot current) {
        // average gains and losses and relative strength index
        if (plotNumber >= RSI_START) {
            if (plotNumber == RSI_START) {
                current.setAverageGains(calculateStartAvgGain(current));
                current.setAverageLosses(calculateStartAvgLoss(current));
            } else {
                current.setAverageGains(calculateFurtherAvgGain(previous, current));
                current.setAverageLosses(calculateFurtherAvgLoss(previous, current));
            }

            current.setRsi(calculateRsi(current));
        }
    }

    BigDecimal calculateFurtherAvgGain(SimplePlot previous, SimplePlot current) {
        BigDecimal gain = current.getGainLoss().doubleValue() < 0 ? ZERO : current.getGainLoss();
        return previous
                .getAverageGains()
                .multiply(THIRTEEN)
                .add(gain)
                .divide(FOURTEEN, CEILING);
    }

    BigDecimal calculateStartAvgGain(SimplePlot current) {
        return new BigDecimal(current.getAccumulatedGains().doubleValue() / 14);
    }

    BigDecimal calculateFurtherAvgLoss(SimplePlot previous, SimplePlot current) {
        BigDecimal loss = current.getGainLoss().doubleValue() > 0 ? ZERO : current.getGainLoss().multiply(new BigDecimal(-1));
        return new BigDecimal(((previous.getAverageLosses().multiply(new BigDecimal(13.0))).add(loss)).doubleValue() / 14.0);
    }

    BigDecimal calculateStartAvgLoss(SimplePlot current) {
        return current.getAccumulatedLosses().divide(FOURTEEN, CEILING);
    }

    BigDecimal calculateRsi(SimplePlot current) {
        if (current.getAverageLosses().doubleValue() == 0) {
            return ONE_HUNDRED;
        } else {
            BigDecimal rs = new BigDecimal(current.getAverageGains().doubleValue() / current.getAverageLosses().doubleValue());
            return new BigDecimal(100 - (100 / (1 + rs.doubleValue())));
        }
    }

    BigDecimal calculateStartEma(int emaNumber, List<Candle> candles) {
        List<Candle> emaCandles = candles.subList(0, emaNumber);
        BigDecimal sum = candlesPropertySum(emaCandles, p -> p.getClose());
        return new BigDecimal(sum.doubleValue() / emaCandles.size());
    }

    BigDecimal calculateFurtherEma(int emaNumber, BigDecimal thisRowsClosePrice, BigDecimal previousEma) {
        BigDecimal weight = new BigDecimal(2.0 / (emaNumber + 1));

        BigDecimal left = thisRowsClosePrice.multiply(weight);
        BigDecimal right = previousEma.multiply(ONE.subtract(weight));

        return left.add(right);
    }

    BigDecimal calculateStartSignal(List<Candle> candles) {
        int startIndex = SECOND_EMA - 1;
        int endIndex = SIGNAL_START - 1;
        List<Candle> signalCandles = candles.subList(startIndex, endIndex);
        BigDecimal macdSum = candlesPropertySum(signalCandles, p -> p.getMacd());

        return new BigDecimal(macdSum.doubleValue() / (endIndex - startIndex));
    }

    BigDecimal calculateFurtherSignal(BigDecimal thisRowsMacd, BigDecimal previousSignal) {
        return calculateFurtherEma(9, thisRowsMacd, previousSignal);
    }

//    BigDecimal plotsPropertySum(List<SimplePlot> plots, Function<SimplePlot, BigDecimal> propertyFunction) {
//        return plots.stream().map(propertyFunction).reduce(ZERO, BigDecimal::add);
//    }

    BigDecimal candlesPropertySum(List<Candle> plots, Function<Candle, BigDecimal> propertyFunction) {
        return plots.stream().map(propertyFunction).reduce(ZERO, BigDecimal::add);
    }
}
