package com.school.management.repo;

import org.springframework.data.repository.CrudRepository;

import com.school.management.domain.Session;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends CrudRepository<Session, Long> {

}
