package de.jverhoelen.cryptoalerts;

import de.jverhoelen.cryptoalerts.currency.CryptoCurrency;
import de.jverhoelen.cryptoalerts.currency.ExchangeCurrency;
import de.jverhoelen.cryptoalerts.currency.combination.IndexedCurrencyCombination;
import de.jverhoelen.cryptoalerts.currency.combination.IndexedCurrencyCombinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@SpringBootApplication
public class PoloniexTickerConsumerApplication {

    @Autowired
    private IndexedCurrencyCombinationService indexedCurrencyCombinations;

    public static void main(String[] args) {
        SpringApplication.run(PoloniexTickerConsumerApplication.class, args);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @PostConstruct
    public void insertIngestedCurrencyCombinations() {
        if (indexedCurrencyCombinations.isEmpty()) {
            indexedCurrencyCombinations.add(Arrays.asList(
                    IndexedCurrencyCombination.of(CryptoCurrency.XRP, ExchangeCurrency.BTC),
                    IndexedCurrencyCombination.of(CryptoCurrency.ETH, ExchangeCurrency.BTC),
                    IndexedCurrencyCombination.of(CryptoCurrency.LTC, ExchangeCurrency.BTC)
//                    IndexedCurrencyCombination.of(CryptoCurrency.BTC, ExchangeCurrency.USDT)
            ));
        }
    }
}
