package com.nkia.lucida.role.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author henoh@nkia.co.kr on 2022-06-22
 * @desc
 */
class EnumTest {

  @Test
  @DisplayName("적합한 value가 아닐경우 에러 일치하는지 확인")
  void fromValue() {
    assertThrows(IllegalArgumentException.class, () -> PermissionTypes.fromValue("test")
        , "Invalid type: test");
    assertThrows(IllegalArgumentException.class, () -> SimpleAction.fromValue("test"),
        "Invalid action: test");
    assertThrows(IllegalArgumentException.class, () -> RoleTypes.fromValue("test"),
        "Invalid role type: test");
  }

  @Test
  void isCorrectAction() {
    assertTrue(SimpleAction.isCorrectAction(SimpleAction.EXECUTE, SimpleAction.READ.name()));
  }

  @Test
  void isNotCorrectAction() {
    assertFalse(SimpleAction.isCorrectAction(SimpleAction.READ, SimpleAction.WRITE.name()));
  }

  @Test
  @DisplayName("함수 call 시 parameter가 null일때")
  void check_throw_exception() {
    assertThrows(IllegalArgumentException.class, () -> SimpleAction.fromValue(null));
    assertThrows(IllegalArgumentException.class, () -> PermissionTypes.fromValue(null));
  }


}