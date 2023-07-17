package com.postgresjdbc.assiHim.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

public class Producer {


        @Autowired
        private KafkaTemplate<String, Object> kafkaTemplate;

        public void push(String topic, Object value) {
            kafkaTemplate.send(topic, value);
        }

}
