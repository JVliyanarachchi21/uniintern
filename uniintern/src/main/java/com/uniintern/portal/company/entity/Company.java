package com.uniintern.portal.company.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    
    @Column(unique = true)
    private String email;
    
    private String industry;
    
    @Column(length = 2000)
    private String description;
    
    private String password;
    
    private String phone;
    private String website;
    private String address;
    
    private String logoPath;

    private String status;
    private LocalDateTime createdAt;
    
    private String verificationCode;
    private LocalDateTime verificationCodeExpiresAt;
    
    @Column(name = "is_email_verified", columnDefinition = "boolean default false")
    private Boolean isEmailVerified = false;

    public Company() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }

    public LocalDateTime getVerificationCodeExpiresAt() { return verificationCodeExpiresAt; }
    public void setVerificationCodeExpiresAt(LocalDateTime verificationCodeExpiresAt) { this.verificationCodeExpiresAt = verificationCodeExpiresAt; }

    public boolean isEmailVerified() { return isEmailVerified != null && isEmailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.isEmailVerified = emailVerified; }
}
