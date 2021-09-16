package org.daeun.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Receiver {

    private KafkaConsumer<String, String> consumer;

    public Receiver(String group, String topic) {
        this.consumer = new KafkaConsumer<>(receiverProps(group));
        //Topic 구독
        consumer.subscribe(Collections.singletonList(topic));
    }

    public void close() {
        consumer.close();
    }

    public void poll() {
        //0.1초 대기 후 서버에서 메시지를 읽어온다.
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
        //메시지 출력
        for (ConsumerRecord<String, String> record : records)
            System.out.printf("%s, groupId = %s, offset = %d, partitionNumber = %d, key = %s, value = %s%n",
                    LocalDateTime.now(),
                    consumer.groupMetadata().groupId(),
                    record.offset(),
                    record.partition(),
                    record.key(),
                    record.value());
    }

    public Map<String, Object> receiverProps(String group) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.28:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        return props;
    }
}
