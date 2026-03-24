package com.uniintern.repo;


import com.uniintern.model.course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface coursereepo extends JpaRepository<course, Integer> {

}
