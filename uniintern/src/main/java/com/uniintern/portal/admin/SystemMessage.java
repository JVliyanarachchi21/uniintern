package com.uniintern.portal.admin;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_messages")
public class SystemMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // BUG_REPORT, INVITE, ANNOUNCEMENT
    private String subject;
    private String content;
    private String senderEmail;
    private String recipientEmail;
    private LocalDateTime createdAt = LocalDateTime.now();

    public SystemMessage() {}

    public SystemMessage(String type, String subject, String content, String senderEmail) {
        this.type = type;
        this.subject = subject;
        this.content = content;
        this.senderEmail = senderEmail;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }
    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
