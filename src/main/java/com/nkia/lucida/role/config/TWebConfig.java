package com.nkia.lucida.role.config;

import com.nkia.lucida.common.mongodb.TenantWebConfigurerSupport;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;


/**
 * @author henoh@nkia.co.kr on 2022-05-11
 * @desc
 */
@Configuration
public class TWebConfig extends TenantWebConfigurerSupport {

  @Autowired
  TWebInterceptor tWebInterceptor;

  @Override
  public String getRequestHeaderName() {
    return null;
  }

  @Override
  public void afterPropertiesSet() {
    //nothing to do..
  }

  @Override
  public void onStartup(ServletContext servletContext) {
    //nothing to do..
  }

  private static final String[] EXCLUDE_PATHS = {
      "/error",
      "/role/swagger**/**",
      "/role/v3/**",
      "/v3/**",
      "/webjars/**",
      "/doc.html",
      "/api/noauth/**"
  };


  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(tWebInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns(EXCLUDE_PATHS);
  }
}
