package de.jverhoelen.cryptoalerts.currency.combination;

import de.jverhoelen.cryptoalerts.currency.CryptoCurrency;
import de.jverhoelen.cryptoalerts.currency.ExchangeCurrency;
import de.jverhoelen.cryptoalerts.datautils.repo.Repository;
import org.springframework.stereotype.Component;

@Component
public interface IndexedCurrencyCombinationRepository extends Repository<IndexedCurrencyCombination, Long> {

    IndexedCurrencyCombination findByCryptoAndExchange(CryptoCurrency crypto, ExchangeCurrency exchange);
}
