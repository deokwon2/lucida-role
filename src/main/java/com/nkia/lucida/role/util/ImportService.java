package com.nkia.lucida.role.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nkia.lucida.role.dao.PermissionRepository;
import com.nkia.lucida.role.dto.PermissionDto;
import com.nkia.lucida.role.entity.Permission;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author henoh@nkia.co.kr on 2022-06-14
 * @desc
 */
@Service
@Slf4j
public class ImportService {

  private static final List<Permission> permissionSavedIds = new ArrayList<>();
  @Autowired
  PermissionRepository permissionRepository;

  public static List<Permission> getPermissionSavedIds() {
    return permissionSavedIds;
  }

  public void importTo(List<String> jsonLines) {
    generateMongoDocs(jsonLines);
  }

  private void generateMongoDocs(List<String> lines) {
    ObjectMapper mapper = new ObjectMapper();

    for (String json : lines) {
      try {
        PermissionDto createDto = mapper.readValue(json, PermissionDto.class);
        Permission old = permissionRepository.findByName(createDto.getName());
        if (old != null) {
          createDto.setId(old.getId());
          createDto.setCTime(old.getCTime());
          if (createDto.equals(old.toDto())) {
            permissionSavedIds.add(old);
            continue;
          }
        } else {
          old = new Permission();
        }
        old = createDto.toDomain(old);
        permissionSavedIds.add(permissionRepository.save(old));
      } catch (JsonProcessingException e) {
        throw new IllegalStateException(e);
      }

    }
    log.info("permissionSaveIds = {}", permissionSavedIds);
  }
}
