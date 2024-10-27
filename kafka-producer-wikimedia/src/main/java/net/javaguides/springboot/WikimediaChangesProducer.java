package net.javaguides.springboot;

import com.launchdarkly.eventsource.ConnectStrategy;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.background.BackgroundEventHandler;
import com.launchdarkly.eventsource.background.BackgroundEventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@Service
public class WikimediaChangesProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikimediaChangesProducer.class);

    private KafkaTemplate<String,String> kafkaTemplate;

    public WikimediaChangesProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(){
        String topic ="wikimedia_recentchange";

        // to read real time stream data from wikimedia , we use event source

        BackgroundEventHandler eventHandler = new WikimediaChangesHandler(kafkaTemplate,topic);

        String url = "https://stream.wikimedia.org/v2/stream/recentchange";

        BackgroundEventSource eventSource = new BackgroundEventSource.Builder(eventHandler,
                new EventSource.Builder(ConnectStrategy.http(URI.create(url))))
                .build();

        eventSource.start();

        try {
            TimeUnit.MINUTES.sleep(10);
        }catch (InterruptedException e){
            LOGGER.error("Error Occurred ::",e);
        }

    }
}
