package com.nkia.lucida.role.config;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nkia.lucida.role.domain.RoleConstant;
import java.nio.file.AccessDeniedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author henoh@nkia.co.kr on 2022-05-16
 * @desc
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(MongoDBTestContainer.class)
class TWebInterceptorTest {

  @DisplayName("tenantId가 없을 경우 에러 확인")
  @ParameterizedTest
  @ValueSource(strings = {
      "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJKb2huIERvZSIsInJvbGVJZHMiOjE1MTYyMzkwMjJ9.Qjzi2I6ZQRR-aOw4VzVbv9SHQCvEEjQD6YpiiwJSGyw"})
  void preHandle(String value) {
    TWebInterceptor tWebInterceptor = new TWebInterceptor();
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.addHeader(RoleConstant.AUTHORIZATION_HEADER, value);
    assertThrows(AccessDeniedException.class, () ->
            tWebInterceptor.preHandle(mockRequest, new MockHttpServletResponse(), new Object()),
        RoleConstant.UNAUTHORIZED);
  }
}
