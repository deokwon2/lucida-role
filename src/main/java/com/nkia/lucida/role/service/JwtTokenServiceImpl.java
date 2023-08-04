package com.nkia.lucida.role.service;

import com.nkia.lucida.role.domain.RoleConstant;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

/**
 * @author henoh@nkia.co.kr on 2022-05-11
 * @desc
 */
@Slf4j
@Service
public class JwtTokenServiceImpl implements JwtTokenService {

  @Override
  public JSONObject getClaims(String bearerToken) {
    if (!isNullOrEmpty(bearerToken)) {
      try {
        String token = bearerToken.substring(RoleConstant.HEADER_PREFIX.length());

        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] parts = token.split("\\.");
        // claims 부분만 가져온다.
        String payloads = new String(decoder.decode(parts[1]));
        JSONParser parser = new JSONParser();

        return (JSONObject) parser.parse(payloads);
      } catch (ParseException | NullPointerException e) {
        return null;
      }
    }
    return null;
  }

  @Override
  public String getUserIdFromBearerToken(String bearerToken) {
    return validateTokenKey(bearerToken, RoleConstant.USER_ID);
  }

  @Override
  public String getTenantIdFromBearerToken(String bearerToken) {
    return validateTokenKey(bearerToken, RoleConstant.ORGANIZATION_ID);
  }

  private boolean isNullOrEmpty(String value) {
    return value == null || value.isEmpty() || " ".equals(value);
  }

  @Override
  public String validateTokenKey(String bearerToken, String key) {
    JSONObject decodePayload = getClaims(bearerToken);
    if (decodePayload == null || decodePayload.get(key) == null) {
      return null;
    }
    return decodePayload.get(key).toString();
  }
}
