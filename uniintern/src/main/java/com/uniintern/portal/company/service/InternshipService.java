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

    //GET by id
    public Internship getById(Long id) {
    return internshipRepository.findById(id).orElse(null);
}

    // Delete internship
    public void delete(Long id) {
        internshipRepository.deleteById(id);
    }
}