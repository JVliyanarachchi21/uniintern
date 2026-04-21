package com.uniintern.portal.company.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class CompanyMailConfig {

    @Value("${company.mail.host}")
    private String host;

    @Value("${company.mail.port}")
    private int port;

    @Value("${company.mail.username}")
    private String username;

    @Value("${company.mail.password}")
    private String password;

    @Value("${company.mail.properties.mail.smtp.auth}")
    private String auth;

    @Value("${company.mail.properties.mail.smtp.starttls.enable}")
    private String starttls;

    // Student settings
    @Value("${spring.mail.host}")
    private String studentHost;

    @Value("${spring.mail.port}")
    private int studentPort;

    @Value("${spring.mail.username}")
    private String studentUsername;

    @Value("${spring.mail.password}")
    private String studentPassword;

    @Bean(name = "companyMailSender")
    public JavaMailSender companyMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttls);
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Bean(name = "studentMailSender")
    @org.springframework.context.annotation.Primary
    public JavaMailSender studentMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(studentHost);
        mailSender.setPort(studentPort);
        mailSender.setUsername(studentUsername);
        mailSender.setPassword(studentPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        
        return mailSender;
    }
}
