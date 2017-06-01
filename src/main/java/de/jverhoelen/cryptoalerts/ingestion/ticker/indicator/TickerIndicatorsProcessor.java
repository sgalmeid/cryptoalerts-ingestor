package de.jverhoelen.cryptoalerts.ingestion.ticker.indicator;

import de.jverhoelen.cryptoalerts.currency.combination.IndexedCurrencyCombinationService;
import de.jverhoelen.cryptoalerts.ingestion.ElasticsearchIndexClient;
import de.jverhoelen.cryptoalerts.ingestion.ticker.plot.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TickerIndicatorsProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TickerIndicatorsProcessor.class);

    private static final String TICKER_INDEX = "poloniex/ticker";
    private static final String RSI_INDEX = "poloniex/rsi";
    private static final String SIGNAL_INDEX = "poloniex/signal";
    private static final String MACD_INDEX = "poloniex/macd";

    private List<String> combinations;

    private StatefulPlotIndicatorsCalculator statefulCalculator;
    private ElasticsearchIndexClient elasticsearchClient;
    private IndexedCurrencyCombinationService currencyCombinations;

    @Autowired
    public TickerIndicatorsProcessor(StatefulPlotIndicatorsCalculator statefulCalculator,
                                     ElasticsearchIndexClient elasticsearchClient,
                                     IndexedCurrencyCombinationService currencyCombinations) {
        this.statefulCalculator = statefulCalculator;
        this.elasticsearchClient = elasticsearchClient;
        this.currencyCombinations = currencyCombinations;
    }

    @PostConstruct
    public void init() {
        combinations = currencyCombinations.getAll()
                .stream()
                .map(cc -> cc.toApiKey())
                .collect(Collectors.toList());
    }

    public void processNewPlot(TickerPlot tickerPlot) {
        String currencyCombination = tickerPlot.getCurrencyCombination();

        if (combinations.contains(currencyCombination)) {
            ingestTickerPlot(tickerPlot);
            calculateAndIngestIndicators(tickerPlot);
        }
    }

    void calculateAndIngestIndicators(TickerPlot ingestedPlot) {
        SimplePlot plot = new SimplePlot(
                ingestedPlot.getCurrencyCombination(),
                ingestedPlot.getEventTimestamp(),
                BigDecimal.ZERO,
                ingestedPlot.getValue()
        );

        PriceChangeCalculationResult result = statefulCalculator.processPriceChange(plot);
        RsiPlot rsiPlot = new RsiPlot(result.getPlotWithIndicators());
        ingestRsiPlot(rsiPlot);

        if (result.getFinishedCandle() != null) {
            MacdPlot macdPlot = new MacdPlot(result.getFinishedCandle());
            SignalPlot signalPlot = new SignalPlot(result.getFinishedCandle());

            ingestMacdPlot(macdPlot);
            ingestSignalPlot(signalPlot);
        }
    }

    void ingestMacdPlot(MacdPlot macdPlot) {
        ingest(MACD_INDEX, macdPlot, macdPlot.getId());
    }

    void ingestSignalPlot(SignalPlot signalPlot) {
        ingest(SIGNAL_INDEX, signalPlot, signalPlot.getId());
    }

    void ingestRsiPlot(RsiPlot plotWithRsi) {
        ingest(RSI_INDEX, plotWithRsi, plotWithRsi.getId());
    }

    void ingestTickerPlot(TickerPlot plot) {
        ingest(TICKER_INDEX, plot, plot.getId());
    }

    private <T> void ingest(String index, T object, String id) {
        try {
            elasticsearchClient.putIntoIndex(object, index, id);
        } catch (URISyntaxException e) {
            LOGGER.error("URI could not be created.", e);
        }
    }
}
