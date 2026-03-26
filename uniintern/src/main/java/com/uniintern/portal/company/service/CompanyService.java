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

    public CompanyService(CompanyRepository companyRepository, EmailService emailService) {
        this.companyRepository = companyRepository;
        this.emailService = emailService;
    }

    public void registerCompany(CompanyRegistrationDto dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (companyRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
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
        
        // OTP matches, mark as verified
        company.setEmailVerified(true);
        company.setVerificationCode(null);
        company.setVerificationCodeExpiresAt(null);
        company.setStatus("ACTIVE");
        companyRepository.save(company);
        
        return true;
    }
}
