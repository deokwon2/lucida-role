package com.nkia.lucida.role.dao;

import com.nkia.lucida.common.mongodb.MongoDBIsolationCollection;
import com.nkia.lucida.role.entity.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author henoh@nkia.co.kr on 2022-06-13
 * @desc
 */
@MongoDBIsolationCollection
public interface RoleRepository extends MongoRepository<Role, String> {

  List<Role> findAllByUserIdsIsContaining(String userId);

  List<Role> findAllByIdIn(List<String> ids);
}
