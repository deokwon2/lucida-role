package com.nkia.lucida.role.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nkia.lucida.role.domain.RoleConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * @author henoh@nkia.co.kr on 2022-05-16
 * @desc
 */
@DisplayName("JWT Token 검증 테스트")
class JwtTokenServiceImplTest {

  private JwtTokenService jwtTokenService;

  @BeforeEach
  void setUp() {
    jwtTokenService = new JwtTokenServiceImpl();
  }

  @DisplayName("토큰의 payload 부분이 제대로 파싱이 되는지 확인")
  @ParameterizedTest
  @ValueSource(strings = {
      "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJvcmdhbml6YXRpb25JZCI6ImFzbzM5MjQwMWtjbnNsZyIsInVzZXJJZCI6Imhlbm9oQG5raWEuY28ua3IifQ.lSC1hCBkAbURJdQOxbtZ2PMqhaNeAKK9uIvD1dyGaIY"})
  void getClaims(String value) {
    assertTrue(jwtTokenService.getClaims(value).containsKey(RoleConstant.ORGANIZATION_ID));
  }

  @DisplayName("토큰 bearer 파싱 제대로 되는지 확인")
  @ParameterizedTest
  @ValueSource(strings = {
      "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJvcmdhbml6YXRpb25JZCI6ImFzbzM5MjQwMWtjbnNsZyIsInVzZXJJZCI6Imhlbm9oQG5raWEuY28ua3IifQ.lSC1hCBkAbURJdQOxbtZ2PMqhaNeAKK9uIvD1dyGaIY"})
  void validatingParsingBearer(String value) {
    assertEquals(
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJvcmdhbml6YXRpb25JZCI6ImFzbzM5MjQwMWtjbnNsZyIsInVzZXJJZCI6Imhlbm9oQG5raWEuY28ua3IifQ.lSC1hCBkAbURJdQOxbtZ2PMqhaNeAKK9uIvD1dyGaIY",
        value.substring(RoleConstant.HEADER_PREFIX.length()));
  }

  @DisplayName("빈 값, 빈 배열인 경우에 null 확인")
  @ParameterizedTest
  @ValueSource(strings = {"", " "})
  @NullAndEmptySource
  void isNullOrEmptyAllowed(String value) {
    assertNull(jwtTokenService.getClaims(value));
  }

  @DisplayName("토큰 키 값이 일치하지 않을 때 에러가 나는지 확인")
  @ParameterizedTest
  @ValueSource(strings = {RoleConstant.USER_ID, RoleConstant.ORGANIZATION_ID})
  void validateJsonKey(String value) {
    String invalidToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0ZW5hbnRJZCI6ImFzbzM5MjQwMWtjbnNsZyIsInVzZXJJZHMiOiJoZW5vaEBua2lhLmNvLmtyIn0.oSz7EeruEJj7M02TcNo07jp2uTYpaSLuEvaPvJgtzdM";
    assertNull(jwtTokenService.validateTokenKey(invalidToken, value));
  }

  @DisplayName("토큰 payload가 null일 때 확인")
  @ParameterizedTest
  @ValueSource(strings = {RoleConstant.USER_ID, RoleConstant.ORGANIZATION_ID})
  void validateJsonNoPayload(String value) {
    String invalidToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.ogjrSBVAUVEszlqSzNwO1121wrmf2eTPR15kjYHgjf4";
    assertNull(jwtTokenService.validateTokenKey(invalidToken, value));
  }

  @DisplayName("UserId를 제대로 가져 오는지 확인")
  @ParameterizedTest
  @CsvSource(value = {
      "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0ZW5hbnRJZCI6ImFzbzM5MjQwMWtjbnNsZyIsInVzZXJJZCI6Im1pa2UifQ.FmkByiA3JcOR0ZPZW4S70ncMyqeDe2mgVOOAI6cD4Gs:mike"}, delimiter = ':')
  void getUserIdFromBearerToken(String token, String name) {
    assertEquals(name, jwtTokenService.getUserIdFromBearerToken(token));
  }


  @DisplayName("TenantId를 제대로 가져 오는지 확인")
  @ParameterizedTest
  @CsvSource(value = {
      "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJvcmdhbml6YXRpb25JZCI6InRlc3QiLCJ1c2VySWQiOiJoZW5vaEBua2lhLmNvLmtyIn0.7TI_3bDiCwh3znf6jgHm6BXxsxC5OXuTNaMZCyiIrtw:test"}, delimiter = ':')
  void getTenantIdFromBearerToken(String token, String tenantId) {
    assertEquals(tenantId, jwtTokenService.getTenantIdFromBearerToken(token));
  }
}
