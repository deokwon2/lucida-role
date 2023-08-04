package com.nkia.lucida.role.domain;

/**
 * @author henoh@nkia.co.kr on 2022-05-11
 * @desc
 */
public class RoleConstant {

  private RoleConstant() {
    throw new IllegalStateException("Domain Class");
  }

  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String HEADER_PREFIX = "Bearer ";
  public static final String USER_ID = "userId";
  public static final String ORGANIZATION_ID = "organizationId";
  //kafka
  public static final String MAX_REQUEST_SIZE = "5242880";
  public static final String TOPIC_ORGANIZATION_CREATE = "common.organization.create";
  public static final String TOPIC_ACCOUNT = "common.organization.user";

  public static final String UNAUTHORIZED = "Unauthorized Token";

  //permission
  public static final String ADMIN_ROLE = "Admin Role";
  public static final String STANDARD_ROLE = "Standard Role";
}
