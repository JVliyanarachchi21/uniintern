package com.uniintern.portal.company.repository;

import com.uniintern.portal.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByEmail(String email);
    Optional<Company> findByEmail(String email);
    List<Company> findByStatus(String status);
}
