package com.nkia.lucida.role.config;

import com.nkia.lucida.common.mongodb.TenantAsyncConfigurerSupport;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author henoh@nkia.co.kr on 2022-05-11
 * @desc
 */
@Configuration
public class TAsyncConfig extends TenantAsyncConfigurerSupport {

  @Override
  public ThreadPoolTaskExecutor enableTenantAwareTaskDecorator() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(7);
    executor.setMaxPoolSize(42);
    executor.setQueueCapacity(11);
    executor.setThreadNamePrefix("TenantAwareTaskExecutor-");
    return executor;
  }

  @Override
  public void afterPropertiesSet() {
    //Nothing to do..
  }
}
