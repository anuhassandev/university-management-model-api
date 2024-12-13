package com.school.management.repo;

import org.springframework.data.repository.CrudRepository;

import com.school.management.domain.Module;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends CrudRepository<Module, String> {

}
