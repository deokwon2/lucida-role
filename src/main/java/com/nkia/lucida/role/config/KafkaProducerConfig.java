package com.nkia.lucida.role.config;

import static com.nkia.lucida.role.domain.RoleConstant.MAX_REQUEST_SIZE;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@EnableKafka
@Configuration
public class KafkaProducerConfig<K, V> {

  @Value("${spring.kafka.bootstrap-servers}")
  private String kafkaEndpoint;

  @Value("${spring.kafka.properties.schema.registry.url}")
  private String schemaRegistryUrl;

  @Bean
  public ProducerFactory<K, V> producerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEndpoint);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
    props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
    props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, MAX_REQUEST_SIZE);
    props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<K, V> kafkaTemplate() {
    KafkaTemplate<K, V> kafkaTemplate = new KafkaTemplate<>(producerFactory());
    kafkaTemplate.setObservationEnabled(true);
    return kafkaTemplate;
  }
}
