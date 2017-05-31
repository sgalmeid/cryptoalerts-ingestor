package de.jverhoelen.cryptoalerts.ingestion.processor.indicator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class StatefulPlotIndicatorsCalculator {

    private PlotIndicatorsCalculator calculator;
    private Map<String, State> temp = new HashMap<>();

    @Autowired
    public StatefulPlotIndicatorsCalculator(PlotIndicatorsCalculator calculator) {
        this.calculator = calculator;
    }

    public SimplePlot processNext(SimplePlot plot) {
        temp.putIfAbsent(plot.getCurrencyCombination(), new State());

        State state = temp.get(plot.getCurrencyCombination());
        linkFromPrevious(plot, state);
        long currentNumber = state.getCurrentNumber();

        state.historizeIfRequired(plot);
        calculator.calculateIndicatorsForOnePlot(state.firstRequiredPlots, currentNumber, state.previous, plot);
        state.rememberAsPrevious(plot);

        return plot;
    }

    private void linkFromPrevious(SimplePlot currentPlot, State currencyIngestionState) {
        SimplePlot previous = currencyIngestionState.previous;
        if (previous != null) {
            currentPlot.setOpen(previous.getClose());
        } else {
            currentPlot.setOpen(currentPlot.getClose());
        }
        currentPlot.registerGainsLosses(previous);
    }

    private class State {
        private static final int REQUIRED_INITIAL_ENTRIES = 40;

        private List<SimplePlot> firstRequiredPlots;
        private SimplePlot previous;
        private AtomicLong counter;

        public State() {
            this.firstRequiredPlots = new ArrayList<>();
            this.counter = new AtomicLong(0);
            this.previous = null;
        }

        long getCurrentNumber() {
            return counter.incrementAndGet();
        }

        void historizeIfRequired(SimplePlot plot) {
            if (counter.get() < REQUIRED_INITIAL_ENTRIES) {
                firstRequiredPlots.add(plot);
            } else {
                firstRequiredPlots.clear();
            }
        }

        void rememberAsPrevious(SimplePlot plot) {
            previous = plot;
        }
    }
}
