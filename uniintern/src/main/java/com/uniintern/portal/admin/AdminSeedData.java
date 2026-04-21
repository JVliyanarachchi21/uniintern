package com.uniintern.portal.admin;

import com.uniintern.portal.company.entity.Company;
import com.uniintern.portal.company.repository.CompanyRepository;
import com.uniintern.portal.company.entity.Internship;
import com.uniintern.portal.company.repository.InternshipRepository;
import com.uniintern.portal.company.entity.Interview;
import com.uniintern.portal.company.repository.InterviewRepository;
import com.uniintern.portal.student.model.Student;
import com.uniintern.portal.student.model.StudentStatus;
import com.uniintern.portal.student.repository.StudentRepository;
import com.uniintern.portal.student.model.StudentApplication;
import com.uniintern.portal.student.model.ApplicationStatus;
import com.uniintern.portal.student.repository.StudentApplicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class AdminSeedData implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeedData.class);

    private final CompanyRepository companyRepository;
    private final InternshipRepository internshipRepository;
    private final InterviewRepository interviewRepository;
    private final StudentRepository studentRepository;
    private final StudentApplicationRepository applicationRepository;
    private final AdminAccountRepository adminAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeedData(CompanyRepository companyRepository, 
                        InternshipRepository internshipRepository,
                        InterviewRepository interviewRepository,
                        StudentRepository studentRepository,
                        StudentApplicationRepository applicationRepository,
                        AdminAccountRepository adminAccountRepository,
                        PasswordEncoder passwordEncoder) {
        this.companyRepository = companyRepository;
        this.internshipRepository = internshipRepository;
        this.interviewRepository = interviewRepository;
        this.studentRepository = studentRepository;
        this.applicationRepository = applicationRepository;
        this.adminAccountRepository = adminAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        try {
            // Seed permanent Admin Account
            if (adminAccountRepository.count() == 0) {
                AdminAccount admin = new AdminAccount();
                admin.setEmail("admin@uniintern.com");
                admin.setPassword(passwordEncoder.encode("UniIntern@Admin2026!"));
                adminAccountRepository.save(admin);
                log.info("Seeded enterprise admin account: admin@uniintern.com");
            }

            if (companyRepository.count() == 0) {
                Company c1 = new Company();
                c1.setCompanyName("TechVentures Inc.");
                c1.setEmail("hr@techventures.com");
                c1.setIndustry("Software");
                c1.setStatus("APPROVED"); // Set to approved for production readiness
                companyRepository.save(c1);

                Company c2 = new Company();
                c2.setCompanyName("DataStream Analytics");
                c2.setEmail("contact@datastream.com");
                c2.setIndustry("Data Science");
                c2.setStatus("APPROVED");
                companyRepository.save(c2);
            }

            if (internshipRepository.count() == 0) {
                Internship i1 = new Internship();
                i1.setCompanyId(1L);
                i1.setTitle("Frontend Developer Intern");
                i1.setDescription("Assist with UI development using modern web technologies.");
                i1.setRequiredSkills("JavaScript, HTML, CSS, React");
                i1.setMinGpa(3.0);
                i1.setLocation("Colombo");
                i1.setDuration("6 months");
                i1.setDeadline(LocalDate.now().plusDays(20));
                i1.setStatus("APPROVED");

                Internship i2 = new Internship();
                i2.setCompanyId(2L);
                i2.setTitle("Data Analyst Intern");
                i2.setDescription("Support reporting and analytics workflows.");
                i2.setRequiredSkills("Python, SQL, Excel");
                i2.setMinGpa(2.8);
                i2.setLocation("Remote");
                i2.setDuration("3 months");
                i2.setDeadline(LocalDate.now().plusDays(15));
                i2.setStatus("APPROVED");

                internshipRepository.save(i1);
                internshipRepository.save(i2);
            }

            // Ensure all seeded internships are APPROVED
            internshipRepository.findAll().forEach(i -> {
                if (!"APPROVED".equals(i.getStatus())) {
                    i.setStatus("APPROVED");
                    internshipRepository.save(i);
                }
            });

            if (studentRepository.count() == 0) {
                Student s = new Student();
                s.setFullName("Test Student");
                s.setEmail("student@my.sliit.lk");
                s.setPassword("Student@123"); // Reverted to plain text for team compatibility
                s.setUniversity("SLIIT");
                s.setDegreeProgram("Information Technology");
                s.setRegistrationNumber("IT23537538");
                s.setNicNumber("20019980873");
                s.setGpa(3.5);
                s.setAcademicYear("3rd Year");
                s.setStatus(StudentStatus.VERIFIED);
                
                s.setLoginAlertsEnabled(false);
                s.setTwoFactorEnabled(false);
                s.setRememberDeviceEnabled(false);
                
                studentRepository.save(s);
            }

            // aggressive seeding for the multi-stage recruitment pipeline
            if (applicationRepository.count() < 5) {
                List<Student> students = studentRepository.findAll();
                List<Internship> internships = internshipRepository.findAll();
                
                if (!students.isEmpty() && !internships.isEmpty()) {
                    // 1. Technical Round Candidate
                    StudentApplication app1 = new StudentApplication();
                    app1.setStudentId(students.get(0).getId());
                    app1.setInternshipId(internships.get(0).getId());
                    app1.setStatus(com.uniintern.portal.student.model.ApplicationStatus.TECHNICAL_ROUND);
                    app1.setScore(85.0);
                    app1.setRemarks("Excellent logic skills. Advancing to technical round.");
                    applicationRepository.save(app1);

                    // 2. HR Round Candidate
                    StudentApplication app2 = new StudentApplication();
                    app2.setStudentId(students.get(0).getId());
                    app2.setInternshipId(internships.size() > 1 ? internships.get(1).getId() : internships.get(0).getId());
                    app2.setStatus(com.uniintern.portal.student.model.ApplicationStatus.HR_ROUND);
                    app2.setScore(92.0);
                    app2.setRemarks("Culture fit confirmed. Proceeding to final HR interview.");
                    applicationRepository.save(app2);

                    // 3. Offer Extended Candidate
                    StudentApplication app3 = new StudentApplication();
                    app3.setStudentId(students.get(0).getId());
                    app3.setInternshipId(internships.get(0).getId());
                    app3.setStatus(com.uniintern.portal.student.model.ApplicationStatus.OFFER_EXTENDED);
                    app3.setScore(98.0);
                    app3.setRemarks("Top-tier talent. Official offer letter dispatched.");
                    applicationRepository.save(app3);
                }
            }

            // Seed a real match for the User Viva Demo
            if (interviewRepository.count() == 0) {
                List<Student> students = studentRepository.findAll();
                List<Internship> internships = internshipRepository.findAll();
                if (!students.isEmpty() && !internships.isEmpty()) {
                    Interview vivaInterview = new Interview();
                    vivaInterview.setStudentId(students.get(0).getId()); // Link to test student
                    vivaInterview.setCandidateName(students.get(0).getFullName());
                    vivaInterview.setInternshipTitle(internships.get(0).getTitle());
                    vivaInterview.setInterviewDateTime(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0));
                    vivaInterview.setStatus("SCHEDULED");
                    interviewRepository.save(vivaInterview);
                    log.info("Seeded Viva Synchronization Interview for Test Student.");
                }
            }

            log.info("Production Recruitment Engine initialized with pipeline stages.");
        } catch (Exception e) {
            log.warn("Skipping seed data. Reason: {}", e.getMessage());
        }
    }
}