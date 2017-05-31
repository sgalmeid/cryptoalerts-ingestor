package de.jverhoelen.cryptoalerts.ingestion.processor;

import de.jverhoelen.cryptoalerts.currency.combination.IndexedCurrencyCombinationService;
import de.jverhoelen.cryptoalerts.ingestion.ElasticsearchIndexClient;
import de.jverhoelen.cryptoalerts.ingestion.IngestedTickerPlot;
import de.jverhoelen.cryptoalerts.ingestion.processor.indicator.SimplePlot;
import de.jverhoelen.cryptoalerts.ingestion.processor.indicator.StatefulPlotIndicatorsCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TickerIndicatorsProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TickerIndicatorsProcessor.class);
    private static final String TICKER_INDEX = "poloniex/ticker";

    private Map<String, LocalDateTime> combinations;

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
                .map(apiKey -> new AbstractMap.SimpleEntry<>(apiKey, LocalDateTime.MIN))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }

    public void processNewPlot(IngestedTickerPlot ingestedPlot) {
        String currencyCombination = ingestedPlot.getCurrencyCombination();

        if (combinations.containsKey(currencyCombination)) {
            LocalDateTime lastIngest = combinations.get(currencyCombination);

            if (lastIngest.equals(LocalDateTime.MIN) || LocalDateTime.now().isAfter(lastIngest.plusSeconds(30))) {
                calculateAndPersist(ingestedPlot);
                combinations.put(currencyCombination, LocalDateTime.now());
            }
        }
    }

    void calculateAndPersist(IngestedTickerPlot ingestedPlot) {
        // SimplePlot as computation base
        SimplePlot plot = new SimplePlot(
                ingestedPlot.getCurrencyCombination(),
                ingestedPlot.getTimestamp(),
                BigDecimal.ZERO,
                ingestedPlot.getLast(),
                ingestedPlot.getBaseVolume()
        );

        // calculate indicators
        SimplePlot plotWithIndicators = statefulCalculator.processNext(plot);
        IndexedTickerPlot indexablePlot = new IndexedTickerPlot(plotWithIndicators);

        // index in ES
        try {
            elasticsearchClient.putIntoIndex(indexablePlot, TICKER_INDEX, indexablePlot.getId());
        } catch (URISyntaxException e) {
            LOGGER.error("URI could not be created.", e);
        }
    }
}
