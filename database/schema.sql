CREATE DATABASE IF NOT EXISTS candidate_management
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE candidate_management;

CREATE TABLE IF NOT EXISTS recruiter (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_recruiter_email (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS jobs (
    id INT NOT NULL AUTO_INCREMENT,
    title VARCHAR(120) NOT NULL,
    department VARCHAR(100) NOT NULL,
    location VARCHAR(120) NOT NULL,
    employment_type VARCHAR(30) NOT NULL,
    description VARCHAR(4000) NOT NULL,
    salary_min DECIMAL(12,2) NULL,
    salary_max DECIMAL(12,2) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    recruiter_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY ix_jobs_status_created (status, created_at),
    CONSTRAINT fk_jobs_recruiter
        FOREIGN KEY (recruiter_id) REFERENCES recruiter (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT chk_jobs_salary_range
        CHECK (salary_min IS NULL OR salary_max IS NULL
               OR salary_min <= salary_max),
    CONSTRAINT chk_jobs_status
        CHECK (status IN ('OPEN', 'CLOSED'))
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS candidates (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(254) NOT NULL,
    phone VARCHAR(30) NULL,
    skills VARCHAR(2000) NULL,
    experience_years DECIMAL(4,1) NOT NULL DEFAULT 0.0,
    status VARCHAR(30) NOT NULL DEFAULT 'New',
    job_id INT NULL,
    PRIMARY KEY (id),
    KEY ix_candidates_status (status),
    KEY ix_candidates_job (job_id),
    KEY ix_candidates_name (name),
    CONSTRAINT fk_candidates_job
        FOREIGN KEY (job_id) REFERENCES jobs (id)
        ON UPDATE CASCADE
        ON DELETE SET NULL,
    CONSTRAINT chk_candidates_experience
        CHECK (experience_years >= 0)
) ENGINE=InnoDB;
