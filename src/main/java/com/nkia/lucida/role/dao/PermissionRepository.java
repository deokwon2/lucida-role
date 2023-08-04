package com.nkia.lucida.role.dao;

import com.nkia.lucida.common.mongodb.MongoDBSharedCollection;
import com.nkia.lucida.role.entity.Permission;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author henoh@nkia.co.kr on 2022-06-13
 * @desc
 */
@MongoDBSharedCollection
public interface PermissionRepository extends MongoRepository<Permission, String> {

  Permission findByName(String name);

  List<Permission> findAllByIdIn(List<String> ids);
}
