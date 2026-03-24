package com.uniintern.portal.admin;

import com.uniintern.portal.company.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AdminSeedData implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeedData.class);

    private final CompanyRepository companyRepository;
    private final InternshipRepository internshipRepository;

    public AdminSeedData(CompanyRepository companyRepository, InternshipRepository internshipRepository) {
        this.companyRepository = companyRepository;
        this.internshipRepository = internshipRepository;
    }

    @Override
    public void run(String... args) {
        try {
            if (companyRepository.count() == 0) {
                Company c1 = new Company();
                c1.setCompanyName("TechVentures Inc.");
                c1.setEmail("hr@techventures.com");
                c1.setIndustry("Software");
                c1.setStatus(CompanyStatus.PENDING_VERIFICATION);
                companyRepository.save(c1);

                Company c2 = new Company();
                c2.setCompanyName("DataStream Analytics");
                c2.setEmail("contact@datastream.com");
                c2.setIndustry("Data Science");
                c2.setStatus(CompanyStatus.PENDING_VERIFICATION);
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
                i1.setStatus(InternshipStatus.PENDING_ADMIN_APPROVAL);

                Internship i2 = new Internship();
                i2.setCompanyId(2L);
                i2.setTitle("Data Analyst Intern");
                i2.setDescription("Support reporting and analytics workflows.");
                i2.setRequiredSkills("Python, SQL, Excel");
                i2.setMinGpa(2.8);
                i2.setLocation("Remote");
                i2.setDuration("3 months");
                i2.setDeadline(LocalDate.now().plusDays(15));
                i2.setStatus(InternshipStatus.PENDING_ADMIN_APPROVAL);

                internshipRepository.save(i1);
                internshipRepository.save(i2);
            }

            log.info("Seed data check completed.");
        } catch (Exception e) {
            log.warn("Skipping seed data. Reason: {}", e.getMessage());
        }
    }
}