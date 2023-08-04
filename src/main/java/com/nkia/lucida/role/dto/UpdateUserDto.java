package com.nkia.lucida.role.dto;

import lombok.Data;

import java.util.Set;

/**
 * @author henoh@nkia.co.kr on 2022-08-03
 * @desc
 */
@Data
public class UpdateUserDto {

  Set<String> removeRoleIds;
  Set<String> addRoleIds;
}
