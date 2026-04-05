package com.uniintern.portal.company.service;

import com.uniintern.portal.company.dto.CompanyRegistrationDto;
import com.uniintern.portal.company.entity.Company;
import com.uniintern.portal.company.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class CompanyService {
    
    private final CompanyRepository companyRepository;
    private final EmailService emailService;

    public CompanyService(CompanyRepository companyRepository, @org.springframework.beans.factory.annotation.Qualifier("companyEmailService") EmailService emailService) {
        this.companyRepository = companyRepository;
        this.emailService = emailService;
    }

    public Company findById(Long id) {
        return companyRepository.findById(id).orElse(null);
    }

    public Company findByEmail(String email) {
        return companyRepository.findByEmail(email).orElse(null);
    }

    public java.util.List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Company getOrCreateMockCompany() {
        return companyRepository.findAll().stream().findFirst().orElseGet(() -> {
            Company dummy = new Company();
            dummy.setCompanyName("TechCorp Lanka");
            dummy.setEmail("hr@techcorp.lk");
            dummy.setIndustry("Information Technology");
            dummy.setPhone("+94 77 123 4567");
            dummy.setWebsite("www.techcorp.lk");
            dummy.setAddress("Colombo, Sri Lanka");
            dummy.setDescription("TechCorp Lanka is a growing technology company focused on software engineering, innovation, and digital transformation. We provide internship opportunities for students to gain practical industry experience.");
            dummy.setLogoPath(null);
            dummy.setStatus("ACTIVE");
            return companyRepository.save(dummy);
        });
    }

    public void updateProfile(Long id, String companyName, String industry, String email, String phone, String website, String address, String description, String logoPath) {
        Company company = findById(id);
        if (company == null) {
            throw new IllegalArgumentException("Company not found");
        }
        company.setCompanyName(companyName);
        company.setIndustry(industry);
        company.setEmail(email);
        company.setPhone(phone);
        company.setWebsite(website);
        company.setAddress(address);
        company.setDescription(description);
        
        if (logoPath != null) {
            company.setLogoPath(logoPath);
        }
        
        companyRepository.save(company);
    }

    public void registerCompany(CompanyRegistrationDto dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (companyRepository.existsByEmail(dto.getEmail())) {
            Company existing = companyRepository.findByEmail(dto.getEmail()).orElse(null);
            if (existing != null && !"APPROVED".equals(existing.getStatus()) && !"ACTIVE".equals(existing.getStatus())) {
                companyRepository.delete(existing);
                companyRepository.flush();
            } else {
                throw new IllegalArgumentException("Email already registered and approved. Please log in.");
            }
        }

        Company company = new Company();
        company.setCompanyName(dto.getCompanyName());
        company.setEmail(dto.getEmail());
        company.setIndustry(dto.getIndustry());
        company.setDescription(dto.getDescription());
        company.setPassword(dto.getPassword());
        company.setStatus("PENDING_VERIFICATION");
        company.setCreatedAt(LocalDateTime.now());
        
        // Generate OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        company.setVerificationCode(otp);
        company.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        company.setEmailVerified(false);

        companyRepository.save(company);
        
        // Send OTP via email
        emailService.sendVerificationOtp(company.getEmail(), otp);
    }
    
    public boolean verifyOtp(String email, String otp) {
        Company company = companyRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Company not found for email: " + email));
                
        if (company.isEmailVerified()) {
            throw new IllegalArgumentException("Email is already verified");
        }
        
        if (company.getVerificationCodeExpiresAt() == null || company.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP has expired. Please request a new one.");
        }
        
        if (!otp.equals(company.getVerificationCode())) {
            throw new IllegalArgumentException("Invalid OTP");
        }
        
        // OTP matches, mark as verified and pending approval from admin
        company.setEmailVerified(true);
        company.setVerificationCode(null);
        company.setVerificationCodeExpiresAt(null);
        company.setStatus("PENDING_APPROVAL");
        companyRepository.save(company);
        
        return true;
    }
}
