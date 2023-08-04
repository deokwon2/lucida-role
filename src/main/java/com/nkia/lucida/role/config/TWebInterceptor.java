package com.nkia.lucida.role.config;

import com.nkia.lucida.common.mongodb.TenantContextHolder;
import com.nkia.lucida.role.domain.RoleConstant;
import com.nkia.lucida.role.service.JwtTokenService;
import com.nkia.lucida.role.service.JwtTokenServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.file.AccessDeniedException;

/**
 * @author henoh@nkia.co.kr on 2022-05-11
 * @desc
 */
@Slf4j
@Component
public class TWebInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    JwtTokenService jwtTokenService = new JwtTokenServiceImpl();
    String tenantId = jwtTokenService.getTenantIdFromBearerToken(
        request.getHeader(RoleConstant.AUTHORIZATION_HEADER));
    if (tenantId == null) {
      tenantId = request.getParameter(RoleConstant.ORGANIZATION_ID);
    }
    if (tenantId != null) {
      TenantContextHolder.INSTANCE.setTenantId(tenantId);
      return true;
    }
    throw new AccessDeniedException(RoleConstant.UNAUTHORIZED);
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) {
    TenantContextHolder.INSTANCE.clear();
  }

}
