package com.uniintern.portal.company.service;

import com.uniintern.portal.company.entity.Internship;
import com.uniintern.portal.company.repository.InternshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InternshipService {

    @Autowired
    private InternshipRepository internshipRepository;

    // Save internship
    public Internship save(Internship internship) {
        return internshipRepository.save(internship);
    }

    // Get internships by company
    public List<Internship> getByCompanyId(Long companyId) {
        return internshipRepository.findByCompanyId(companyId);
    }

    // Get all internships
    public List<Internship> getAll() {
        return internshipRepository.findAll();
    }
}