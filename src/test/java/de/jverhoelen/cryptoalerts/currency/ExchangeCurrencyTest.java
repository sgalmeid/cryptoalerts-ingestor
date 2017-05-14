package de.jverhoelen.cryptoalerts.currency;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ExchangeCurrencyTest {
    @Test
    public void byFullName() throws Exception {
        ExchangeCurrency btc = ExchangeCurrency.byFullName("btc");
        assertThat(btc, is(ExchangeCurrency.BTC));
    }

    @Test
    public void byShortName() throws Exception {
        ExchangeCurrency btc = ExchangeCurrency.byFullName("btc");
        assertThat(btc, is(ExchangeCurrency.BTC));

        ExchangeCurrency usdt = ExchangeCurrency.byFullName("$");
        assertThat(usdt, is(ExchangeCurrency.USDT));
    }

    @Test
    public void getFullName() throws Exception {
        String fullName = ExchangeCurrency.BTC.getFullName();
        assertThat(fullName, is("BTC"));
    }

}