package de.jverhoelen.cryptoalerts.ingestion.processor.indicator;

import de.jverhoelen.cryptoalerts.ingestion.processor.indicator.SimplePlot;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Function;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.CEILING;

@Component
public class PlotIndicatorsCalculator {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100.0);

    public List<SimplePlot> calculateIndicators(List<SimplePlot> plots) {
        long rowCounter = 1;
        SimplePlot previous = null;

        for (SimplePlot plot : plots) {
            calculateIndicatorsForOnePlot(plots, rowCounter, previous, plot);

            previous = plot;
            rowCounter++;
        }

        return plots;
    }

    void calculateIndicatorsForOnePlot(List<SimplePlot> allPlots, long plotNumber, SimplePlot previous, SimplePlot current) {
        // ema 12
        if (plotNumber == 12) {
            current.setTwelveDaysEma(calculateStartEma(12, allPlots));
        } else if (plotNumber > 12) {
            current.setTwelveDaysEma(calculateFurtherEma(12, current.getClose(), previous.getTwelveDaysEma()));
        }

        // ema 26
        if (plotNumber == 26) {
            current.setTwentySixDaysEma(calculateStartEma(26, allPlots));
        } else if (plotNumber > 26) {
            current.setTwentySixDaysEma(calculateFurtherEma(26, current.getTwelveDaysEma(), previous.getTwentySixDaysEma()));
        }

        // signal on and from plot 35 on
        if (plotNumber == 35) {
            current.setSignal(calculateStartSignal(allPlots));
        } else if (plotNumber > 35) {
            current.setSignal(calculateFurtherSignal(current.getMacd(), previous.getSignal()));
        }

        // average gains and losses and relative strength index
        if (plotNumber == 15) {
            current.setAverageGains(calculateStartAvgGain(current));
            current.setAverageLosses(calculateStartAvgLoss(current));
            current.setRsi(calculateRsi(current));
        } else if (plotNumber > 15) {
            current.setAverageGains(calculateFurtherAvgGain(previous, current));
            current.setAverageLosses(calculateFurtherAvgLoss(previous, current));
            current.setRsi(calculateRsi(current));
        }
    }

    BigDecimal calculateFurtherAvgGain(SimplePlot previous, SimplePlot current) {
        BigDecimal gain = current.getGainLoss().doubleValue() < 0 ? ZERO : current.getGainLoss();
        return previous
                .getAverageGains()
                .multiply(new BigDecimal(13.0))
                .add(gain)
                .divide(new BigDecimal(14.0), CEILING);
    }

    BigDecimal calculateStartAvgGain(SimplePlot current) {
        return current.getAccumulatedGains().divide(new BigDecimal(14), CEILING);
    }

    BigDecimal calculateFurtherAvgLoss(SimplePlot previous, SimplePlot current) {
        BigDecimal loss = current.getGainLoss().doubleValue() > 0 ? ZERO : current.getGainLoss().multiply(new BigDecimal(-1));
        return ((previous.getAverageLosses().multiply(new BigDecimal(13.0))).add(loss)).divide(new BigDecimal(14.0), CEILING);
    }

    BigDecimal calculateStartAvgLoss(SimplePlot current) {
        return current.getAccumulatedLosses().divide(new BigDecimal(14.0), CEILING);
    }

    BigDecimal calculateRsi(SimplePlot current) {
        if (current.getAverageLosses().doubleValue() == 0) {
            return ONE_HUNDRED;
        } else {
            BigDecimal rs = current.getAverageGains().divide(current.getAverageLosses(), CEILING);
            return ONE_HUNDRED.subtract(ONE_HUNDRED.divide(ONE.add(rs), CEILING));
        }
    }

    BigDecimal calculateStartEma(int emaNumber, List<SimplePlot> plots) {
        List<SimplePlot> emaPlots = plots.subList(0, emaNumber);
        return plotsPropertySum(emaPlots, p -> p.getClose()).divide(new BigDecimal(emaPlots.size()), RoundingMode.FLOOR);
    }

    BigDecimal calculateFurtherEma(int emaNumber, BigDecimal thisRowsClosePrice, BigDecimal previousEma) {
        BigDecimal weight = new BigDecimal(2.0).divide(new BigDecimal(emaNumber + 1), CEILING);

        BigDecimal left = thisRowsClosePrice.multiply(weight);
        BigDecimal right = previousEma.multiply(ONE.subtract(weight));

        return left.add(right);
    }

    BigDecimal calculateStartSignal(List<SimplePlot> plots) {
        int startIndex = 26 - 1;
        int endIndex = 35 - 1;
        List<SimplePlot> signalPlots = plots.subList(startIndex, endIndex);

        return plotsPropertySum(signalPlots, p -> p.getMacd()).divide(new BigDecimal(endIndex - startIndex), CEILING);
    }

    BigDecimal calculateFurtherSignal(BigDecimal thisRowsMacd, BigDecimal previousSignal) {
        return calculateFurtherEma(9, thisRowsMacd, previousSignal);
    }

    BigDecimal plotsPropertySum(List<SimplePlot> plots, Function<SimplePlot, BigDecimal> propertyFunction) {
        return plots.stream().map(propertyFunction).reduce(ZERO, BigDecimal::add);
    }
}
