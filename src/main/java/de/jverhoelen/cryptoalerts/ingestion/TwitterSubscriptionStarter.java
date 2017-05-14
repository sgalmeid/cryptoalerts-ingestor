package de.jverhoelen.cryptoalerts.ingestion;


import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.springframework.stereotype.Service;
import twitter4j.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class TwitterSubscriptionStarter {

//    @PostConstruct
    public void init() throws InterruptedException {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(10000);
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
        // add some track terms
        endpoint.trackTerms(Lists.newArrayList("twitterapi", "#yolo"));

//        Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);
        // Authentication auth = new BasicAuth(username, password);

//        // Create a new BasicClient. By default gzip is enabled.
//        Client client = new ClientBuilder()
//                .hosts(Constants.STREAM_HOST)
//                .endpoint(endpoint)
//                .authentication(auth)
//                .processor(new StringDelimitedProcessor(queue))
//                .build();

        // Establish a connection
//        client.connect();

        // Do whatever needs to be done with messages
        for (int msgRead = 0; msgRead < 1000; msgRead++) {
            String msg = queue.take();
            System.out.println(msg);
        }

//        client.stop();
    }
}
