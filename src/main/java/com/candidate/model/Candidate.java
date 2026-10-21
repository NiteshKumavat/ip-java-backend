
package com.candidate.model;

import java.math.BigDecimal;

public class Candidate {

    private int id;
    private String name;
    private String email;
    private String phone;
    private String skills;
    private BigDecimal experienceYears;
    private String status;
    private Integer jobId;
    private String jobTitle;

    public Candidate() {
    }

    public Candidate(int id, String name, String email,
                     String phone, String skills,
                     BigDecimal experienceYears,
                     String status, Integer jobId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.skills = skills;
        this.experienceYears = experienceYears;
        this.status = status;
        this.jobId = jobId;
    }

    public Candidate(int id, String name, String email,
                     String phone, String skills,
                     BigDecimal experienceYears, String status,
                     Integer jobId, String jobTitle) {
        this(id, name, email, phone, skills, experienceYears, status, jobId);
        this.jobTitle = jobTitle;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public BigDecimal getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(BigDecimal experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getJobId() { return jobId; }
    public void setJobId(Integer jobId) { this.jobId = jobId; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
}
