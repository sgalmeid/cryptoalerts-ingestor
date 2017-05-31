package de.jverhoelen.cryptoalerts.ingestion;

import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class IngestedTickerPlotTest {

    @Test
    public void from() throws Exception {
        String[] testData = new String[]{
                "BTC_DASH",
                "3",
                "0",
                "0",
                "0",
                "3000",
                "30",
                "0",
                "0",
                "5",
                "3",
        };

        IngestedTickerPlot result = IngestedTickerPlot.from(testData);
        assertThat(result.getCurrencyCombination(), is("BTC_DASH"));
        assertThat(result.getLast(), is(new BigDecimal(3.0)));
        assertThat(result.getBaseVolume(), is(new BigDecimal(3000.0)));
    }
}