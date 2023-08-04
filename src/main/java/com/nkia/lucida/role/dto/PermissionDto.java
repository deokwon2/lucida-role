package com.nkia.lucida.role.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nkia.lucida.role.domain.PermissionTypes;
import com.nkia.lucida.role.domain.SimpleAction;
import com.nkia.lucida.role.entity.Permission;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

/**
 * @author henoh@nkia.co.kr on 2022-06-16
 * @desc
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties
public class PermissionDto {

  String id;

  String name;

  String description;

  String displayName;

  SimpleAction action; //읽기,쓰기

  PermissionTypes type;

  boolean restricted = false;

  long cTime = System.currentTimeMillis();

  boolean isActive = true;

  public Permission toDomain(Permission source) {
    BeanUtils.copyProperties(this, source);
    return source;
  }
}
