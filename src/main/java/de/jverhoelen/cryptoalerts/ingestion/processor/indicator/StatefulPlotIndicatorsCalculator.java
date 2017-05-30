package de.jverhoelen.cryptoalerts.ingestion.processor.indicator;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class StatefulPlotIndicatorsCalculator {

    @Autowired
    private PlotIndicatorsCalculator calculator;

    private Map<String, State> temp = new HashMap<>();

    public SimplePlot processNext(SimplePlot plot) {
        State state = temp.get(plot.getCurrencyCombination());
        long currentNumber = state.getCurrentNumber();

        calculator.calculateIndicatorsForOnePlot(new ArrayList<>(state.firstRequiredPlots), currentNumber, state.previous, plot);
        state.historizeIfRequired(plot);

        return plot;
    }

    private class State {
        private static final int REQUIRED_INITIAL_ENTRIES = 36;

        private Queue<SimplePlot> firstRequiredPlots;
        private SimplePlot previous;
        private AtomicLong counter;

        public State() {
            this.firstRequiredPlots = new CircularFifoQueue<>(36);
            this.counter = new AtomicLong(0);
            this.previous = null;
        }

        long getCurrentNumber() {
            return counter.incrementAndGet();
        }

        void historizeIfRequired(SimplePlot plot) {
            if(counter.get() < REQUIRED_INITIAL_ENTRIES) {
                firstRequiredPlots.add(plot);
            } else {
                firstRequiredPlots = new CircularFifoQueue<>(0);
            }

            previous = plot;
        }
    }
}
