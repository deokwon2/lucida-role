package com.nkia.lucida.role.domain;

/**
 * @author henoh@nkia.co.kr on 2022-06-13
 * @desc
 */
public enum PermissionTypes {
  GENERAL, DASHBOARD, SMS;

  public static PermissionTypes fromValue(String value) {
    if (value != null) {
      for (PermissionTypes group : values()) {
        if (group.name().equals(value)) {
          return group;
        }
      }
    }

    throw new IllegalArgumentException("Invalid type: " + value);
  }

}
