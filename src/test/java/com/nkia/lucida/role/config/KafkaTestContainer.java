package com.nkia.lucida.role.config;

import com.nkia.lucida.role.RoleApplication;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;


/**
 * @author henoh@nkia.co.kr on 2022-06-21
 * @desc
 */
@ActiveProfiles("test")
@TestConfiguration
@ContextConfiguration(classes = RoleApplication.class)
public abstract class KafkaTestContainer {

  private static final String DOCKER_IMAGE = "confluentinc/cp-kafka:6.2.1";

  @Container
  public static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(
      DockerImageName.parse(DOCKER_IMAGE))
      .withEmbeddedZookeeper().withReuse(true);

  static {
    KAFKA_CONTAINER.start();
    System.setProperty("spring.kafka.bootstrap-servers=", KAFKA_CONTAINER.getBootstrapServers());
  }

  @PreDestroy
  private void stop() {
    KAFKA_CONTAINER.stop();
  }
}
