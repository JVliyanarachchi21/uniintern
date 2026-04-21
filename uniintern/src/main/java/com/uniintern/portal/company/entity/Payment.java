package com.uniintern.portal.company.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "company_payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
