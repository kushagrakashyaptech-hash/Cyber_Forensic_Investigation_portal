CYBER FORENSIC INVESTIGATION PORTAL - ADMIN INTEGRATION
=======================================================

WHAT IS INCLUDED
- Citizen portal: http://localhost:8080/
- Web-based administration portal: http://localhost:8080/admin.html
- Shared MySQL database and Spring Boot REST API.
- Complaint register and investigation case workflow.
- Investigator account management and assignment.
- Citizen account/role management.
- Digital evidence register with secure download links.
- Reports/analytics with CSV export.
- Administrative audit log.
- Citizen notifications when an administrator changes a complaint.
- Evidence upload does NOT calculate or require a SHA-256 hash.

REQUIREMENTS
- Java 17 or newer
- MySQL 8.x
- Gradle 8.14.3 (the included Gradle Wrapper can be used; your existing
  C:\Tools\gradle-8.14.3 installation can also run the project)

FIRST-TIME DATABASE SETUP
1. Open MySQL Workbench or MySQL command line.
2. Run the contents of database/schema.sql.
3. The application also adds the new investigation columns and audit table
   when it starts. It changes evidence.sha256 to nullable for older databases.

DATABASE CONFIGURATION (Windows CMD)
Open CMD in the web-backend folder and set your own credentials:
  set DB_USERNAME=root
  set DB_PASSWORD=YOUR_ACTUAL_MYSQL_PASSWORD
Do not put your real password in source code or commit it to GitHub.

START THE BACKEND
From C:\CyberForensicPortal\web-backend:
  C:\Tools\gradle-8.14.3\bin\gradle.bat bootRun

Or use the included wrapper if it is already downloaded:
  gradlew.bat bootRun

Open:
  Citizen portal: http://localhost:8080/
  Admin portal:   http://localhost:8080/admin.html

DEMO ADMIN LOGIN
  Username: admin
  Password: admin123

The first successful login upgrades a legacy plain-text demo password to BCrypt.
Change demo credentials before using this beyond a classroom demonstration.

ADMIN WORKFLOW
1. A citizen registers and submits a complaint from the citizen portal.
2. The complaint immediately appears in the admin Complaint Register.
3. The administrator opens Review, sets status and priority, assigns an
   investigator, and writes a progress update.
4. The backend saves the update in the same complaint row and creates a
   notification for the complainant.
5. The citizen sees the latest status, assigned unit and remarks in Track
   Complaint and Notifications.
6. Evidence files submitted by citizens appear in Digital Evidence and can
   be downloaded by an administrator.

INVESTIGATOR ACCOUNTS
Create investigator accounts from Administration > Investigators. They are
stored in the users table with role INVESTIGATOR and can be assigned to cases.
The current project provides administrator-controlled assignment and tracking;
it does not yet provide a separate investigator login workspace.

NOTES
- Complaint register records are the case records; the report reference is the
  stable case reference shared between both portals.
- Existing sample data is retained.
- Uploaded evidence files are stored in web-backend/evidence-uploads.
- Audit log records administrator actions performed through this portal.
