package com.uniintern.portal.company.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "company_applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
