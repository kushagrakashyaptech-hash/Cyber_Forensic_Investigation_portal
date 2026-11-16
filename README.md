# Cyber Forensic Investigation Portal

A web-based Cyber Forensic Investigation Portal designed to help manage cybercrime reports, investigation cases and digital evidence through a centralized platform.

## About the Project

The Cyber Forensic Investigation Portal aims to simplify the management of cybercrime investigations by providing separate interfaces for citizens and administrators.

Citizens can access the portal to submit cybercrime reports and track their complaints, while administrators can manage reports, cases and investigation-related information.

## Key Features

### Citizen Portal
- Citizen registration and login.
- Submit cybercrime complaints.
- Track submitted complaints.
- View complaint status and updates.

### Administrator Portal
- Administrator dashboard.
- Manage citizen complaints.
- Case investigation management.
- Investigator management.
- Evidence management.
- Reports and analytics.
- Export reports as CSV.
- Audit log interface.

## Technology Stack

| Component | Technology |
|---|---|
| Frontend | HTML, CSS, JavaScript |
| Backend | Java |
| Backend Framework | Spring Boot |
| Database | MySQL |
| Build Tool | Gradle |
| Version Control | Git and GitHub |

## Project Structure

```text
Cyber_Forensic_Investigation_Portal/
│
├── web-backend/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/cyberforensic/portal/
│   │       └── resources/
│   │           ├── static/
│   │           │   ├── index.html
│   │           │   └── admin.html
│   │           └── application.properties
│   │
│   ├── database/
│   │   └── schema.sql
│   │
│   ├── build.gradle
│   ├── settings.gradle
│   ├── gradlew
│   └── gradlew.bat
│
└── README.md
```

## Requirements

Before running the project, install:

- Java JDK 17 or later.
- MySQL Server.
- Git.
- A web browser.

The project uses Gradle Wrapper, so a separate Gradle installation is not required.

## Installation and Setup

### 1. Clone the Repository

```bash
git clone https://github.com/kushagrakashyaptech-hash/Cyber_Forensic_Investigation_portal.git
```

### 2. Navigate to the Backend Folder

```bash
cd Cyber_Forensic_Investigation_portal/web-backend
```

### 3. Configure MySQL

Start your MySQL server and configure the database credentials.

Set the environment variables in Windows CMD:

```cmd
set DB_USERNAME=root
set DB_PASSWORD=your_mysql_password
```

Check the database schema and initialize it using the supplied SQL file if required.

### 4. Run the Application

On Windows:

```cmd
gradlew.bat bootRun
```

On Linux or macOS:

```bash
./gradlew bootRun
```

Wait for the Spring Boot application to start successfully.

## Access the Application

Once the server is running, open:

| Portal | URL |
|---|---|
| Citizen Portal | http://localhost:8080/ |
| Administrator Portal | http://localhost:8080/admin.html |

## Database

MySQL is used to store application data, including information related to users, cybercrime reports, investigation cases and digital evidence.

The database schema is provided in the project's database directory.

## Project Objective

The primary objective is to develop a centralized platform that improves the organization and management of cybercrime investigations.

The project focuses on simplifying complaint handling, investigation management and digital evidence tracking.

## Conclusion

The Cyber Forensic Investigation Portal provides a centralized platform for managing cybercrime complaints, investigation cases and digital evidence. By integrating a user-friendly web interface with a Java Spring Boot backend and MySQL database, the system aims to make investigation-related activities more organized, accessible and efficient.

The project establishes a foundation for secure complaint handling, case management and evidence tracking. It also provides scope for future enhancements such as SHA-256 hash verification, detailed chain-of-custody tracking, investigation timeline reconstruction and automated forensic report generation.

Overall, this project demonstrates how technology can support the digital investigation process and contribute towards more systematic cybercrime management.
