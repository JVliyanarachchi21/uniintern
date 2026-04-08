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
import java.util.Arrays;
import java.util.List;

@Service
public class AdminSchedulingService {

    private final StudentApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final InternshipRepository internshipRepository;
    private final com.uniintern.portal.company.repository.InterviewRepository interviewRepository;
    private final AdminMatchingService matchingService;

    public AdminSchedulingService(StudentApplicationRepository applicationRepository,
                                  StudentRepository studentRepository,
                                  InternshipRepository internshipRepository,
                                  com.uniintern.portal.company.repository.InterviewRepository interviewRepository,
                                  AdminMatchingService matchingService) {
        this.applicationRepository = applicationRepository;
        this.studentRepository = studentRepository;
        this.internshipRepository = internshipRepository;
        this.interviewRepository = interviewRepository;
        this.matchingService = matchingService;
    }

    public List<PendingInterviewDto> getPendingInterviews() {
        // Fetch statuses that are in the recruitment pipeline but not yet accepted/declined
        List<com.uniintern.portal.student.model.ApplicationStatus> pipelineStatuses = Arrays.asList(
            com.uniintern.portal.student.model.ApplicationStatus.SHORTLISTED,
            com.uniintern.portal.student.model.ApplicationStatus.INTERVIEW_SCHEDULED,
            com.uniintern.portal.student.model.ApplicationStatus.TECHNICAL_ROUND,
            com.uniintern.portal.student.model.ApplicationStatus.HR_ROUND
        );

        List<StudentApplication> pipelineApps = applicationRepository.findAll().stream()
                .filter(a -> pipelineStatuses.contains(a.getStatus()))
                .toList();
        
        List<PendingInterviewDto> pendingList = new ArrayList<>();

        for (StudentApplication app : pipelineApps) {
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
                dto.setStatus(app.getStatus().toString());
                
                // Calculate Smart Match Score
                dto.setMatchScore(matchingService.calculateMatchScore(student, internship));
                
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
