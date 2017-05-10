package de.jverhoelen.cryptoalerts.sentiment;

import de.jverhoelen.cryptoalerts.datautils.repo.AbstractRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SentimentTermService extends AbstractRepositoryService<SentimentTerm, String> {

    @Autowired
    private SentimentTermRepository repository;

    public List<String> findByKind(SentimentTermKind kind) {
        return repository.findByKind(kind).stream().map(term -> term.getTerm()).collect(Collectors.toList());
    }
}
