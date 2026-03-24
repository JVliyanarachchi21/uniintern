package com.uniintern.portal.company;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findByStatus(CompanyStatus status);
}
