CREATE DATABASE IF NOT EXISTS cyber_forensics;
USE cyber_forensics;

CREATE TABLE IF NOT EXISTS users (
 id INT PRIMARY KEY AUTO_INCREMENT,
 username VARCHAR(50) UNIQUE NOT NULL,
 password VARCHAR(100) NOT NULL,
 role VARCHAR(20) DEFAULT 'USER',
 full_name VARCHAR(100),
 email VARCHAR(100) UNIQUE,
 mobile_number VARCHAR(20),
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cases (
 id INT PRIMARY KEY AUTO_INCREMENT,
 case_number VARCHAR(30) UNIQUE NOT NULL,
 title VARCHAR(150) NOT NULL,
 investigator VARCHAR(100) NOT NULL,
 description VARCHAR(500),
 status VARCHAR(30) DEFAULT 'Open'
);

CREATE TABLE IF NOT EXISTS cybercrime_reports (
 id INT PRIMARY KEY AUTO_INCREMENT,
 report_id VARCHAR(30) UNIQUE NOT NULL,
 user_id INT NOT NULL,
 victim_name VARCHAR(100) NOT NULL,
 email VARCHAR(100) NOT NULL,
 mobile_number VARCHAR(20) NOT NULL,
 crime_category VARCHAR(100) NOT NULL,
 incident_date VARCHAR(50) NOT NULL,
 location VARCHAR(200),
 description TEXT NOT NULL,
 amount_lost VARCHAR(50),
 suspect_name VARCHAR(100),
 suspect_phone VARCHAR(50),
 suspect_email VARCHAR(100),
 wallet_address VARCHAR(150),
 transaction_hash VARCHAR(150),
 website_url VARCHAR(200),
 additional_details TEXT,
 status VARCHAR(50) DEFAULT 'SUBMITTED',
 assigned_unit VARCHAR(100) DEFAULT 'Cyber Crime Cell Unit 1',
 assigned_investigator VARCHAR(100) NULL,
 priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
 investigator_remarks TEXT,
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS evidence (
 id INT PRIMARY KEY AUTO_INCREMENT,
 case_number VARCHAR(30),
 report_id VARCHAR(30),
 evidence_name VARCHAR(150) NOT NULL,
 evidence_type VARCHAR(100) NOT NULL,
 description TEXT,
 file_path VARCHAR(500),
 sha256 VARCHAR(64) NULL,
 status VARCHAR(30) DEFAULT 'Uploaded',
 user_id INT,
 uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_notifications (
 id INT PRIMARY KEY AUTO_INCREMENT,
 user_id INT NOT NULL,
 report_id VARCHAR(30),
 message TEXT NOT NULL,
 is_read BOOLEAN DEFAULT FALSE,
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS admin_audit_logs (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 admin_user_id INT NULL,
 action VARCHAR(100) NOT NULL,
 entity_type VARCHAR(50),
 entity_id VARCHAR(80),
 details TEXT,
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- First-time demo credentials. Existing rows are not overwritten.
INSERT IGNORE INTO users(id,username,password,role,full_name,email,mobile_number)
VALUES (1,'admin','admin123','ADMIN','System Administrator','admin@cyberforensics.gov','9876543210'),
       (2,'user1','user123','USER','John Doe','user1@example.com','9123456789');
