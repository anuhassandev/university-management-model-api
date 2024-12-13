package com.school.management.repo;

import org.springframework.data.repository.CrudRepository;

import com.school.management.domain.Convenor;
import org.springframework.stereotype.Repository;

@Repository
public interface ConvenorRepository extends CrudRepository<Convenor, Long> {

}
