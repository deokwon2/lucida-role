package com.nkia.lucida.role.config;

import com.nkia.lucida.role.RoleApplication;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

/**
 * @author henoh@nkia.co.kr on 2022-06-21
 * @desc
 */
@ActiveProfiles("test")
@TestConfiguration
@ContextConfiguration(classes = RoleApplication.class)
public abstract class MongoDBTestContainer {

  private static final String DOCKER_IMAGE = "mongo:5.0.8";

  @Container
  public static final MongoDBContainer MONGO_DB_CONTAINER = new MongoDBContainer(
      DOCKER_IMAGE).withReuse(true);

  static {
    MONGO_DB_CONTAINER.start();
    System.setProperty("com.nkia.lucida.common.mongodb.uri",
        "mongodb://" + MONGO_DB_CONTAINER.getHost() + ":" + MONGO_DB_CONTAINER.getFirstMappedPort()
            .toString());
  }


  @PreDestroy
  public void stop() {
    MONGO_DB_CONTAINER.stop();
  }
}
