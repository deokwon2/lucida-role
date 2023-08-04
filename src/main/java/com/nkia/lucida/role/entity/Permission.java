package com.nkia.lucida.role.entity;

import com.nkia.lucida.common.mongodb.MongoDBSharedCollection;
import com.nkia.lucida.role.domain.PermissionTypes;
import com.nkia.lucida.role.domain.SimpleAction;
import com.nkia.lucida.role.dto.PermissionDto;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author henoh@nkia.co.kr on 2022-06-10
 * @desc
 */
@Document(collection = "permission")
@NoArgsConstructor
@Data
@MongoDBSharedCollection
public class Permission {

  @Id
  @Getter
  String id;

  String name;

  String description;

  String displayName;

  String action; //읽기,쓰기

  String type;

  boolean restricted = false;

  long cTime = System.currentTimeMillis();

  @Transient
  public SimpleAction getAction() {
    return SimpleAction.fromValue(action);
  }

  public void setAction(SimpleAction action) {
    if (action != null) {
      this.action = action.name();
    }
  }

  @Transient
  public PermissionTypes getType() {
    return PermissionTypes.fromValue(type);
  }

  public void setType(PermissionTypes type) {
    if (type != null) {
      this.type = type.name();
    }
  }

  public PermissionDto toDto() {
    PermissionDto dto = new PermissionDto();
    BeanUtils.copyProperties(this, dto);
    dto.setId(this.getId());
    return dto;
  }

}
