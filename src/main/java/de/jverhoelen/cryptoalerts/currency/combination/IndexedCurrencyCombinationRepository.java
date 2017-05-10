package de.jverhoelen.cryptoalerts.currency.combination;

import de.jverhoelen.cryptoalerts.datautils.repo.Repository;
import org.springframework.stereotype.Component;

@Component
public interface IndexedCurrencyCombinationRepository extends Repository<IndexedCurrencyCombination, Long> {
}
