package de.jverhoelen.cryptoalerts.currency;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class CryptoCurrencyTest {
    @Test
    public void byFullName() throws Exception {
        CryptoCurrency btc = CryptoCurrency.byFullName("bitcoin");
        assertThat(btc, is(CryptoCurrency.BTC));
    }

    @Test
    public void byShortName() throws Exception {
        CryptoCurrency btc = CryptoCurrency.byFullName("bitcoin");
        assertThat(btc, is(CryptoCurrency.BTC));

        CryptoCurrency ltc = CryptoCurrency.byFullName("LiTeCoIn");
        assertThat(ltc, is(CryptoCurrency.LTC));
    }

    @Test
    public void getFullName() throws Exception {
        String fullName = CryptoCurrency.BTC.getFullName();
        assertThat(fullName, is("Bitcoin"));
    }
}