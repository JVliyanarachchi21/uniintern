package com.uniintern.portal.company.dto;

import java.time.LocalDate;

public class InternshipListingDto {
    private Long id;
    private String title;
    private String companyName;
    private String logoPath;
    private String location;
    private String duration;
    private String descriptionSnippet;
    private String postedAgo;

    public InternshipListingDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescriptionSnippet() {
        return descriptionSnippet;
    }

    public void setDescriptionSnippet(String descriptionSnippet) {
        this.descriptionSnippet = descriptionSnippet;
    }

    public String getPostedAgo() {
        return postedAgo;
    }

    public void setPostedAgo(String postedAgo) {
        this.postedAgo = postedAgo;
    }
}
