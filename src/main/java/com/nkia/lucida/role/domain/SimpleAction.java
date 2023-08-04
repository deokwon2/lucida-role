package com.nkia.lucida.role.domain;

import lombok.Getter;


/**
 * @author henoh@nkia.co.kr on 2022-06-13
 * @desc
 */
public enum SimpleAction {
  NONE(null),
  READ("read"),
  WRITE("read,write"),
  EXECUTE("read,write,execute");

  @Getter
  private final String action;

  SimpleAction(String action) {
    this.action = action;
  }

  static boolean isCorrectAction(SimpleAction action, String actionExpected) {
    return action.getAction().contains(actionExpected.toLowerCase());
  }

  public static SimpleAction fromValue(String value) {
    if (value != null) {
      for (SimpleAction action : values()) {
        if (action.name().equals(value)) {
          return action;
        }
      }
    }

    throw new IllegalArgumentException("Invalid action: " + value);
  }
}
