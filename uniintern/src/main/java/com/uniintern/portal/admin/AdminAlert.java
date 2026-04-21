package com.uniintern.portal.admin;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_alerts")
public class AdminAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // REGISTRATION, INTERNSHIP, SECURITY, SYSTEM

    @Column(nullable = false)
    private String priority; // HIGH, MEDIUM, LOW

    @Column(nullable = false, length = 1000)
    private String message;

    private String targetUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private boolean isRead = false;

    public AdminAlert() {}

    public AdminAlert(String type, String priority, String message, String targetUrl) {
        this.type = type;
        this.priority = priority;
        this.message = message;
        this.targetUrl = targetUrl;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    public Long getId() { return id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
