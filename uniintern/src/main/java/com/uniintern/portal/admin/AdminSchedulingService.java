package com.uniintern.portal.admin;

import com.uniintern.portal.company.entity.Internship;
import com.uniintern.portal.company.repository.InternshipRepository;
import com.uniintern.portal.student.model.ApplicationStatus;
import com.uniintern.portal.student.model.Student;
import com.uniintern.portal.student.model.StudentApplication;
import com.uniintern.portal.student.repository.StudentApplicationRepository;
import com.uniintern.portal.student.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminSchedulingService {

    private final StudentApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final InternshipRepository internshipRepository;

    public AdminSchedulingService(StudentApplicationRepository applicationRepository,
                                  StudentRepository studentRepository,
                                  InternshipRepository internshipRepository) {
        this.applicationRepository = applicationRepository;
        this.studentRepository = studentRepository;
        this.internshipRepository = internshipRepository;
    }

    public List<PendingInterviewDto> getPendingInterviews() {
        List<StudentApplication> shortlisted = applicationRepository.findByStatus(ApplicationStatus.SHORTLISTED);
        List<PendingInterviewDto> pendingList = new ArrayList<>();

        for (StudentApplication app : shortlisted) {
            Student student = studentRepository.findById(app.getStudentId()).orElse(null);
            Internship internship = internshipRepository.findById(app.getInternshipId()).orElse(null);

            if (student != null && internship != null) {
                PendingInterviewDto dto = new PendingInterviewDto();
                dto.setApplicationId(app.getId());
                dto.setStudentName(student.getFullName());
                dto.setUniversity(student.getUniversity());
                dto.setInternshipTitle(internship.getTitle());
                dto.setInternshipId(internship.getId());
                dto.setCompanyId(internship.getCompanyId());
                pendingList.add(dto);
            }
        }
        return pendingList;
    }

    public StudentApplication getApplicationById(Long id) {
        return applicationRepository.findById(id).orElse(null);
    }

    public void saveApplication(StudentApplication app) {
        applicationRepository.save(app);
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }
}
