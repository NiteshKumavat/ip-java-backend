package com.candidate.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Job {

    private final int id;
    private final String title;
    private final String department;
    private final String location;
    private final String employmentType;
    private final String description;
    private final BigDecimal salaryMin;
    private final BigDecimal salaryMax;
    private final String status;
    private final Timestamp createdAt;

    public Job(int id, String title, String department, String location,
               String employmentType, String description,
               BigDecimal salaryMin, BigDecimal salaryMax,
               String status, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.department = department;
        this.location = location;
        this.employmentType = employmentType;
        this.description = description;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDepartment() { return department; }
    public String getLocation() { return location; }
    public String getEmploymentType() { return employmentType; }
    public String getDescription() { return description; }
    public BigDecimal getSalaryMin() { return salaryMin; }
    public BigDecimal getSalaryMax() { return salaryMax; }
    public String getStatus() { return status; }
    public Timestamp getCreatedAt() { return createdAt; }
}
