package de.jverhoelen.cryptoalerts;

import com.google.common.collect.ImmutableMap;
import de.jverhoelen.cryptoalerts.currency.CryptoCurrency;
import de.jverhoelen.cryptoalerts.currency.ExchangeCurrency;
import de.jverhoelen.cryptoalerts.currency.combination.IndexedCurrencyCombination;
import de.jverhoelen.cryptoalerts.currency.combination.IndexedCurrencyCombinationService;
import de.jverhoelen.cryptoalerts.sentiment.SentimentTerm;
import de.jverhoelen.cryptoalerts.sentiment.SentimentTermKind;
import de.jverhoelen.cryptoalerts.sentiment.SentimentTermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class PoloniexTickerConsumerApplication extends AsyncConfigurerSupport {

    @Autowired
    private IndexedCurrencyCombinationService indexedCurrencyCombinations;

    @Autowired
    private SentimentTermService sentimentTerms;

    @Value("${executor.queue.capacity}")
    private int queueCapacity;

    @Value("${executor.pool.size.core}")
    private int corePoolSize;

    @Value("${executor.pool.size.max}")
    private int maxPoolSize;

    public static void main(String[] args) {
        SpringApplication.run(PoloniexTickerConsumerApplication.class, args);
    }

    @PostConstruct
    public void insertIngestedCurrencyCombinations() {
        if (indexedCurrencyCombinations.isEmpty()) {
            indexedCurrencyCombinations.add(Arrays.asList(
                    IndexedCurrencyCombination.of(CryptoCurrency.XRP, ExchangeCurrency.BTC),
                    IndexedCurrencyCombination.of(CryptoCurrency.ETH, ExchangeCurrency.BTC),
                    IndexedCurrencyCombination.of(CryptoCurrency.LTC, ExchangeCurrency.BTC)
            ));
        }

        try {
            initSentimentTerms();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initSentimentTerms() throws IOException {
        Map<SentimentTermKind, String> sentimentKindFileNames = ImmutableMap.<SentimentTermKind, String>builder()
                .put(SentimentTermKind.POSITIVE, "positives.txt")
                .put(SentimentTermKind.NEGATIVE, "negatives.txt")
                .build();

        sentimentKindFileNames.entrySet().stream().forEach(sk -> {
            try {
                File file = new ClassPathResource(sk.getValue()).getFile();
                Stream<String> lines = Files.lines(file.toPath());

                lines.forEach(line -> sentimentTerms.add(new SentimentTerm(sk.getKey(), line)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setQueueCapacity(queueCapacity);
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();

        return executor;
    }
}
