package com.nkia.lucida.role.domain;

/**
 * @author henoh@nkia.co.kr on 2022-06-10
 * @desc
 */
public enum RoleTypes {
  ADMIN, STANDARD, CUSTOM;

  public static RoleTypes fromValue(String value) {
    if (value == null) {
      return null;
    }

    for (RoleTypes roleType : values()) {
      if (roleType.name().equals(value)) {
        return roleType;
      }
    }

    throw new IllegalArgumentException("Invalid role type: " + value);
  }
}
