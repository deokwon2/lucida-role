package com.nkia.lucida.role.service;

import org.json.simple.JSONObject;

/**
 * @author henoh@nkia.co.kr on 2022-05-11
 * @desc
 */
public interface JwtTokenService {

  JSONObject getClaims(String bearerToken);

  String getUserIdFromBearerToken(String bearerToken);

  String getTenantIdFromBearerToken(String bearerToken);

  String validateTokenKey(String bearerToken, String key);
}
