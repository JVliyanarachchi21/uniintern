package com.uniintern.portal.admin;

import com.uniintern.portal.company.entity.Internship;
import com.uniintern.portal.company.repository.CompanyRepository;
import com.uniintern.portal.company.repository.InternshipRepository;
import com.uniintern.portal.student.model.ApplicationStatus;
import com.uniintern.portal.student.model.Student;
import com.uniintern.portal.student.model.StudentApplication;
import com.uniintern.portal.student.repository.StudentApplicationRepository;
import com.uniintern.portal.student.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AdminSchedulingService {

    private final StudentApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final InternshipRepository internshipRepository;
    private final CompanyRepository companyRepository;
    private final com.uniintern.portal.company.repository.InterviewRepository interviewRepository;
    private final AdminMatchingService matchingService;

    public AdminSchedulingService(StudentApplicationRepository applicationRepository,
                                  StudentRepository studentRepository,
                                  InternshipRepository internshipRepository,
                                  CompanyRepository companyRepository,
                                  com.uniintern.portal.company.repository.InterviewRepository interviewRepository,
                                  AdminMatchingService matchingService) {
        this.applicationRepository = applicationRepository;
        this.studentRepository = studentRepository;
        this.internshipRepository = internshipRepository;
        this.companyRepository = companyRepository;
        this.interviewRepository = interviewRepository;
        this.matchingService = matchingService;
    }

    public List<PendingInterviewDto> getPendingInterviews() {
        // Fetch statuses that are in the recruitment pipeline
        List<ApplicationStatus> pipelineStatuses = Arrays.asList(
            ApplicationStatus.SHORTLISTED,
            ApplicationStatus.INTERVIEW_SCHEDULED,
            ApplicationStatus.TECHNICAL_ROUND,
            ApplicationStatus.HR_ROUND
        );

        // High-performance fetch: Apps + Students in one query
        List<StudentApplication> pipelineApps = applicationRepository.findByStatusInWithStudent(pipelineStatuses);
        
        List<PendingInterviewDto> pendingList = new ArrayList<>();

        for (StudentApplication app : pipelineApps) {
            Student student = app.getStudent(); // Already loaded via JOIN FETCH
            
            // Still fetch Internship, but minimize database hits if multiple apps per internship
            Internship internship = internshipRepository.findById(app.getInternshipId()).orElse(null);

            if (student != null && internship != null) {
                PendingInterviewDto dto = new PendingInterviewDto();
                dto.setApplicationId(app.getId());
                dto.setStudentName(student.getFullName());
                dto.setUniversity(student.getUniversity());
                dto.setInternshipTitle(internship.getTitle());
                dto.setInternshipId(internship.getId());
                dto.setCompanyId(internship.getCompanyId());
                
                // Fetch company name
                companyRepository.findById(internship.getCompanyId()).ifPresent(c -> dto.setCompanyName(c.getCompanyName()));
                
                dto.setStatus(app.getStatus().toString());
                
                // SYNCHRONIZATION: Use the actual score from the Filtering Module
                // If score is NULL (not filtered yet), fallback to the dynamic matching score
                if (app.getScore() != null) {
                    dto.setMatchScore(app.getScore());
                } else {
                    dto.setMatchScore(matchingService.calculateMatchScore(student, internship));
                }
                
                pendingList.add(dto);
            }
        }
        return pendingList;
    }

    public boolean hasTimeConflict(Long studentId, java.time.LocalDateTime newTime) {
        List<com.uniintern.portal.company.entity.Interview> interviews = interviewRepository.findByStudentId(studentId);
        long bufferMinutes = 30;

        for (com.uniintern.portal.company.entity.Interview existing : interviews) {
            java.time.LocalDateTime start = existing.getInterviewDateTime();
            java.time.LocalDateTime end = start.plusMinutes(60); // Assuming 1hr interviews

            // Check if newTime is within existing interview + buffer
            if (newTime.isAfter(start.minusMinutes(bufferMinutes)) && 
                newTime.isBefore(end.plusMinutes(bufferMinutes))) {
                return true;
            }
        }
        return false;
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
