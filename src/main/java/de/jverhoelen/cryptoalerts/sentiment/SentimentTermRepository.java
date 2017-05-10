package de.jverhoelen.cryptoalerts.sentiment;

import de.jverhoelen.cryptoalerts.datautils.repo.Repository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public interface SentimentTermRepository extends Repository<SentimentTerm, String> {

    List<SentimentTerm> findByKind(SentimentTermKind kind);
}
