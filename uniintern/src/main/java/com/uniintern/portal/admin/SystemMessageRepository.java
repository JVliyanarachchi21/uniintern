package com.uniintern.portal.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SystemMessageRepository extends JpaRepository<SystemMessage, Long> {
    List<SystemMessage> findByType(String type);
}
