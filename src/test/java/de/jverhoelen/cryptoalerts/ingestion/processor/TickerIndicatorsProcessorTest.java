package de.jverhoelen.cryptoalerts.ingestion.processor;

import de.jverhoelen.cryptoalerts.currency.CryptoCurrency;
import de.jverhoelen.cryptoalerts.currency.ExchangeCurrency;
import de.jverhoelen.cryptoalerts.currency.combination.IndexedCurrencyCombination;
import de.jverhoelen.cryptoalerts.currency.combination.IndexedCurrencyCombinationService;
import de.jverhoelen.cryptoalerts.ingestion.ElasticsearchIndexClient;
import de.jverhoelen.cryptoalerts.ingestion.IngestedTickerPlot;
import de.jverhoelen.cryptoalerts.ingestion.processor.indicator.PlotIndicatorsCalculator;
import de.jverhoelen.cryptoalerts.ingestion.processor.indicator.StatefulPlotIndicatorsCalculator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import static org.mockito.Mockito.when;

public class TickerIndicatorsProcessorTest {

    StatefulPlotIndicatorsCalculator statefulCalculator = new StatefulPlotIndicatorsCalculator(new PlotIndicatorsCalculator());
    TickerIndicatorsProcessor processor;

    @Before
    public void init() {
        ElasticsearchIndexClient es = Mockito.mock(ElasticsearchIndexClient.class);
        IndexedCurrencyCombinationService indexedCCs = Mockito.mock(IndexedCurrencyCombinationService.class);

        processor = new TickerIndicatorsProcessor(statefulCalculator, es, indexedCCs);

        when(indexedCCs.getAll()).thenReturn(Arrays.asList(IndexedCurrencyCombination.of(CryptoCurrency.ETH, ExchangeCurrency.BTC)));
    }

    @Test
    public void calculateAndPersist() throws Exception {

        double last = 0.1;

        for (int i = 0; i < 50; i++) {
            IngestedTickerPlot plot = new IngestedTickerPlot();
            last = last * 1.005;

            plot.setBaseVolume(new BigDecimal((i + 1) * 0.01 * 60000));
            plot.setLast(new BigDecimal(last));
            plot.setCurrencyCombination("BTC_ETH");
            plot.setTimestamp(new Date());

            processor.calculateAndPersist(plot);
        }
    }

    @Test
    public void calculateAndPersistFromKnownValues() throws Exception {

        String numbers = "459.99,448.85,446.06,450.81,442.8,448.97,444.57,441.4,430.47,420.05,431.14,425.66,430.58,431.72,437.87,428.43,428.35,432.5,443.66,455.72,454.49,452.08,452.73,461.91,463.58,461.14,452.08,442.66,428.91,429.79,431.99,427.72,423.2,426.21,426.98,435.69,434.33,429.8,419.85,426.24,402.8,392.05,390.53,398.67,406.13,405.46,408.38,417.2,430.12,442.78,439.29,445.52,449.98,460.71,458.66,463.84,456.77,452.97,454.74,443.86,428.85,434.58,433.26,442.93,439.66,441.35";
        String[] splittedNumbers = numbers.split(",");


        for (String numberStr : splittedNumbers) {
            IngestedTickerPlot plot = new IngestedTickerPlot();

            plot.setBaseVolume(new BigDecimal(0.0));
            plot.setLast(new BigDecimal(numberStr));
            plot.setCurrencyCombination("BTC_ETH");
            plot.setTimestamp(new Date());

            processor.calculateAndPersist(plot);
        }
    }

}